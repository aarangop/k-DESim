/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import ResourceBase
import simulation.core.Environment
import simulation.exceptions.ResourceDepletedException
import simulation.exceptions.ResourceIsFullException
import simulation.exceptions.StoreAlreadyInitializedException

/**
 * Discrete resource. Hols a definite, integer number of items.
 */
class Store<T>(env: Environment, val storeCapacity: Int) : ResourceBase(env, storeCapacity.toDouble()) {
    private var items: ArrayDeque<T> = ArrayDeque()
    private var getQueue: ArrayDeque<StoreGet<T>> = ArrayDeque()
    private val putQueue: ArrayDeque<StorePut<T>> = ArrayDeque()
    private var isInitialized = false

    /**
     * Returns the number of items available in the store.
     */
    val numberOfAvailableItems: Int
        get() = items.filterNotNull().size

    /**
     * Initialize the store with a list of items.
     *
     * @param initialItems List of items to put into the store.
     */
    fun initialize(initialItems: List<T>) {
        require(initialItems.size <= storeCapacity) { "The number of items provided to initialize store exceeds the store capacity." }
        if (isInitialized) {
            throw StoreAlreadyInitializedException()
        }
        for (item in initialItems) {
            items.add(item)
        }
        isInitialized = true
    }

    /**
     * Request one item from the store.
     *
     * Returns an event, which will hold the item requested from the store once it has been processed.
     *
     * The event will be scheduled immediately. However, it's processing depends on whether there are enough items in
     * the store to fulfill the request.
     *
     * @return An event associated with the request
     */
    fun requestOne(): StoreGet<T> {
        return StoreGet(env, this)
    }

    fun putOne(item: T): StorePut<T> {
        return StorePut(env, this, item)
    }

    private fun getNextItem(): T {
        if (numberOfAvailableItems == 0) {
            throw ResourceDepletedException()
        }

        return items.removeFirst()
    }

    private fun putNextItem(item: T) {
        if (numberOfAvailableItems < capacity) {
            items.add(item)
        } else {
            throw ResourceIsFullException()
        }
    }

    /**
     * The `tryGetOne` function triggers the execution of the getEvent if there are enough items in store to fulfill the request.
     *
     * If there are not enough items in store to fulfill the request, the request is added to the getQueue.
     *
     * @param getEvent The event that will be triggered if the request is fulfilled
     */
    internal fun tryGet(getEvent: StoreGet<T>) {
        try {
            getEvent.succeed(getNextItem())
        } catch (e: ResourceIsFullException) {
            getQueue.add(getEvent)
        }
    }

    /**
     * The `tryPut` function triggers the execution of the putEvent if the store has capacity left.
     *
     * If the store is full, the request will be added to the putQueue.
     *
     * @param putEvent The event that will be triggered when the item has been put into the store.
     */
    fun tryPut(putEvent: StorePut<T>, item: T) {
        try {
            putNextItem(item)
            putEvent.succeed(item)
        } catch (e: ResourceIsFullException) {
            putQueue.add(putEvent)
        }
    }
}