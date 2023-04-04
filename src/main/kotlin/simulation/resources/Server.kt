/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import simulation.core.Environment
import simulation.event.EventBase
import simulation.event.ServerReleaseEvent
import simulation.event.ServerRequestEvent
import simulation.exceptions.InvalidServerAction
import simulation.process.Process


/**
 * The server class represents a resource which can be blocked and released within a process.
 *
 * The server class is intended to be subclassed. Server functions can be 'decorated' with the `serverAction` method,
 * which ensures that an exception is thrown if the server is being used by a process that has not been granted access
 * to the server.
 */
open class Server(val env: Environment) {
    private var isBlocked = false
    private var requestQueue: ArrayDeque<ServerRequestEvent> = ArrayDeque()
    private var activeRequest: ServerRequestEvent? = null

    /**
     * `serverAction` is a decorator for actions that can be performed by subclasses of `Server`. It receives the scope
     * from which the function is called and a function to be executed.
     *
     * The function is only executed if the scope matches the scope of the active requests. This mechanism enforces that
     * server functions are only called by processes that have been granted access to the `Server`.
     *
     * @param scope Current process scope
     */
    fun serverAction(scope: SequenceScope<EventBase>, fn: () -> Sequence<EventBase>): Process {
        // Check that the provided scope matches the active request's scope.
        if (activeRequest?.hasSameScope(scope) == true) {
            val process = Process(env, fn())
            env.schedule(process)
            return process
        } else {
            throw InvalidServerAction(activeRequest, scope)
        }
    }


    /**
     * The `request` method makes an attempt to block the Server.
     *
     * The `request` method returns an event that a process can wait for (yield). After the event is processed, the
     * server is available within the process where it was requested.
     *
     * @return An event associated with the request.
     */
    fun request(scope: SequenceScope<EventBase>): ServerRequestEvent {
        return ServerRequestEvent(this.env, this, scope)
    }

    /**
     * Release the server and return an event associated with the server's release. Once the event is processed, the
     * server is free again.
     *
     * After the server is released the event associated with the original server's request is expired.
     *
     * After the server is released the next queued request will be processed.
     *
     * @return An event associated with the release.
     */
    fun release(): ServerReleaseEvent {
        return ServerReleaseEvent(this.env, this)
    }

    /**
     * The `processRequest` method is called after a request event has been triggered.
     *
     * During `processRequest` the `Server` is marked as blocked and the request that blocked it is saved as the
     * currently active request.
     *
     * @param request Event that triggered the call to `processRequest`.
     */
    internal fun processRequest(request: ServerRequestEvent) {
        // Set the request as the active request.
        activeRequest = request
        isBlocked = true
    }

    /**
     * Release the `Server` by marking it as unblocked, expire the request event that requested the `Server` and unset
     * the `activeRequest`.
     *
     * If there are queued requests, try to process the next one.
     */
    internal fun processRelease() {
        // Expire the event associated with the request.
        activeRequest?.expire()
        // Unset the active request.
        activeRequest = null
        isBlocked = false
        // After the server is released, check if there are queued requests and process the next one.
        if (!requestQueue.isEmpty()) {
            val nextRequest = requestQueue.removeFirst()
            tryRequest(nextRequest)
        }
    }

    /**
     * Try to fulfill a request for a server. If the server is currently blocked, queue the request.
     *
     * @param request Server request event.
     */
    internal fun tryRequest(request: ServerRequestEvent) {
        // Check if the server is blocked
        if (isBlocked) {
            // Server is blocked.
            // Append the request to the requestQueue.
            requestQueue.add(request)
        } else {
            request.succeed(this)
        }
    }

    /**
     * Try to release the server. The associated release request should be processed immediately.
     *
     * @param request Release request event.
     */
    fun tryRelease(request: ServerReleaseEvent) {
        request.succeed(this)
    }
}