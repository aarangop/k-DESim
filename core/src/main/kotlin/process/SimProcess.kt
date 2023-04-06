/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package process

import Environment
import event.Event

/**
 * The `Process` class represents a process which can wait for other events to be triggered. Processes are declared with
 * kotlin `sequences`, which are akin to Python's generators. The sequence associated with a `Process` must be an
 * `EventBase` yielding sequence.
 *
 * A `Process` instance is itself an event and can be scheduled like a normal event. When it is triggered by the
 * environment, the process' sequence starts.
 *
 * When the process sequence is being executed, execution will stop at every yield command.
 *
 * The sequence must yield `Event` types or its subclasses. The yielded events will then be scheduled by the
 * environment and the process takes care of resuming the execution of the sequence when the yielded events are
 * triggered.
 */
open class SimProcess(
    env: Environment,
    processSequence: Sequence<Event>,
    timeout: Double = 0.0
) : Event(env, timeout) {

    private var processSequenceIterator: Iterator<Event> = processSequence.iterator()
    private var processStartTime: Double = 0.0

    val processFinishedEvent = Event(env)

    init {
        processStartTime = env.now
        this.appendCallback {
            resume()
        }
    }

    var isAlive: Boolean = false
        private set

    fun interrupt() {
        throw NotImplementedError()
    }

    /**
     * Continue the execution of the process sequence.
     *
     * If the sequence yields more events, append the `resume()` method to the next event in order to continue with the
     * sequence's execution once that event has been processed.
     *
     * If the sequence yields no more events, mark the process as processed, unset its `isAlive` flag and succeed its
     * `processFinishedEvent`.
     */
    private fun resume() {
        // Resume process by getting the next event from the process sequence
        try {
            // Check for next item in iterator inside try/catch block to avoid 'peeking' into the iterator with hasNext
            val nextEvent = processSequenceIterator.next()
            nextEvent.appendCallback { resume() }
        } catch (_: NoSuchElementException) {
            // Process execution finished
            isProcessed = true
            isAlive = false
            processFinishedEvent.succeed()
        }
    }

    /**
     * Override the `Event.processEvent()` method from the `Event` parent class. The process' `isProcessed` and
     * `isAlive` flags won't be set back to false after executing the callbacks because the actual processing can be
     * divided into several steps, determined by the events yielded by the process sequence.
     */
    override fun processEvent() {
        isTriggered = true
        isAlive = true
        for (callback in callbacks) {
            callback(this)
        }
    }
}
