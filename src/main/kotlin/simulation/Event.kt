package simulation

class Event<T>(env: Environment, timeout: Double) : EventBase(env, timeout) {
    private var eventValue: T? = null

    fun succeed(eventValue: T) {
        this.addCallback { processEvent(eventValue) }
        env.schedule(this)
    }

    private fun processEvent(eventValue: T) {
        super.processEvent()
        this.eventValue = eventValue
    }

    fun value(): T? {
        return eventValue
    }
}