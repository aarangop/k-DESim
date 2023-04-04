/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment
import simulation.event.EventPriority.NORMAL
import simulation.exceptions.EventFailedException


/**
 * The `Event` class represents an event that can be scheduled and executed by the simulation environment.
 * It is the basic construct, along with the environment upon which a discrete event simulation is built.
 *
 * An event:
 * - may happen (`isTriggered` is false),
 * - will happen (`isTriggered` is true) or,
 * - has happened (`isProcessed` is true)
 *
 * Every event is associated to an `Environment` *env* and is initially not triggered.
 *
 * The processing of an event can be triggered by scheduling the event on the environment.
 *
 * Callbacks can be bound to the event, which will be called when the event is processed. The event will be passed to
 * the individual callbacks.
 *
 * TODO: Fail events. An event can be failed. When an event fails it is not silently ignored and an exception is raised.
 * TODO: A callback that processes a failed event must set its status to defused to prevent crashing the program due to the exception.
 */
open class Event(
    val env: Environment,
    val timeout: Double = 0.0,
    val priority: EventPriority = NORMAL
) {
    var isTriggered = false
        protected set

    var isProcessed = false
        protected set

    var status: EventStatus = EventStatus.OKAY

    var scheduledExecutionTime = 0.0
        internal set

    init {
        require(timeout >= 0) { "Timeout for event must be null or positive." }
    }

    var id: Int? = null
        private set

    protected var callbacks: Array<(Event) -> Unit> = emptyArray()

    /**
     * Schedule the event immediately.
     */
    open fun succeed() {
        env.schedule(this)
    }

    /**
     * The `Event.processEvent` function iterates through the callbacks assigned to the event and executes them.
     *
     * Before executing the callbacks the isTriggered flag is set to true.
     *
     * When all callbacks have been executed the isProcessed flag is set to true.
     */
    internal open fun processEvent() {
        isTriggered = true
        for (callback in callbacks) {
            callback(this)
        }
        isProcessed = true
    }

    /**
     * Appends a callback to the event's callback list.
     *
     * @param fn Callback to be appended to the event's callback list.
     */
    open fun appendCallback(fn: (Event) -> Unit) {
        callbacks += fn
    }

    /**
     * Signal that the event failed. TODO how to do exception handling for failed events?
     */
    open fun fail() {
        throw EventFailedException(this)
    }

    internal fun setId(id: Int) {
        this.id = id
    }
}