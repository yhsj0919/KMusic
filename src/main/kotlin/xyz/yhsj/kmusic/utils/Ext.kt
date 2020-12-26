package xyz.yhsj.kmusic.utils

import io.ktor.http.*
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

fun String.md5(): String = MD5.md5(this)


fun FormParams(builder: ParametersBuilder.() -> Unit): Parameters {
    return Parameters.build(builder = builder)
}

fun Parameters.plus(builder: ParametersBuilder.() -> Unit): Parameters {
    return this.plus(Parameters.build(builder = builder))
}

fun Parameters.params(): String {
    val params = StringBuffer()
    this.forEach { s, list ->
        list.forEach {
            params.append("$s=$it&")
        }
    }
    val data = params.toString()
    return if (data.endsWith("&")) {
        data.substring(0, data.length - 1)
    } else {
        data
    }
}
