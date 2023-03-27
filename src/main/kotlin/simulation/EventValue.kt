/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

class EventValue<T>(var status: EventValueStatus = EventValueStatus.PENDING, var value: T? = null)