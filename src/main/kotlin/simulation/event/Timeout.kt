/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import simulation.core.Environment

/**
 * Wrapper class for timeout event.
 */
class Timeout(env: Environment, timeout: Double) : EventBase(env, timeout)