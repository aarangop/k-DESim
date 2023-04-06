/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment

class AllOfCondition(env: Environment, vararg events: Event) : Condition(env, *events) {
    override fun processConditionEvent(event: Event) {
        if (events.all { it.isTriggered }) {
            this.succeed()
        }
    }
}