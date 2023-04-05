/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import Environment
import process.SimProcess

fun main() {
    val env = Environment()
    val subProcess = SimProcess(env, sequence {
        println("${env.now} - p1 starting")
        yield(env.timeout(10.0))
        println("${env.now} - p1 finished waiting")
    })

    val mainProcess = SimProcess(env, sequence {
        println("${env.now} - p2 starting")
        yield(env.timeout(10.0))
        println("${env.now} - p2 finished waiting, will wait for p1 to finish")
        // Problem: Process sequence 1 is not being resumed.
        yield(env.process(subProcess))
        println("${env.now} - p1 and p2 finished")
        yield(env.timeout(0.0))
    })

    env.process(mainProcess)
    env.run()
}