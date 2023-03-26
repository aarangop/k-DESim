package samples

import simulation.Environment
import simulation.Event
import simulation.Process

class Aircraft(val env: Environment, val aircraftId: String) {
    val turnOnEvent: Event<Aircraft> = Event(env, 0.0)

    val turnOn = Process(env, sequence {
        //
        yield(env.timeout(10.0))
        println("${aircraftId} turned on @ ${env.now}")
//        turnOnEvent.succeed(this@Aircraft)
    })
}

fun main() {
    val env = Environment()

    val aircraft = Aircraft(env, "Aircraft 1")

    val monitoringProcess = Process(env, sequence {
        val turnOnFinishedEvent = env.process(aircraft.turnOn)
        // TODO: In this case an event is yielded which is not triggered by a timeout but by another process with
        //  `event.succeed`, therefore it should not be scheduled normally.
        yield(turnOnFinishedEvent)
        println("${aircraft.aircraftId} is turned on @ ${env.now}")
    })

    env.schedule(monitoringProcess)
    env.run()
}