/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

import simulation.core.Environment
import simulation.event.EventBase
import simulation.resources.ServerRequestEvent

open class ResourceBase(val env: Environment) {
    open fun request(): EventBase {
        throw NotImplementedError()
    }

    open fun release(): EventBase {
        throw NotImplementedError()
    }

    open fun request(scope: SequenceScope<EventBase>): ServerRequestEvent {
        throw NotImplementedError()
    }
}