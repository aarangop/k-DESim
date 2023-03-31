/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import simulation.KDESimTestBase
import simulation.event.EventBase
import simulation.event.EventPriority
import simulation.event.Timeout
import simulation.exceptions.EmptySchedule
import simulation.process.Process
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvironmentTest : KDESimTestBase() {
    /**
     * The simulation should stop if there are no more events.
     */
    @Test
    fun `event queue is empty when environment finishes`() {
        val log = mutableListOf<Double>()
        val process = Process(env, sequence {
            while (env.now < 2) {
                log += env.now
                yield(env.timeout(1.0))
            }
        })
        env.schedule(process)
        env.run()

        assertContentEquals(listOf(0.0, 1.0), log)
    }

    @Test
    fun `simulation runs until event termination`() {
        val process = Process(env, sequence {
            while (env.now < 5) {
                yield(env.timeout(1.0))
            }
        })
        val terminationEvent = env.timeout(100.0)
        env.schedule(process)
        env.run(terminationEvent)

        assertEquals(100.0, env.now)
    }

    @Test
    fun `exception thrown with negative until`() {
        assertThrows<IllegalArgumentException> { env.run(-5.0) }
    }

    @Test
    fun `simulation finishes using a termination event`() {
        val terminationEvent = EventBase(env, 10000.0)
        env.schedule(terminationEvent)

        env.run(terminationEvent)

        assertEquals(10000.0, env.now)
    }

    @Test
    fun `schedule sets event scheduled execution time`() {
        val delay = 50.0
        val timeoutEvent = env.timeout(delay)

        env.schedule(timeoutEvent)

        assertEquals(env.now + delay, timeoutEvent.scheduledExecutionTime)
    }

    @Test
    fun `event processed flag is set when event is processed`() {
        val testEvent = EventBase(env, 10.0)

        env.schedule(testEvent)
        env.run()

        assertTrue(testEvent.isProcessed)
    }

    @Test
    fun `single process events are triggered at the right time`() {
        val executionTimes = mutableListOf<Double>()
        val expectedExecutionTimes = listOf(10.0, 20.0)
        val p = Process(env, sequence {
            yield(env.timeout(10.0))
            executionTimes += env.now
            yield(env.timeout(10.0))
            executionTimes += env.now
        })
        env.schedule(p)

        env.run()

        assertContentEquals(expectedExecutionTimes, executionTimes)
    }

    @Test
    fun `events of multiple processes are processed at the right time`() {
        val p1Timeouts = listOf(10.0, 20.0)
        val p2Timeouts = listOf(15.0, 30.0)
        val p1ExpectedTimeouts = listOf(10.0, 30.0)
        val p2ExpectedTimeouts = listOf(15.0, 45.0)
        val p1ExecutionTimes = mutableListOf<Double>()
        val p2ExecutionTimes = mutableListOf<Double>()
        val p1 = Process(env, sequence {
            val it = p1Timeouts.iterator()
            // Wait for 10 seconds before continuing
            yield(env.timeout(it.next()))
            // Simulation time should now be 10
            p1ExecutionTimes += env.now
            yield(env.timeout(it.next()))
            // Simulation time should now be 30 (10 + 20)
            p1ExecutionTimes += env.now
        })
        val p2 = Process(env, sequence {
            val it = p2Timeouts.iterator()
            // Simulation time should be 0
            // Wait for 15 seconds before continuing
            yield(env.timeout(it.next()))
            // Simulation time should now be 15
            p2ExecutionTimes += env.now
            yield(env.timeout(it.next()))
            // Simulation time should now be 45 (15 + 20)
            p2ExecutionTimes += env.now
        })

        env.schedule(p1)
        env.schedule(p2)
        env.run()

        assertAll(
            { assertContentEquals(p1ExpectedTimeouts, p1ExecutionTimes) },
            { assertContentEquals(p2ExpectedTimeouts, p2ExecutionTimes) }
        )
    }

    @Test
    fun `environment finishes with until event`() {
        val terminationEvent = Timeout(env, 10.0)
        env.schedule(terminationEvent)
        env.run(terminationEvent)
        assertAll(
            { assertEquals(10.0, env.now) },
            { assertEquals(true, terminationEvent.isProcessed) }
        )
    }

    @Test
    fun `environment throws EmptySchedule exception if termination event is not scheduled by the user`() {
        val terminationEvent = Timeout(env, 10.0)
        assertThrows<EmptySchedule> {
            env.run(terminationEvent)
        }
    }

    @Test
    fun `environment finishes with until process`() {
        // TODO: write this test
    }

    @Test
    fun `environment's event queue respects the event priority`() {
        var eventWithDibs: Int? = null
        fun callDibs(event: EventBase) {
            if (eventWithDibs == null) {
                eventWithDibs = event.id
            }
        }

        val highPriorityEvent = EventBase(env, 10.0, EventPriority.HIGH)
        val normalPriorityEvent = EventBase(env, 10.0, EventPriority.NORMAL)
        highPriorityEvent.appendCallback { event: EventBase -> callDibs(event) }
        normalPriorityEvent.appendCallback { event: EventBase -> callDibs(event) }

        env.schedule(normalPriorityEvent, highPriorityEvent)
        env.run()

        assertEquals(highPriorityEvent.id, eventWithDibs)
    }
}