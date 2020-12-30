package xyz.yhsj.kmusic

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*

val baiduMusic = HttpClient(OkHttp) {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    install(JsonFeature) {
        serializer = GsonSerializer()
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "api-qianqian.taihe.com/v1"
        }
        header("app-version", "v8.2.3.1")
        header("from", "android")
        header(
            "user-agent",
            "Mozilla/5.0 (Linux; U; Android 8.0.0; zh-cn; MI 5 Build/OPR1.170623.032) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
        )
    }
}

val client = HttpClient(OkHttp) {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    install(JsonFeature) {
        serializer = GsonSerializer()
    }

    defaultRequest {
        header(
            "user-agent",
            "Mozilla/5.0 (Linux; U; Android 8.0.0; zh-cn; MI 5 Build/OPR1.170623.032) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
        )
    }
}


suspend inline fun <reified T> HttpClient.get(
    url: String,
    autoClose: Boolean = true,
    block: HttpRequestBuilder.() -> Unit = {}
): T {
    return get<T>(url, block).apply {
        if (autoClose) {
            close()
        }
    }
}

suspend inline fun <reified T> HttpClient.form(
    url: String,
    formParameters: Parameters = Parameters.Empty,
    encodeInQuery: Boolean = false,
    autoClose: Boolean = true,
    block: HttpRequestBuilder.() -> Unit = {}
): T {
    return submitForm<T>(url, formParameters, encodeInQuery, block).apply {
        if (autoClose) {
            close()
        }
    }
}

suspend inline fun <reified T> HttpClient.formWithBinaryData(
    url: String,
    formData: List<PartData> = emptyList(),
    autoClose: Boolean = true,
    block: HttpRequestBuilder.() -> Unit = {}
): T {
    return submitFormWithBinaryData<T>(url, formData, block).apply {
        if (autoClose) {
            close()
        }
    }
}