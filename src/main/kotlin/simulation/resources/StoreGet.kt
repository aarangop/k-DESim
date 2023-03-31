/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import simulation.core.Environment
import simulation.event.Event
import simulation.event.EventBase

class StoreGet<T>(env: Environment, private val resource: Store<T>, val quantity: Int = 1) : Event<T>(env) {
    private val tryGetEvent: EventBase = EventBase(env)

    init {
        tryGetEvent.appendCallback { resource.tryGet(this) }
        env.schedule(tryGetEvent)
    }
}