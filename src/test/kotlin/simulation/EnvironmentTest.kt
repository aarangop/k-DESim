/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import simulation.core.Environment
import simulation.event.EventBase
import simulation.event.Process
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvironmentTest {
    val testEnv: Environment = Environment()

    /**
     * The simulation should stop if there are no more events.
     */
    @Test
    fun `test event queue is empty when environment finishes`() {
        var log = emptyList<Double>()
        val process = Process(testEnv, sequence {
            while (testEnv.now < 2) {
                log += testEnv.now
                yield(testEnv.timeout(1.0))
            }
        })
        testEnv.schedule(process)
        testEnv.run()

        assertContentEquals(listOf(0.0, 1.0), log)
    }

    @Test
    fun `simulation runs until event termination`() {
        val process = Process(testEnv, sequence {
            while (testEnv.now < 5) {
                yield(testEnv.timeout(1.0))
            }
        })
        val terminationEvent = testEnv.timeout(100.0)
        testEnv.schedule(process)
        testEnv.run(terminationEvent)

        assertEquals(100.0, testEnv.now)
    }

    @Test
    fun `exception thrown with negative until`() {
        assertThrows<IllegalArgumentException> { testEnv.run(-5.0) }
    }

    @Test
    fun testSimulationFinishesusingTerminationEvent() {
        val terminationEvent = EventBase(testEnv, 10000.0)

        testEnv.run(terminationEvent)

        assertEquals(10000.0, testEnv.now)
    }

    @Test
    fun testScheduleSetsEventScheduledExecutionTime() {
        val delay = 50.0
        val timeoutEvent = testEnv.timeout(delay)

        testEnv.schedule(timeoutEvent)

        assertEquals(testEnv.now + delay, timeoutEvent.scheduledExecutionTime)
    }

    @Test
    fun testEventProcessedFlagIsSetWhenEventIsProcessed() {
        val testEvent = EventBase(testEnv, 10.0)

        testEnv.schedule(testEvent)
        testEnv.run()

        assertTrue(testEvent.isProcessed)
    }

    @Test
    fun testSingleProcessEventsAreTriggeredAtTheRightTime() {
        val executionTimes = mutableListOf<Double>()
        val expectedExecutionTimes = listOf<Double>(10.0, 20.0)
        val p = Process(testEnv, sequence {
            yield(testEnv.timeout(10.0))
            executionTimes += testEnv.now
            yield(testEnv.timeout(10.0))
            executionTimes += testEnv.now
        })
        testEnv.schedule(p)

        testEnv.run()

        assertContentEquals(expectedExecutionTimes, executionTimes)
    }

    @Test
    fun testEventsOfMultipleProcessesAreProcessedAtTheRightTime() {
        val p1Timeouts = listOf(10.0, 20.0)
        val p2Timeouts = listOf(15.0, 30.0)
        val p1ExpectedTimeouts = listOf(10.0, 30.0)
        val p2ExpectedTimeouts = listOf(15.0, 45.0)
        val p1ExecutionTimes = mutableListOf<Double>()
        val p2ExecutionTimes = mutableListOf<Double>()
        val p1 = Process(testEnv, sequence {
            val it = p1Timeouts.iterator()
            // Wait for 10 seconds before continuing
            yield(testEnv.timeout(it.next()))
            // Simulation time should now be 10
            p1ExecutionTimes += testEnv.now
            yield(testEnv.timeout(it.next()))
            // Simulation time should now be 30 (10 + 20)
            p1ExecutionTimes += testEnv.now
        })
        val p2 = Process(testEnv, sequence {
            val it = p2Timeouts.iterator()
            // Simulation time should be 0
            // Wait for 15 seconds before continuing
            yield(testEnv.timeout(it.next()))
            // Simulation time should now be 15
            p2ExecutionTimes += testEnv.now
            yield(testEnv.timeout(it.next()))
            // Simulation time should now be 45 (15 + 20)
            p2ExecutionTimes += testEnv.now
        })

        testEnv.schedule(p1)
        testEnv.schedule(p2)
        testEnv.run()

        assertAll(
            { assertContentEquals(p1ExpectedTimeouts, p1ExecutionTimes) },
            { assertContentEquals(p2ExpectedTimeouts, p2ExecutionTimes) }
        )
    }

    // TODO: Test event priority. From two events scheduled at the same time the one with the highest priority has to be triggered first.
}