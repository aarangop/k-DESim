/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment
import simulation.resources.Server

class ServerRequestEvent(env: Environment, private val server: Server, internal val scope: SequenceScope<EventBase>) :
    Event<Server>(env) {

    fun hasSameScope(actionScope: SequenceScope<EventBase>): Boolean {
        return scope == actionScope
    }

    private val tryRequestEvent = EventBase(env)

    init {
        tryRequestEvent.appendCallback { server.tryRequest(this) }
        appendCallback { server.processRequest(this) }
        env.schedule(tryRequestEvent)
    }
}