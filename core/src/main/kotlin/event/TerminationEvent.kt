/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment
import exceptions.StopSimulationException

class TerminationEvent(env: Environment, timeout: Double = 0.0) : Event(env, timeout, EventPriority.HIGH) {
    init {
        appendCallback { terminationCallback() }
    }

    private fun terminationCallback() {
        throw StopSimulationException(this)
    }
}