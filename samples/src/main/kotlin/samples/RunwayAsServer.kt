/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import Environment
import entities.Aircraft
import resources.Server

class Runway(env: Environment, val name: String) : Server(env)

fun main() {
    val env = Environment()
    val aircraft = List(5) { i: Int -> Aircraft(env, "Aircraft $i") }
    val runway = Runway(env, "09R")

    val aircraftTakeoffEvent = env.process(sequence {
        for (anAircraft in aircraft) {
            anAircraft.takeOffFromRunway(runway)
        }
        yield(env.timeout(1000.0))
    })

    env.run(aircraftTakeoffEvent)
}