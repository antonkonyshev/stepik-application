package com.github.antonkonyshev.stepic.data.network

import okhttp3.Interceptor
import okhttp3.Response

class StepicHeaderInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        var request = request()
        if (request.method == "POST") {
            request.header("Cookie")?.let { cookie ->
                """csrftoken=[\w\d]+""".toRegex().find(cookie)?.value?.replace(
                    "csrftoken=", ""
                )?.let { csrfToken ->
                    request = request.newBuilder()
                        .addHeader("X-CSRFToken", csrfToken)
                        .build()
                }
            }
        }
        proceed(request)
    }
}