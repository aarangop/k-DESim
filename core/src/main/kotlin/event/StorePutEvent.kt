/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment
import resources.Store

class StorePutEvent<T>(env: Environment, private val resource: Store<T>, internal val item: T) : ValueEvent<T>(env) {
    private val tryPutEvent: Event = Event(env)

    init {
        tryPutEvent.appendCallback { resource.tryPut(this) }
        tryPutEvent.appendCallback { resource.processPut() }
        env.schedule(tryPutEvent)
    }
}