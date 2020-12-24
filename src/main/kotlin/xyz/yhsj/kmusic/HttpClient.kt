package xyz.yhsj.kmusic

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*

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

suspend inline fun <reified T> HttpClient.get(
    urlString: String,
    autoClose: Boolean = true,
    block: HttpRequestBuilder.() -> Unit = {}
): T {
    return get<T>(urlString, block).apply {
        if (autoClose) {
            close()
        }
    }
}