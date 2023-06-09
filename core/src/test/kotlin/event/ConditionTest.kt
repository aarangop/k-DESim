/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import org.junit.jupiter.api.Test
import simulation.KDESimTestBase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConditionTest : KDESimTestBase() {
    @Test
    fun `AllOfCondition triggers when all events have been triggered`() {
        val timeouts = Array(2) {
            env.timeout(10.0)
        }
        val allConditionFulfilled = env.schedule(AllOfCondition(env, *timeouts))

        env.run(allConditionFulfilled)
        assertTrue(timeouts.all { it.isProcessed })
    }

    @Test
    fun `AnyOfCondition triggers when one event has been triggered`() {
        val timeout1 = env.timeout(10.0)
        val timeout2 = env.timeout(20.0)

        val anyOfConditionFulfilled = env.schedule(AnyOfCondition(env, timeout1, timeout2))

        env.run(anyOfConditionFulfilled)
        assertTrue(timeout1.isProcessed && !timeout2.isProcessed)
    }

    @Test
    fun `AnyOfCondition returns event that triggered condition`() {
        val timeout2 = env.timeout(20.0)
        val event = ValueEvent<String>(env)
        val anyOfCondition = AnyOfCondition(env, event, timeout2)

        env.schedule(anyOfCondition)
        env.process(sequence {
            yield(env.timeout(10.0))
            event.succeed("Success")
        })

        env.run()

        assertEquals("Success", anyOfCondition.getConditionValue())
    }
}