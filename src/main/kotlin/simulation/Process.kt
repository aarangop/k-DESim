/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

/**
 * The Process class receives a sequence which yields events.
 *
 * A Process can be scheduled by the environment. The provided sequence will be executed and stopped on every yield.
 * The sequence must yield Event types, which will be scheduled and when fired, will cause the process to resume execution.
 */
open class Process(
    env: Environment,
    processSequence: Sequence<EventBase>,
    timeout: Double = 0.0
) : EventBase(env, timeout) {

    private var processSequenceIterator: Iterator<EventBase>? = null
    private var processStartTime: Double = 0.0
    var processedEvent = EventBase(env, 0.0)

    init {
        processSequenceIterator = processSequence.iterator()
        processStartTime = env.now
    }

    private fun resume() {
        // Start process execution by getting the next event from the process sequence
        try {
            // Check for next item in iterator inside try/catch block to avoid 'peeking' into the iterator with hasNext
            val nextEvent = processSequenceIterator?.next()
            // Schedule next event
            if (nextEvent != null) {
                // Once the next event is processed the process needs to be resumed
                nextEvent.addCallback { resume() }
                env.schedule(nextEvent)
            }
        } catch (_: NoSuchElementException) {
            // Process execution finished
            isProcessed = true
            // Schedule execution of processedEvent event
            env.schedule(processedEvent)
        }
    }

    override fun processEvent() {
        isTriggered = true

        resume()
    }
}
