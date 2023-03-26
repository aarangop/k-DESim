/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import java.util.*

class Environment(var now: Double = 0.0) {
    var eventQueue = PriorityQueue<Event> { t1: Event, t2: Event ->
        if (t1.timeout == t2.timeout)
            (t1.priority.priority - t2.priority.priority)
        else (t1.scheduledExecutionTime - t2.scheduledExecutionTime).toInt()
    }

    fun run(timeout: Double = 1000.0) {
        require(timeout > 0) { "Timeout must be greater than or equal to zero! " }
        schedule(Event(this, timeout, EventPriority.HIGH))
        run()
    }

    fun run(terminationEvent: Event) {
        schedule(terminationEvent)
        run()
    }

    fun timeout(timeout: Double): Timeout {
        return Timeout(this, timeout)
    }

    private fun run() {
        while (!eventQueue.isEmpty()) {
            val nextEvent = eventQueue.remove()
            now = nextEvent.scheduledExecutionTime
            nextEvent.processEvent()
        }
    }

    fun schedule(event: Event) {
        event.scheduledExecutionTime = now + event.timeout
        eventQueue.add(event)
    }
}