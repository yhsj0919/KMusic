package xyz.yhsj.kmusic.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import khttp.responses.Response
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.concurrent.Callable
import java.util.concurrent.Executors


enum class HttpMethod {
    GET, POST
}


//json序列化
fun Any?.json(): String =
    if (this != null) {
        Gson().toJson(this)
    } else {
        ""
    }

//json反序列化
inline fun <reified T> fromJson(json: String): T = Gson().fromJson(json, T::class.java)


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


inline fun <reified T> Response.obj(): T {
    println(this.url)
    return when (T::class.java) {
        String::class.java -> {
            this.text as T
        }
        ByteArray::class.java -> {
            this.content as T
        }
        JSONObject::class.java -> {
            this.jsonObject as T
        }
        JSONArray::class.java -> {
            this.jsonArray as T
        }
        else -> {
            fromJson(this.text)
        }
    }
}

//
//fun FormParams(builder: ParametersBuilder.() -> Unit): Parameters {
//    return Parameters.build(builder = builder)
//}
//
//fun Parameters.plus(builder: ParametersBuilder.() -> Unit): Parameters {
//    return this.plus(Parameters.build(builder = builder))
//}
//
fun Map<String, String>.params(): String {
    return this.toList().joinToString("&") { "${it.first}=${it.second}" }
}
