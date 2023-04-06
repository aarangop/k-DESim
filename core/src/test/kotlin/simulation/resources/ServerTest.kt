/*
 * Copyright (c) 2023. Andrés Arango Pérez <arangoandres.p@gmail.com>
 *
 * You may use, distribute and modify this code under the terms of the MIT license.
 */

package simulation.resources

import Environment
import event.Event
import exceptions.InvalidServerAction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import resources.Server
import simulation.KDESimTestBase
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerTest : KDESimTestBase() {

    class MyServer(env: Environment) : Server(env) {
        fun doSomething(scope: SequenceScope<Event>) = serverAction(scope) {
            sequence {
                println("Server will start a long work @${env.now}")
                yield(env.timeout(1000.0))
                println("Server finished work @${env.now}")
            }
        }
    }

    @Test
    fun `access the server actions after it becomes available`() {
        val server = MyServer(env)

        val mainProcess = env.process(sequence {
            yield(server.request(this))
            assertDoesNotThrow { server.doSomething(this) }
        })
        env.run(mainProcess)
    }

    @Test
    fun `running a server action before it is available throws an exception`() {
        val server = MyServer(env)

        val mainProcess = env.process(sequence {
            // Request a server but do not wait for it to become available.
            server.request(this)
            assertFailsWith<InvalidServerAction> { server.doSomething(this) }
        })

        env.run(mainProcess)
    }

    @Test
    fun `attempting to access a server that is currently blocked by another process throws an exception`() {
        val server = MyServer(env)
        // Spawn a blocking process.
        env.process(sequence {
            yield(server.request(this))
            yield(env.timeout(50.0))
            yield(server.release())
        })
        // Spawn a process that will attempt to use the blocked server
        val failProcess = env.process(sequence {
            assertFailsWith<InvalidServerAction> { server.doSomething(this) }
        })
        env.run(failProcess)
    }

    @Test
    fun `access the server after it is released by another process`() {
        val server = MyServer(env)

        env.process(sequence {
            yield(server.request(this))
            yield(env.timeout(10.0))
            yield(server.release())
        })
        val process = env.process(sequence {
            yield(server.request(this))
            assertDoesNotThrow { server.doSomething(this) }
        })

        env.run(process)
    }

    @Test
    fun `request blocks until the server is released by another process`() {
        val server = Server(env)
        // Blocking process
        env.process(sequence {
            yield(server.request(this))
            yield(env.timeout(100.0))
            yield(server.release())
        })
        // Waiting process
        env.process(sequence {
            yield(server.request(this))
            assertEquals(100.0, env.now)
        })

        env.run()
    }

    @Test
    fun `server action blocks until sequence finishes`() {
        val server = MyServer(env)
        var timeServerFinishedAction = 0.0
        env.process(sequence {
            yield(server.request(this))
            yield(env.timeout(10.0))
            yield(server.release())
        })
        val process = env.process(sequence {
            yield(server.request(this))
            yield(server.doSomething(this))
            timeServerFinishedAction = env.now
        })

        env.run(process)

        assertEquals(1010.0, timeServerFinishedAction)
    }

    @Test
    fun `concurrent requests for a resource must wait for the resource to be released`() {
        val server = MyServer(env)
        val expectedRequestFulfilledTimes = listOf(0.0, 1100.0, 2200.0, 3300.0, 4400.0)
        val actualRequestFulfilledTimes = mutableListOf<Double>()
        // Create multiple processes that will request a server.
        for (i in 0..4) {
            env.process(sequence {
                yield(server.request(this))
                actualRequestFulfilledTimes += env.now
                yield(server.doSomething(this))
                yield(env.timeout(100.0))
                yield(server.release())
            })
        }
        env.run(5000.0)

        assertContentEquals(expectedRequestFulfilledTimes, actualRequestFulfilledTimes)
    }
}