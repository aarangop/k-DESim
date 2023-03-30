/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import org.junit.jupiter.api.Test
import simulation.KDESimTestBase
import simulation.process.Process
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

class StoreTest : KDESimTestBase() {
    class Product(val weight: Double, val size: Double)

    var productStore: Store<Product> = Store(env, 10)

    @BeforeTest
    fun initializeStore() {
        productStore.initialize(List(productStore.capacity) {
            Product(
                Random.nextDouble(10.0, 200.0),
                Random.nextDouble(1.0, 200.0),
            )
        })
    }

    @Test
    fun get() {
        var requestedProduct: Product? = null
        val p1 = Process(env, sequence {
            // Request a product from the store
            val request = productStore.get(1.0)
            yield(request)
            if (request.value() != null) {
                requestedProduct = request.value()
            }
        })
        env.process(p1)
        env.run()

        assertNotNull(requestedProduct)
    }

    @Test
    fun put() {
    }
}