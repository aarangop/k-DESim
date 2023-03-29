/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import simulation.core.Environment
import simulation.event.Process

fun main() {
    val env = Environment()
    val subProcess = Process(env, sequence {
        println("${env.now} - p1 starting")
        yield(env.timeout(10.0))
        println("${env.now} - p1 finished waiting")
    }, processId = "Subprocess")

    val p1 = Process(env, sequence {
        println("${env.now} - p2 starting")
        yield(env.timeout(10.0))
        println("${env.now} - p2 finished waiting, will wait for p1 to finish")
        yield(env.process(subProcess))
        println("${env.now} - p1 and p2 finished")
        yield(env.timeout(0.0))
    }, processId = "Process 2")

    // FIX: Process 1 is not being fully processed!
    // After processing subProcess, nothing else happens even though the full sequence of Process 1 hasn't been iterated!
    env.process(p1)
    env.run()
}