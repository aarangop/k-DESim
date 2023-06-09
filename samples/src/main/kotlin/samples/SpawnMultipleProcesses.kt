/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import Environment
import process.SimProcess

fun main() {
    // Create environment
    val env = Environment()

    // Define sequence of process 1
    val process1 = sequence {
        println("Starting process 1 @ ${env.now}")
        yield(env.timeout(10.0))
        println("Continuing execution of process 1 @ ${env.now}")
        yield(env.timeout(10.0))
        println("Finishing execution of process 1  @ ${env.now}")
    }

    // Define sequence of process 2
    val process2 = sequence {
        println("Starting process 2 @ ${env.now}")
        yield(env.timeout(15.0))
        println("Continuing execution of process 2 @ ${env.now}")
        yield(env.timeout(10.0))
        println("Finishing execution of process 2 @ ${env.now}")
    }

    // Schedule processes for immediate execution
    env.schedule(SimProcess(env, process1))
    env.schedule(SimProcess(env, process2))

    // Run simulation
    env.run()
}