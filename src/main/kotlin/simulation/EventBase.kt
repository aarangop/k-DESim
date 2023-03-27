/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import simulation.EventPriority.NORMAL


/**
 * The EventBase class represents an event that can be scheduled and executed by the simulation environment.
 */
open class EventBase(
    val env: Environment,
    val timeout: Double? = null,
    val priority: EventPriority = NORMAL
) {
    var isTriggered: Boolean = false
    var isProcessed: Boolean = false
    var scheduledExecutionTime: Double = 0.0
    private var eventId: Int? = null

    init {
        require((timeout == null) || (timeout >= 0)) { "Timeout for event must be null or positive." }
    }

    private var callbacks: Array<(EventBase) -> Unit> = emptyArray()

    open fun action() {}

    /**
     * The `Event.succeed` function causes the event to be scheduled and processed immediately.
     */
    open fun succeed() {
        env.schedule(this)
    }

    /**
     * The `Event.processEvent` function iterates through the callbacks assigned to the event and executes them.
     * Before executing the callbacks the isTriggered flag is set to true.
     * When all callbacks have been executed the isProcessed flag is set to true.
     */
    internal open fun processEvent() {
        isTriggered = true
        for (c in callbacks) {
            c(this)
        }
        isProcessed = true
    }

    /**
     * Adds a callback to the event's callback list. These will be executed when the event is triggered.
     */
    open fun addCallback(fn: (EventBase) -> Unit) {
        callbacks += fn
    }

    internal fun setId(id: Int) {
        eventId = id
    }

    open fun fail() {

    }
}