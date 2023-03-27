package samples

import simulation.Environment
import simulation.EventBase

fun main() {
    val env = Environment()
    env.schedule(EventBase(env, 10.0))
    env.run()
    println("Simulation finished at time ${env.now}")
}