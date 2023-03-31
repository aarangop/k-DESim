/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment
import simulation.resources.Store

class StoreGet<T>(env: Environment, private val resource: Store<T>) : Event<T>(env) {
    private val tryGetEvent: EventBase = EventBase(env)

    init {
        tryGetEvent.appendCallback { resource.tryGet(this) }
        tryGetEvent.appendCallback { resource.processGet() }
        env.schedule(tryGetEvent)
    }
}