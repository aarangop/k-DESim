/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.exceptions

import simulation.event.EventBase

class StopSimulationException(val event: EventBase) : Throwable()