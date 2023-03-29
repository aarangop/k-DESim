/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import org.junit.jupiter.api.assertAll
import simulation.KDESimTestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessTest : KDESimTestBase() {
    @Test
    fun `process isAlive is true while its sequence has not terminated`() {
        var p1IsAlive = false
        val p1 = Process(env, sequence {
            yield(env.timeout(10.0))
        })
        val p2 = Process(env, sequence {
            yield(env.timeout(5.0))
            p1IsAlive = p1.isAlive
        })
        env.process(p1, p2)
        env.run()

        assertEquals(true, p1IsAlive)
    }

    @Test
    fun `isAlive is false after process has terminated`() {
        val p1 = Process(env, sequence {
            yield(env.timeout(10.0))
        })
        env.process(p1)
        env.run()

        assertEquals(false, p1.isAlive)
    }

    @Test
    fun `isProcessed is false while process is running`() {
        var isProcessed = true
        val p1 = Process(env, sequence {
            yield(env.timeout(10.0))
        })
        val p2 = Process(env, sequence {
            yield(env.timeout(5.0))
            isProcessed = p1.isProcessed
        })
        env.process(p1, p2)
        env.run()

        assertEquals(false, isProcessed)
    }

    @Test
    fun `isProcessed is true when process has been processed`() {
        val p1 = Process(env, sequence {
            yield(env.timeout(100.0))
        })
        env.process(p1)
        env.run()

        assertEquals(true, p1.isProcessed)
    }

    /**
     * The simulation should work for nested processes, e.g. process which yield another process.
     */
    @Test
    fun `test nested processes`() {
        val p1 = Process(env, sequence {
            println("p1 starting")
            yield(env.timeout(10.0))
            println("p1 finished waiting")
        })
        val p2 = Process(env, sequence {
            println("p2 starting")
            yield(env.timeout(10.0))
            println("p2 finished waiting, will wait for p1 to finish")
            yield(env.process(p1))
            println("p1 and p2 finished")
        })

        env.process(p2)
        env.run(p2)

        assertAll(
            { assertEquals(20.0, env.now) },
            { assertEquals(true, p1.isProcessed) },
            { assertEquals(true, p2.isProcessed) }
        )
    }
}