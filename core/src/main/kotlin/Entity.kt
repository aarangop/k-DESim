/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

import event.Event


open class Entity(val env: Environment) {
    companion object {
        fun entityProcess(entity: Entity, fn: () -> Event) {

        }
    }
}