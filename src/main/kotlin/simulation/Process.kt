package simulation

/**
 * The Process class receives a sequence which yields events.
 *
 * A Process can be scheduled by the environment. The provided sequence will be executed and stopped on every yield.
 * The sequence must yield Event types, which will be scheduled and when fired, will cause the process to resume execution.
 */
class Process(
    env: Environment,
    processSequence: Sequence<Event>,
    timeout: Double = 0.0
) : Event(env, timeout) {

    private var processSequenceIterator: Iterator<Event>? = null

    init {
        processSequenceIterator = processSequence.iterator()
    }

    override fun action() {
        // Start process execution by getting the next sequence item
        try { // Leave this inside a try catch block in order to avoid 'peeking' into the iterator with hasNext
            // If the sequence yields another event, make it call this function to continue execution whenever it is triggered.
            processSequenceIterator?.next()?.addCallback { action() }
        } catch (_: NoSuchElementException) {
            // Process execution finished
        }
    }
}
