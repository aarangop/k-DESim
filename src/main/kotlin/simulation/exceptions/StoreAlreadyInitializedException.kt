/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.exceptions

class StoreAlreadyInitializedException : Throwable() {
    override val message: String
        get() = "The store is already initialized."
}