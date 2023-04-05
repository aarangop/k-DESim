/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

import event.Event
import event.TerminationEvent
import event.Timeout
import exceptions.EmptySchedule
import exceptions.StopSimulationException
import process.SimProcess
import java.util.*

/**
 * The Environment class is the main class for discrete simulation. It keeps track of the simulation time and is in charge of processing events.
 *
 * @param now Initial simulation time
 */
class Environment(var now: Double = 0.0) {
    init {
        require(now >= 0) { "The initial simulation time must be positive." }
    }

    private var eventQueue = PriorityQueue { t1: Event, t2: Event ->
        if (t1.timeout == t2.timeout) {
            (t2.priority.priority - t1.priority.priority)
        } else {
            (t1.scheduledExecutionTime - t2.scheduledExecutionTime).toInt()
        }
    }

    private var terminationEvent: TerminationEvent = TerminationEvent(this, 0.0)

    companion object {
        private var id: Int = -1
        fun nextEventId(): Int {
            id += 1
            return id
        }
    }

    /**
     * Run the simulation until a timeout event based on the provided timeout is triggered.
     *
     * @param until Timeout of the simulation termination event.
     */
    fun run(until: Double = 1000.0) {
        require(until > 0) { "Timeout must be greater than or equal to zero! " }

        run(schedule(Timeout(this, until)))
    }

    /**
     * Run the simulation until the termination event is triggered.
     *
     * The termination event must be scheduled by the user, otherwise the simulation will most likely eventually trigger
     * an EmptySchedule exception.
     *
     * @param untilEvent Event, which, when triggered, ends the simulation.
     *
     */
    fun run(untilEvent: Event) {
        // Start a process that waits for the untilEvent to be triggered, then triggers the termination event.
        schedule(SimProcess(this, sequence {
            yield(untilEvent)
            terminationEvent.succeed()
        }))
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
    fun process(process: SimProcess): Event {
        schedule(process)
        return process.processFinishedEvent
    }

    /**
     * Create and schedule a new process with the provided sequence.
     *
     * @param processSequence Event yielding sequence.
     *
     * @return Scheduled process.
     */
    fun process(processSequence: Sequence<Event>): Event {
        return process(SimProcess(this, processSequence))
    }

    /**
     * Schedules multiple processes on the simulation environment.
     *
     * @param processes List of processes to be scheduled by the environment.
     *
     * @return List of events associated with the termination of the provided processes.
     */
    fun process(vararg processes: SimProcess): List<Event> {
        val processFinishedEvents = mutableListOf<Event>()
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
    fun schedule(event: Event): Event {
        event.scheduledExecutionTime = now + event.timeout
        assignEventId(event)
        eventQueue.add(event)
        return event
    }

    /**
     * Schedule multiple events on the simulation environment.
     *
     * @param events Events to be scheduled.
     *
     * @return List of the scheduled events.
     */
    fun schedule(vararg events: Event): List<Event> {
        val eventList = mutableListOf<Event>()
        for (event in events) {
            val scheduledEvent = schedule(event)
            eventList += scheduledEvent
        }
        return eventList
    }

    /**
     * Actual simulation loop. Triggers the processing of events from the event queue until it is empty or the termination event is triggered.
     */
    private fun simulationLoop() {
        while (true) {
            try {
                step()
            } catch (e: StopSimulationException) {
                // Simulation finished by termination event!
                break
            }
        }
    }

    /**
     * Takes the next event from the event loop and processes it.
     *
     * @return The next event from the eventQueue
     */
    private fun step(): Event {
        try {
            val nextEvent = eventQueue.remove()
            now = nextEvent.scheduledExecutionTime
            nextEvent.processEvent()
            return nextEvent
        } catch (e: NoSuchElementException) {
            throw EmptySchedule()
        }
    }

    private fun assignEventId(event: Event) {
        event.setId(nextEventId())
    }
}