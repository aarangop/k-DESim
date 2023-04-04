/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.process

import simulation.core.Environment
import simulation.event.Event

/**
 * The `Process` class represents a process which can wait for other events to be triggered. Processes are declared with
 * kotlin `sequences`, which are akin to Python's generators. The sequence associated with a `Process` must be an
 * `EventBase` yielding sequence.
 *
 * A Process is itself an event and can be scheduled like a normal event. When it is triggered by the environment, the
 * process' sequence starts.
 *
 * When the process sequence is being executed, execution will stop at every yield command.
 *
 * The sequence must yield EventBase types. The yielded events will then be scheduled by the environment. When the
 * events are fired, the sequence will continue execution.
 *
 * From SimPy's documentation:
 *
 * '
 * A generator (also known as a coroutine) can suspend its execution by
 * yielding an event. ``Process`` will take care of resuming the generator
 * with the value of that event once it has happened. The exception of failed
 * events is thrown into the generator.
 *
 * ``Process`` itself is an event, too. It is triggered, once the generator
 * returns or raises an exception. The value of the process is the return
 * value of the generator or the exception, respectively.
 *
 * Processes can be interrupted during their execution by :meth:`interrupt`.
 *  ...
 * '
 */
open class Process(
    env: Environment,
    processSequence: Sequence<Event>,
    timeout: Double = 0.0
) : Event(env, timeout) {

    private var processSequenceIterator: Iterator<Event> = processSequence.iterator()
    private var processStartTime: Double = 0.0
    val processFinishedEvent = Event(env)

    var isAlive: Boolean = false
        private set

    init {
        processStartTime = env.now
        this.appendCallback {
            resume()
        }
    }

    fun interrupt() {
        throw NotImplementedError()
    }

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

    override fun processEvent() {
        isTriggered = true
        isAlive = true
        for (callback in callbacks) {
            callback(this)
        }
    }
}
