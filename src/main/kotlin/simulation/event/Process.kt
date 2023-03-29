/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment

/**
 * The Process class receives an event yielding sequence.
 *
 * A Process is itself an event and can be scheduled like a normal event. When it is triggered by the environment, the
 * process' sequence starts.
 *
 * When the process sequence is being executed, execution will stop at every yield command.
 *
 * The sequence must yield EventBase types. The yielded events will then be scheduled by the environment. When the events are fired, the sequence will continue execution.
 */
open class Process(
    env: Environment,
    processSequence: Sequence<EventBase>,
    timeout: Double = 0.0
) : EventBase(env, timeout) {

    private var processSequenceIterator: Iterator<EventBase>? = null
    private var processStartTime: Double = 0.0

    init {
        processSequenceIterator = processSequence.iterator()
        processStartTime = env.now
    }

    private fun resume() {
        // Start process execution by getting the next event from the process sequence
        try {
            // Check for next item in iterator inside try/catch block to avoid 'peeking' into the iterator with hasNext
            processSequenceIterator?.next()?.addCallback { resume() }
        } catch (_: NoSuchElementException) {
            // Process execution finished
            isProcessed = true
        }
    }


    override fun processEvent() {
        isTriggered = true
        resume()
    }
}
