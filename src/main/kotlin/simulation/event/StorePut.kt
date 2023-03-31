/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment
import simulation.resources.Store

class StorePut<T>(env: Environment, private val resource: Store<T>, internal val item: T) : Event<T>(env) {
    private val tryPutEvent: EventBase = EventBase(env)

    init {
        tryPutEvent.appendCallback { resource.tryPut(this) }
        tryPutEvent.appendCallback { resource.processPut() }
        env.schedule(tryPutEvent)
    }
}