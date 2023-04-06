/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package entities

import Entity
import Environment
import event.ValueEvent
import process.SimProcess

class Aircraft(env: Environment, val aircraftId: String) : Entity(env) {
    val turnOnEvent: ValueEvent<Aircraft> = ValueEvent(env)
    val takeoffEvent: ValueEvent<Aircraft> = ValueEvent(env)
    private var isOn = false
    fun turnOn(): ValueEvent<Aircraft> {
        env.process(sequence {
            // Wait for 10 seconds to simulate aircraft turning on
            yield(env.timeout(10.0))
            this@Aircraft.isOn = true
            // Signal that the turn on event succeeded, pass the aircraft as value
            turnOnEvent.succeed(this@Aircraft)
        })
        return turnOnEvent
    }

    fun takeOff(): ValueEvent<Aircraft> {
        env.schedule(SimProcess(env, sequence {
            if (!isOn) {
                takeoffEvent.fail()
            }
            yield(env.timeout(30.0))
            takeoffEvent.succeed(this@Aircraft)
        }))
        return takeoffEvent
    }
}