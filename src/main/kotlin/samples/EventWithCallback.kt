package samples

import simulation.Environment
import simulation.EventBase

class EventWithCallback(env: Environment, timeout: Double) : EventBase(env, timeout) {
    override fun action() {
        println("Event action triggered at time ${env.now}")
    }
}

fun main() {
    val env = Environment()
    val evt = EventWithCallback(env, 10.0)

    env.schedule(evt)

    env.run()
}