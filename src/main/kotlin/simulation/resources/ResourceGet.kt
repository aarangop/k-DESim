/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import simulation.core.Environment
import simulation.event.Event

class ResourceGet<T>(env: Environment) : Event<T>(env) {

    private fun triggerGet() {

    }

    init {
        appendCallback { triggerGet() }
        env.schedule(this)
    }
}