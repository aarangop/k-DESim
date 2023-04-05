/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment
import resources.Store

class StoreGetEvent<T>(env: Environment, private val resource: Store<T>) : ValueEvent<T>(env) {
    private val tryGetEvent: Event = Event(env)

    init {
        tryGetEvent.appendCallback { resource.tryGet(this) }
        tryGetEvent.appendCallback { resource.processGet() }
        env.schedule(tryGetEvent)
    }
}