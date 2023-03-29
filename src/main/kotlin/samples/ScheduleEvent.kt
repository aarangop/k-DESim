/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import simulation.core.Environment
import simulation.event.EventBase

fun main() {
    val env = Environment()
    env.schedule(EventBase(env, 10.0))
    env.run()
    println("Simulation finished at time ${env.now}")
}