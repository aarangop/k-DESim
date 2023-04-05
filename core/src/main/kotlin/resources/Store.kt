/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package resources

import Environment
import event.StoreGetEvent
import event.StorePutEvent
import exceptions.StoreAlreadyInitializedException
import exceptions.StoreIsEmptyException
import exceptions.StoreIsFullException

/**
 * Discrete resource. Hols a definite, integer number of items.
 */
class Store<T>(val env: Environment, val storeCapacity: Int) {
    private var items: ArrayDeque<T> = ArrayDeque()
    private var getQueue: ArrayDeque<StoreGetEvent<T>> = ArrayDeque()
    private val putQueue: ArrayDeque<StorePutEvent<T>> = ArrayDeque()
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
     * Returns an event that will hold the item requested from the store once the request has been processed.
     *
     * The event will be scheduled immediately. However, it's processing depends on whether there are enough items in
     * the store to fulfill the request.
     *
     * @return A `StoreGet<T>` event associated with the request
     */
    fun requestOne(): StoreGetEvent<T> {
        return StoreGetEvent(env, this)
    }

    /**
     * Request one item to be put into the store.
     *
     * Returns an event whose value holds the item put into the store once the request has been processed.
     *
     * The associated put request event will be scheduled immediately.
     *
     * If the store is full, the associated request will be processed when the store has capacity.
     *
     * If the store is not full the request can be processed immediately.
     *
     * @param item Item to put into the store.
     *
     * @return A `StorePut<T>` event associated with the request.
     */
    fun putOne(item: T): StorePutEvent<T> {
        return StorePutEvent(env, this, item)
    }

    /**
     * Attempt to get an item from the store. If the store is empty throws a `ResourceDepletedException`.
     *
     * @return Item from the store
     */
    private fun getNextItem(): T {
        if (items.isEmpty()) {
            throw StoreIsEmptyException()
        }

        return items.removeFirst()
    }

    /**
     * Attempt to put an item back into the store. If the store is full throw a `ResourceIsFullException`.
     *
     * @param item Item to put into the store.
     */
    private fun putNextItem(item: T) {
        if (numberOfAvailableItems < storeCapacity) {
            items.add(item)
        } else {
            throw StoreIsFullException()
        }
    }

    /**
     * Try to trigger the execution of the `getEvent`.
     *
     * If there are enough items in store to fulfill the request, trigger the `getEvent`.
     *
     * If there are not enough items in store to fulfill the request, the request is queued.
     *
     * @param getEvent The event that will be triggered if the request is fulfilled
     */
    internal fun tryGet(getEvent: StoreGetEvent<T>) {
        try {
            getEvent.succeed(getNextItem())
        } catch (e: StoreIsEmptyException) {
            getQueue.add(getEvent)
        }
    }

    /**
     * Try to trigger the execution of the `putEvent`.
     *
     * If the store has capacity left, trigger the `putEvent`.
     *
     * If the store is full, the request will be queueu.
     *
     * @param putEvent The event that will be triggered when the item has been put into the store.
     */
    internal fun tryPut(putEvent: StorePutEvent<T>) {
        try {
            putNextItem(putEvent.item)
            putEvent.succeed(putEvent.item)
        } catch (e: StoreIsFullException) {
            putQueue.add(putEvent)
        }
    }

    /**
     * Try to fulfill queued get requests.
     *
     * This function is added as a callback to `StorePut<T>`. This allows queued `StoreGet` requests to be processed
     * when items are put back into the store.
     */
    internal fun processPut() {
        if (getQueue.isEmpty()) return
        val nextGetEvent = getQueue.removeFirst()
        tryGet(nextGetEvent)
    }

    /**
     * Try to fulfill put requests.
     *
     * This function is added as a callback to `StoreGet<T>`. This allows queued `StorePut` requests to be processed
     * when items are taken from the store.
     */
    internal fun processGet() {
        if (putQueue.isEmpty()) return
        val nextPutEvent = putQueue.removeFirst()
        tryPut(nextPutEvent)
    }
}