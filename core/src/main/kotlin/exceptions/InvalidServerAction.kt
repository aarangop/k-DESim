/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package exceptions

import event.Event
import event.ServerRequestEvent

class InvalidServerAction(
    activeRequest: ServerRequestEvent?,
    callingScope: SequenceScope<Event>
) :
    Throwable(
        "Attempted to run a function from $callingScope but the ${activeRequest?.scope} is blocking the Server."
    )
