package simulation

import simulation.EventPriority.NORMAL


open class Event(
    val env: Environment,
    val timeout: Double = 0.0,
    val priority: EventPriority = NORMAL
) {
    var isTriggered: Boolean = false
    var isProcessed: Boolean = false

    init {
        require(timeout >= 0) { "Timeout for event cannot be negative." }
    }

    private var callbacks: Array<(Event) -> Unit> = emptyArray()

    open fun action() {}
    internal open fun processEvent() {
        isTriggered = true
        action()
        for (c in callbacks) {
            c(this)
        }
        isProcessed = true
    }

    open fun addCallback(fn: (Event) -> Unit) {
        callbacks += fn
    }
}