package kiwi.liam.crystal.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kiwi.liam.crystal.data.model.FeedbackModel
import kiwi.liam.crystal.data.model.ResponseMessage
import kotlin.time.Duration.Companion.milliseconds

interface FeedbackService {
    suspend fun sendFeedback(feedback: FeedbackModel): Result<ResponseMessage>
}

internal class CrystalFeedbackService(
    private val apiKey: String,
    private val host: String,
    private val client: HttpClient = HttpClient {
        install( ContentNegotiation) {
            json()
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }
        install(HttpTimeout) {
            val timeout = 500.milliseconds.inWholeMilliseconds
            socketTimeoutMillis = timeout
            requestTimeoutMillis = timeout
        }
    },
) : FeedbackService {
    override suspend fun sendFeedback(feedback: FeedbackModel): Result<ResponseMessage> {
        val response = client.post {
            url(host)

            contentType(ContentType.Application.Json)
            headers["x-api-key"] = apiKey

            setBody(feedback)
        }

        return response.body()
    }
}