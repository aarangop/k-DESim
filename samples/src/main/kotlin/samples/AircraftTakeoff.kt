/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import Environment
import entities.Aircraft
import process.SimProcess

fun main() {
    val env = Environment()

    val aircraft = Aircraft(env, "Aircraft 1")

    val monitoringProcess = SimProcess(env, sequence {
        yield(aircraft.turnOn())
        val aircraftThatTurnedOn = aircraft.turnOnEvent.value()
        if (aircraftThatTurnedOn != null) {
            println("${aircraftThatTurnedOn.aircraftId} powered up @ ${env.now}")
        }
        yield(aircraft.takeOff())
        val aircraftThatTookOff = aircraft.takeoffEvent.value()
        if (aircraftThatTookOff != null) {
            println("${aircraftThatTookOff.aircraftId} took off @ ${env.now}")
        }
    })

    env.schedule(monitoringProcess)
    env.run(1000.0)
}