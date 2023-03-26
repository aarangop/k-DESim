package simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventBaseTest {
    val env: Environment = Environment()

    @Test
    fun accessValue() {
        // Create an event
        val event = EventBase(env, 10.0)
        // Append a callback to the event that returns a value
        event.addCallback {
            //            return "Event Value"
        }
        // Create variable to store the event's return value
        val eventValue = "None"
        // Create a process that waits for the event to be processed
        val process = Process(env, sequence {
            // eventValue = yield(event)
        })
        env.schedule(process)
        env.run()

        assertEquals("Event Value", eventValue)
    }

}