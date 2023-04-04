/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import ResourceBase
import simulation.core.Environment
import simulation.event.Event
import simulation.event.EventBase
import simulation.exceptions.InvalidServerAction
import simulation.process.Process


class ServerRequestEvent(env: Environment, private val server: Server, val scope: SequenceScope<EventBase>) :
    Event<Server>(env) {

    fun hasSameScope(actionScope: SequenceScope<EventBase>): Boolean {
        return scope == actionScope
    }

    private val tryRequestEvent = EventBase(env)

    init {
        tryRequestEvent.appendCallback { server.tryRequest(this) }
        appendCallback { server.processRequest(this) }
        env.schedule(tryRequestEvent)
    }
}

class ServerReleaseEvent(env: Environment, val server: Server) : Event<Server>(env) {
    private val tryReleaseEvent = EventBase(env)

    init {
        tryReleaseEvent.appendCallback { server.tryRelease(this) }
        appendCallback { server.processRelease() }
        env.schedule(tryReleaseEvent)
    }
}

/**
 * The server class represents a resource which can be blocked and released by another simulation entity.
 *
 * The server class is supposed to be subclassed.
 */
open class Server(env: Environment) : ResourceBase(env) {
    private var isBlocked = false
    private var requestQueue: ArrayDeque<ServerRequestEvent> = ArrayDeque()
    private var activeRequest: ServerRequestEvent? = null

    /**
     * `serverAction` is a decorator for actions that can be performed by subclasses of `Server`. It receives the scope
     * from which the function is called and a function to be executed.
     *
     * The function is only executed if the scope matches the scope of the active requests. This mechanism enforces that
     * server functions are only called by processes who currently own the Server.
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
     * The `request` method makes an attempt to block the Server. It returns an event, which holds the server instance
     * as value when processed.
     *
     * @return An event associated with the request. After the event is processed, the server will be available through
     * the event's value.
     */
    override fun request(scope: SequenceScope<EventBase>): ServerRequestEvent {
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
    override fun release(): ServerReleaseEvent {
        return ServerReleaseEvent(this.env, this)
    }

    internal fun processRequest(request: ServerRequestEvent) {
        // Set the request as the active request.
        activeRequest = request
    }

    internal fun processRelease() {
        // Expire the event associated with the request.
        activeRequest?.expire()
        // Unset the active request.
        activeRequest = null
        // After the server is released, check if there are queued requests and process the next one.
        if (!requestQueue.isEmpty()) {
            val nextRequest = requestQueue.removeFirst()
            tryRequest(nextRequest)
        }
    }

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

    fun tryRelease(request: ServerReleaseEvent) {
        isBlocked = false
        request.succeed(this)
    }
}