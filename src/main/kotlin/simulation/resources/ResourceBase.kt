/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import simulation.core.Environment
import simulation.event.Event

typealias Put<T> = Event<T>

abstract class ResourceBase<T>(val env: Environment) {
    private var items: List<T> = mutableListOf()
    protected var getQueue: List<ResourceGet<T>> = mutableListOf()
    protected var putQueue: List<Put<T>> = mutableListOf()
    abstract fun get(quantity: Double): ResourceGet<T>
    abstract fun put(quantity: Double, items: T?): Put<T>

    open fun initialize(initialItems: List<T>) {
        items = initialItems
    }
}