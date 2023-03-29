/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment

/**
 * The event class represents an event that can be triggered and processed by the environment. It is a generic class and
 * can hold values of the declared type which are set once the event is triggered.
 *
 * @param env Environment to which this event is associated
 * @param timeout Timeout to schedule event. Defaults to 0.0
 */
class Event<T>(env: Environment, timeout: Double = 0.0) : EventBase(env, timeout) {
    private var value: EventValue<T> = EventValue()

    /**
     * The `Event.succeed` function causes the event to be scheduled immediately. If an event value is provided it will
     * become available once the event has been successfully processed.
     *
     * @param value: T: Value to assign to the event once it has been processed
     */
    fun succeed(value: T?) {
        // Set the value of the event.
        this.value.value = value
        this.value.status = EventValueStatus.AVAILABLE
        // Call base class' succeed, which schedules the event
        super.succeed()
    }

    /**
     * Access the value of the event. Returns null if the value is not yet available.
     *
     * @return Value associated to the event or null if the value is not yet available.
     */
    fun value(): T? {
        return if (value.status == EventValueStatus.AVAILABLE) {
            value.value
        } else {
            null
        }
    }
}