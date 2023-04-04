/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources


import org.junit.jupiter.api.Test
import simulation.KDESimTestBase
import simulation.event.EventValueStatus
import simulation.event.StoreGetEvent
import simulation.event.StorePutEvent
import simulation.exceptions.StoreAlreadyInitializedException
import simulation.process.Process
import kotlin.test.*

class StoreTest : KDESimTestBase() {

    private var storeCapacity = 10
    private var store: Store<Unit> = Store(this.env, storeCapacity)

    fun getFullStore(): Store<Unit> {
        val newStore = Store<Unit>(env, storeCapacity)
        newStore.initialize(List(storeCapacity) { })
        return newStore
    }

    fun getEmptyStore(): Store<Unit> {
        return Store<Unit>(env, storeCapacity)
    }

    @BeforeTest
    fun initializeStore() {
        store = Store(env, storeCapacity)
        store.initialize(List(store.storeCapacity) { })
    }

    @Test
    fun `uninitialized store has no available items`() {
        val emptyStore = Store<Unit>(env, 10)
        assertEquals(0, emptyStore.numberOfAvailableItems)
    }

    @Test
    fun `initialized store has available items`() {
        val store = Store<Unit>(env, 10)
        store.initialize(List(10) {})
        assertEquals(10, store.numberOfAvailableItems)
    }

    @Test
    fun `trying to initialize a store again throws exception`() {
        assertFailsWith<StoreAlreadyInitializedException> {
            store.initialize(List(store.storeCapacity) { })
        }
    }

    @Test
    fun `get one item from the store`() {
        var requestedItem: Unit? = null
        val p1 = Process(env, sequence {
            // Request a product from the store
            val request = store.requestOne()
            yield(request)
            requestedItem = request.value()
        })
        env.process(p1)
        env.run()

        assert(requestedItem != null)
    }

    @Test
    fun `put item into the store`() {
        val store = Store<Unit>(env, 10)
        env.process(sequence {
            val request = store.putOne(Unit)
            yield(request)
        })
        env.run()

        assertEquals(1, store.numberOfAvailableItems)
    }

    @Test
    fun `item is not added to the store if the store is full`() {
        var request: StorePutEvent<Unit>? = null
        env.process(sequence {
            request = store.putOne(Unit)
            yield(request!!)
        })
        env.run()

        assertNull(request?.value())
    }

    @Test
    fun `get request is fulfilled after item has been put into the store`() {
        val store = getEmptyStore()
        var getRequest: StoreGetEvent<Unit>? = null
        env.process(sequence {
            getRequest = store.requestOne()
            yield(getRequest!!)
        })
        env.process(sequence {
            yield(env.timeout(10.0))
            store.putOne(Unit)
        })

        env.run()

        assertNotNull(getRequest!!.value())
    }

    @Test
    fun `put request is fulfilled after an item has been taken from the store`() {
        val store = getFullStore()
        var putRequest: StorePutEvent<Unit>? = null
        env.process(sequence {
            putRequest = store.putOne(Unit)
            yield(putRequest!!)
        })
        env.process(sequence {
            yield(env.timeout(50.0))
            store.requestOne()
        })

        env.run()

        assertEquals(EventValueStatus.AVAILABLE, putRequest!!.valueStatus)
    }
}