package samples

import simulation.Environment
import simulation.EventBase
import simulation.Process

class EventWithValue<T>(env: Environment, timeout: Double) : EventBase(env, timeout) {
    var value: T? = null

    fun succeed(eventValue: T) {
        env.schedule(this)
    }

    override fun action() {
        super.action()
        // Where is the value set?
    }
}

/**
 * Pseudocode for a process that has value yielding events
 *
 * class TakeOffEvent<Tuple(Aircraft,Runway)>{...}
 * takeoffEvent = TakeOffEvent()
 * takeoffProcess = {
 *      yield(aircraft.turnOn())
 *      yield(aircraft.rollTo(runway))
 *      takeoffEvent.succeed(aircraft, runway)
 * }
 *
 * initOperationsProcess = {
 *      yield(aircraft.takeoff())
 * }
 */

fun main() {
    val env = Environment()
    val event = EventWithValue<String>(env, 10.0)
    var eventValue: String?
    val process = Process(env, sequence {
        // the yielded event returns a value when it is successfully processed.
//        eventValue = yield(env.schedule(event))
    })

    env.schedule(event)

}