/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

/**
 * Enum class to represent event's priority.
 */
enum class EventPriority(val priority: Int) {
    NORMAL(0),
    HIGH(1)
}