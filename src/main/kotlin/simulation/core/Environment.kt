/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.core

import simulation.event.EventBase
import simulation.event.EventPriority
import simulation.event.Process
import simulation.event.Timeout
import java.util.*

/**
 * The Environment class is the main class for discrete simulation. It keeps track of the simulation time and is in charge of processing events.
 *
 * @param now Initial simulation time
 */
class Environment(var now: Double = 0.0) {
    private var eventQueue = PriorityQueue { t1: EventBase, t2: EventBase ->
        if (t1.timeout == t2.timeout)
            (t1.priority.priority - t2.priority.priority)
        else (t1.scheduledExecutionTime - t2.scheduledExecutionTime).toInt()
    }

    init {
        require(now >= 0) { "The initial simulation time must be positive." }
    }

    companion object {
        private var id: Int = -1
        fun nextEventId(): Int {
            id += 1
            return id
        }
    }

    private class TerminationEvent(env: Environment, timeout: Double) : EventBase(env, timeout, EventPriority.HIGH)

    /**
     * Run the simulation until a timeout event based on the provided timeout is triggered.
     *
     * @param timeout Timeout of the simulation termination event.
     */
    fun run(timeout: Double = 1000.0) {
        require(timeout > 0) { "Timeout must be greater than or equal to zero! " }
        schedule(TerminationEvent(this, timeout))
        simulationLoop()
    }

    /**
     * Run the simulation until the termination event is triggered.
     *
     * @param terminationEvent Event, which, when triggered, ends the simulation.
     *
     */
    fun run(terminationEvent: EventBase) {
        schedule(terminationEvent)
        simulationLoop()
    }

    /**
     * Schedules a timeout event
     *
     * @param timeout Timeout.
     *
     * @return The scheduled timeout event.
     */
    fun timeout(timeout: Double): Timeout {
        val timeoutEvent = Timeout(this, timeout)
        schedule(timeoutEvent)
        return timeoutEvent
    }

    /**
     * Schedules a process on the simulation environment.
     *
     * @param process Process to be scheduled by the environment.
     */
    fun process(process: Process): EventBase {
        schedule(process)
        return process
    }

    /**
     * Schedules multiple processes on the simulation environment.
     *
     * @param processes List of processes to be scheduled by the environment.
     *
     * @return List of events associated with the termination of the provided processes.
     */
    fun process(vararg processes: Process): List<EventBase> {
        val processFinishedEvents = mutableListOf<EventBase>()
        for (process in processes) {
            schedule(process)
            processFinishedEvents += process
        }
        return processFinishedEvents
    }

    /**
     * Schedule an event on the simulation environment
     *
     * @param event Event to be scheduled
     *
     * @return Scheduled event
     */
    fun schedule(event: EventBase): EventBase {
        event.scheduledExecutionTime = now + (event.timeout ?: 0.0)
        assignEventId(event)
        eventQueue.add(event)
        return event
    }

    /**
     * Actual simulation loop. Triggers the processing of events from the event queue until it is empty or the termination event is triggered.
     */
    private fun simulationLoop() {
        while (!eventQueue.isEmpty()) {
            val event = step()
            if (event is TerminationEvent) {
                break
            }
        }
    }

    /**
     * Takes the next event from the event loop and processes it.
     *
     * @return The next event from the eventQueue
     */
    private fun step(): EventBase {
        val nextEvent = eventQueue.remove()
        now = nextEvent.scheduledExecutionTime
        nextEvent.processEvent()
        return nextEvent
    }

    private fun assignEventId(event: EventBase) {
        event.setId(nextEventId())
    }
}