package samples

import simulation.Environment
import simulation.Event

class EventWithCallback(env: Environment, timeout: Double) : Event(env, timeout) {
    override fun action() {
        println("Event action triggered at time ${env.now}")
    }
}

fun main(args: Array<String>) {
    val env = Environment()
    val evt = EventWithCallback(env, 10.0)

    env.schedule(evt)

    env.run()
}