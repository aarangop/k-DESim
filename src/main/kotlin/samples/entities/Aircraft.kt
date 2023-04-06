/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package samples.entities

import simulation.core.Environment

class Aircraft(val env: Environment, name: String) {
    fun takeoff() = run {
        env.process(sequence {
            yield(env.timeout(30.0))
        })
    }
}