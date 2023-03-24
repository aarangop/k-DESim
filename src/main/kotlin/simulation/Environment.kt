package simulation

import java.util.*

class Environment(var now: Double = 0.0) {
    var eventQueue = PriorityQueue<Event> { t1: Event, t2: Event ->
        if (t1.timeout == t2.timeout)
            (t1.priority.priority - t2.priority.priority)
        else (t1.timeout - t2.timeout).toInt()
    }

    fun run(timeout: Double = 0.0) {
        require(timeout >= 0) { "Timeout must be greater than or equal to zero! " }
        schedule(Event(this, timeout))
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
            now += nextEvent.timeout
            nextEvent.processEvent()
        }
    }

    fun schedule(event: Event) {
        eventQueue.add(event)
    }
}