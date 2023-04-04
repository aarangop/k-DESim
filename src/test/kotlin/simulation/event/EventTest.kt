/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simulation.KDESimTestBase
import simulation.process.Process

class EventTest : KDESimTestBase() {

    @Test
    fun `an event's value is set when it succeeds`() {
        val event = ValueEvent<String>(this@EventTest.env, 10.0)
        var eventValue: String? = null
        val expectedEventValue = "Success!!"

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

    @Test
    fun `an event's value is null before it succeeds`() {
        val event = ValueEvent<String>(env, 10.0)
        val process = sequence {
            assertEquals(null, event.value())
            yield(env.schedule(event))
        }
        env.process(process)
        env.run(event)
    }
}