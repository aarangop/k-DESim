package simulation

class Timeout(env: Environment, timeout: Double) : Event(env, timeout) {
    init {
        env.schedule(this)
    }
}