package samples

import simulation.Environment
import simulation.Event

fun main(args: Array<String>) {
    val env = Environment()
    env.schedule(Event(env, 10.0))
    env.run()
    println("Simulation finished at time ${env.now}")
}