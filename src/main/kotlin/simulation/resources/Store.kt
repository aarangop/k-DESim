/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import simulation.core.Environment

class Store<T>(env: Environment, val capacity: Int) : ResourceBase<T>(env) {
    /**
     * Request an item from the store
     *
     * @param quantity Number of item units to fetch from the store
     *
     * @return Get event, which when processed, will hold the request's value.
     */
    override fun get(quantity: Double): ResourceGet<T> {
        // Create an event associated with the request
        val getEvent = ResourceGet<T>(env)
        // Attach a callback which will try to
        env.schedule(getEvent)
        return ResourceGet(env)
    }

    override fun put(quantity: Double, items: T?): Put<T> {
        return Put(env)
    }

}