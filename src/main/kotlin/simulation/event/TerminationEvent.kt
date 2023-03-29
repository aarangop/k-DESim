/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment
import simulation.exceptions.StopSimulationException

class TerminationEvent(env: Environment, timeout: Double = 0.0) : EventBase(env, timeout, EventPriority.HIGH) {
    init {
        appendCallback { terminationCallback() }
    }

    private fun terminationCallback() {
        throw StopSimulationException(this)
    }

    companion object {
        fun fromEvent(event: EventBase): TerminationEvent {
            return TerminationEvent(event.env, event.timeout)
        }
    }
}