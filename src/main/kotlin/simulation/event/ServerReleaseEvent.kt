/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment
import simulation.resources.Server

class ServerReleaseEvent(env: Environment, private val server: Server) :
    Event<Server>(env) {
    private val tryReleaseEvent = EventBase(env)

    init {
        tryReleaseEvent.appendCallback { server.tryRelease(this) }
        appendCallback { server.processRelease() }
        env.schedule(tryReleaseEvent)
    }
}