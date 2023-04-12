/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment

class AnyOfCondition(env: Environment, vararg events: Event) : Condition(env, *events) {
    private var triggeringEvent: Event? = null

    override fun processConditionEvent(event: Event) {
        val triggeredEvent = events.filter { it.isTriggered }
        if (triggeredEvent.isNotEmpty()) {
            this.succeed(triggeredEvent[0])
            triggeringEvent = triggeredEvent[0]
        }
    }

    fun getConditionValue(): Any? {
        if (triggeringEvent is ValueEvent<*>) {
            return (triggeringEvent as ValueEvent<*>).value()
        }
        return null
    }
}