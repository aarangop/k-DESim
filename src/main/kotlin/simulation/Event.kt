/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

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

    fun value(): T? {
        return if (value.status == EventValueStatus.AVAILABLE) {
            value.value
        } else {
            null
        }
    }
}