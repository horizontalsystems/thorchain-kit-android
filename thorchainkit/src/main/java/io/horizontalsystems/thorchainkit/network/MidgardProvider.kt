package io.horizontalsystems.thorchainkit.network

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class MidgardProvider(
    baseUrls: List<URL>
) {

    private val apis: List<MidgardApi> = baseUrls.map {
        Retrofit.Builder()
            .baseUrl(it.toString())
            .client(ApiClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MidgardApi::class.java)
    }

    suspend fun fetchActions(
        address: String,
        limit: Int = 50,
        nextPageToken: String? = null
    ): Pair<List<MidgardAction>, String?> {
        var lastError: Throwable? = null

        apis.forEach { api ->
            try {
                val response = api.actions(address, limit, nextPageToken)
                return Pair(response.actions, response.meta.nextPageToken?.takeIf { it.isNotEmpty() })
            } catch (error: HttpException) {
                if (error.code() < 500) throw error
                lastError = error
            } catch (error: Throwable) {
                lastError = error
            }
        }

        throw lastError ?: IllegalStateException("No midgard providers configured")
    }
}
