/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package event

import Environment

/**
 * Wrapper class for timeout event.
 */
class Timeout(env: Environment, timeout: Double) : Event(env, timeout)