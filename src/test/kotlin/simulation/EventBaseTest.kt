/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventBaseTest {
    val env: Environment = Environment()

    @Test
    fun `an event's value is set when it succeeds`() {
        val event = Event<String>(env, 10.0)
        var eventValue: String? = null
        var expectedEventValue = "Success!!"

        val eventTriggeringProcess = Process(env, sequence {
            yield(env.schedule(env.timeout(10.0)))
            event.succeed(expectedEventValue)
        })

        val eventReceivingProcess = Process(env, sequence {
            yield(event)
            eventValue = event.value()
        })

        env.process(eventReceivingProcess, eventTriggeringProcess)
        env.run()

        assertEquals(expectedEventValue, eventValue)
    }
}