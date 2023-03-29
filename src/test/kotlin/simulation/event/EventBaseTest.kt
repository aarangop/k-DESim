/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simulation.KDESimTestBase

class EventBaseTest : KDESimTestBase() {

    @Test
    fun `an event's value is set when it succeeds`() {
        val event = Event<String>(this@EventBaseTest.env, 10.0)
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
    fun `scheduling an event that has already been triggered throws an exception`() {
        // todo
    }
}