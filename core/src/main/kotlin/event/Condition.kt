/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment

abstract class Condition(env: Environment, vararg val events: Event) : ValueEvent<Event>(env) {

    init {
        for (event in events) {
            event.appendCallback { processConditionEvent(event) }
        }
    }

    abstract fun processConditionEvent(event: Event)
}