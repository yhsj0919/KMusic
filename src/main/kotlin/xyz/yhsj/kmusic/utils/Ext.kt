package xyz.yhsj.kmusic.utils

import java.util.concurrent.Callable
import java.util.concurrent.Executors

//import com.google.gson.Gson


//fun Any.json(): String = Gson().toJson(this)

fun <T, R> Array<out T>.future(transform: (T) -> R): List<R> {
    val callable = this.map {
        Callable {
            transform.invoke(it)
        }
    }
    return Executors.newWorkStealingPool(callable.size).invokeAll(callable)
            .map { it.get() }
}

fun <T, R> Iterable<T>.future(transform: (T) -> R): List<R> {
    val callable = this.map {
        Callable {
            transform.invoke(it)
        }
    }
    return Executors.newWorkStealingPool(callable.size).invokeAll(callable)
            .map { it.get() }
}