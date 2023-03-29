/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples

import simulation.core.Environment
import simulation.event.Event
import simulation.event.Process

class Aircraft(val env: Environment, val aircraftId: String) {
    val turnOnEvent: Event<Aircraft> = Event(env)
    val takeoffEvent: Event<Aircraft> = Event(env)
    private var isOn = false
    fun turnOn(): Event<Aircraft> {
        env.schedule(Process(env, sequence {
            // Wait for 10 seconds to simulate aircraft turning on
            yield(env.timeout(10.0))
            this@Aircraft.isOn = true
            // Signal that the turn on event succeeded, pass the aircraft as value
            turnOnEvent.succeed(this@Aircraft)
        }))
        return turnOnEvent
    }

    fun takeOff(): Event<Aircraft> {
        env.schedule(Process(env, sequence {
            if (!isOn) {
                takeoffEvent.fail()
            }
            yield(env.timeout(30.0))
            takeoffEvent.succeed(this@Aircraft)
        }))
        return takeoffEvent
    }
}

fun main() {
    val env = Environment()

    val aircraft = Aircraft(env, "Aircraft 1")

    val monitoringProcess = Process(env, sequence {
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