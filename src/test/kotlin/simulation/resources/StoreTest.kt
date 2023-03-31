/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources


import org.junit.jupiter.api.Test
import simulation.KDESimTestBase
import simulation.exceptions.StoreAlreadyInitializedException
import simulation.process.Process
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class StoreTest : KDESimTestBase() {

    private var storeCapacity = 10
    private var store: Store<Unit> = Store(this.env, storeCapacity)

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
        var request: StorePut<Unit>? = null
        env.process(sequence {
            request = store.putOne(Unit)
            yield(request!!)
        })
        env.run()

        assertNull(request?.value())
    }

    @Test
    fun `when the store is empty, simulation halts until items become available`() {

    }

    @Test
    fun put() {
    }
}