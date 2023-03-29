/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation

import simulation.core.Environment
import kotlin.test.BeforeTest

/**
 * Base test class that provides a new environment for each test.
 */
open class KDESimTestBase {
    var env: Environment = Environment()

    @BeforeTest
    fun setup() {
        env = Environment()
    }
}