/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import samples.entities.Aircraft
import simulation.core.Environment
import simulation.resources.Server

class Runway(env: Environment, val name: String) : Server(env)
class Airport(val env: Environment, val name: String) {
    val runway: Runway = Runway(env, "09R")
}

fun main() {
    val env = Environment()
    val aircraft = Aircraft(env, "Aircraft 1")
    var airport = Airport(env, "EDDS")
    env.process(sequence {

    })
}

