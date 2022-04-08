package com.example.lesson24

import org.junit.Test

import org.junit.Assert.*
import java.io.Serializable
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MyFuture<T>(var isReady: Boolean, var value: T?)


fun calc(a: Int, b: Int): MyFuture<Int> {
    val future = MyFuture<Int>(false, null)
    thread {
        Thread.sleep(1000)
        future.isReady = true
        future.value = (a +  b)
    }
    return  future
}

fun calc2(a: Int, b: Int, callback: (Int) -> Unit) {
    thread {
        Thread.sleep(1000)
        callback(a + b)
    }
}

fun ex() {

    val res = calc(3, 4)
    res.isReady

    res.value


    calc2(2, 3) {
        //runOnUiThread
        val result = it
    }

    // ((2 + 3) + 4) + 5

    calc2(2, 3) { res->
        calc2(res, 4) { res2 ->
            calc2(res2, 5) {
                calc2(res2, 6) {
                    displayOnUi(it)
                }
            }
        }
    }


    loadUserFromDb { user, error ->

        if(error == null) {
            checkLoggedIn(user) { isLoggedIn ->

                if (isLoggedIn == true) {
                    getUserProfile(user) { profile ->
                        displayOnUi(profile)
                    }
                } else {
                    anotherRequest(user) { result ->

                    }
                }
            }
        }
    }



}

private fun loadUserFromDb(callback: (user: Any, error: Any) -> Unit) {}
private fun checkLoggedIn(user: Any, callback: (isLoggedIn: Any) -> Unit) {}
private fun getUserProfile(user: Any, callback: (profile: Any) -> Unit) {}
private fun anotherRequest(user: Any, callback: (profile: Any) -> Unit) {}


private fun displayOnUi(i: Any) {

}

private data class Note(val title: String, val text: String): Serializable {

}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val executor = Executors.newFixedThreadPool(3)

        println("Thread: " + Thread.currentThread().name)

        val future = executor.submit(MyCallable(2, 3))
        if(future.isDone) {
            println("result is ready: " + future.get())
        } else {
            println("result is not ready")
        }




        for(i in 1..5) {
            executor.execute(MyRunnable(i))
        }
        if(future.isDone) {
            println("result is ready: " + future.get())
        } else {
            println("result is not ready")
        }

        Thread.sleep(5000)
        if(future.isDone) {
            println("result is ready: " + future.get())
        } else {
            println("result is not ready")
        }
    }


}

class MyCallable(private val a: Int, private val b: Int): Callable<Int> {
    override fun call(): Int {
        println("MyCallable($a, $b) started on thread: ${Thread.currentThread().name} ")
        Thread.sleep(1000)
        println("MyCallable($a, $b) stopped on thread: ${Thread.currentThread().name} ")
        return a + b;
    }

}

class MyRunnable(private val number: Int): Runnable {
    override fun run() {
        println("MyRunnable-$number started on thread: ${Thread.currentThread().name} ")
        Thread.sleep(1000)
        println("MyRunnable-$number stopped on thread: ${Thread.currentThread().name} ")
    }

}