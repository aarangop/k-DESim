/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import simulation.EventPriority.NORMAL


open class Event(
    val env: Environment,
    val timeout: Double = 0.0,
    val priority: EventPriority = NORMAL
) {
    var isTriggered: Boolean = false
    var isProcessed: Boolean = false
    var scheduledExecutionTime: Double = 0.0

    init {
        require(timeout >= 0) { "Timeout for event cannot be negative." }
    }

    private var callbacks: Array<(Event) -> Unit> = emptyArray()

    open fun action() {}
    internal open fun processEvent() {
        isTriggered = true
        for (c in callbacks) {
            c(this)
        }
        isProcessed = true
    }

    open fun addCallback(fn: (Event) -> Unit) {
        callbacks += fn
    }
}