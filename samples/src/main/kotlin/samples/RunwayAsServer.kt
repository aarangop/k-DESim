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


/**
 * Use Kotlin `Extensions` to implement a takeoffSequence outside Aircraft, thus leaving the Aircraft class alone.
 */
fun Aircraft.takeoff(runway: Runway) = sequence {
    // Request the runway and wait until the request is fulfilled.
    yield(runway.request(this))
    // The runway is now in control of this aircraft instance, so take-off!
    yield(env.timeout(30.0))
    // Aircraft took off, notify
    println("Aircraft $aircraftId Took of from runway ${runway.name} @ ${env.now}")
    // Don't forget to release the runway so other aircraft can use it!
    yield(runway.release())
}

fun main() {
    val env = Environment()
    val aircraft = List(5) { i: Int -> Aircraft(env, "Aircraft $i") }
    val runway = Runway(env, "09R")

    for (anAircraft in aircraft) {
        // Spawn multiple takeoffSequences from aircraft!
        env.process(anAircraft.takeoff(runway))
    }

    env.run()
}