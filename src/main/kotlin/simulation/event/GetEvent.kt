/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.event

import ResourceBase
import simulation.core.Environment

class GetEvent<T>(env: Environment, resource: ResourceBase) : Event<T>(env)