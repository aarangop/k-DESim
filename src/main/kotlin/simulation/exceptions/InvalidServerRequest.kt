/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.exceptions

class InvalidServerRequest :
    Throwable("It was attempted to retrieve a server from a different scope than from where it was requested.")
