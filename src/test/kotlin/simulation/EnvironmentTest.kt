/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvironmentTest {
    val testEnv: Environment = Environment()

    @Test
    fun testEnvironmentTimeIsAdvancedToNextEventTime() {
        val expectedTime = 10.0
        val sampleEvent = Event(testEnv, expectedTime)

        testEnv.schedule(sampleEvent)
        testEnv.run()

        assertEquals(expectedTime, testEnv.now)
    }

    @Test
    fun testSimulationFinishesusingTerminationEvent() {
        val terminationEvent = Event(testEnv, 10000.0)

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
        val testEvent = Event(testEnv, 10.0)

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
}