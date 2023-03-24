package samples

import simulation.Environment
import simulation.Process

fun main(args: Array<String>) {
    // Create environment
    val env = Environment()

    val simpleProcess = Process(env, sequence {
        println("Starting process execution @${env.now}")
        yield(env.timeout(50.0))
        println("First process step executed @${env.now}")
        yield(env.timeout(35.0))
        println("Second process step executed @${env.now}")
    })

    env.schedule(simpleProcess)

    env.run()
}