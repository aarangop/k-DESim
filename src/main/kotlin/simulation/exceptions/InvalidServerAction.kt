/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.exceptions

import simulation.event.EventBase
import simulation.event.ServerRequestEvent

class InvalidServerAction(
    activeRequest: ServerRequestEvent?,
    callingScope: SequenceScope<EventBase>
) :
    Throwable(
        "Attempted to run a function from $callingScope but the ${activeRequest?.scope} is blocking the Server."
    )
