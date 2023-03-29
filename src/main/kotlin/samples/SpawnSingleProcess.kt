/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import simulation.core.Environment
import simulation.process.Process

fun main() {
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