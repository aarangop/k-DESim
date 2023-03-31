/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import simulation.core.Environment
import simulation.event.Event
import simulation.event.EventBase

class StorePut<T>(env: Environment, private val resource: Store<T>, item: T) : Event<T>(env) {
    private val tryPutEvent: EventBase = EventBase(env)

    init {
        tryPutEvent.appendCallback { resource.tryPut(this, item) }
        env.schedule(tryPutEvent)
    }
}