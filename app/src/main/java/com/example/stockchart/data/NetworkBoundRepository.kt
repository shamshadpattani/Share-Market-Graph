package com.example.stockchart.data

import androidx.annotation.MainThread
import com.example.stockchart.data.utlis.ResultOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import retrofit2.Response

@ExperimentalCoroutinesApi
abstract class NetworkBoundRepository<T> {

    fun asFlow() = flow<ResultOf<T>> {

        // Emit Loading State
        emit(ResultOf.Loading)

        try {
            // Fetch latest data from remote
            val apiResponse = fetchFromRemote()

            // Parse body
            val remotePosts = apiResponse.body()

            // Check for response validation
            if (apiResponse.isSuccessful && remotePosts != null) {
                emit(ResultOf.Success(remotePosts))
            } else {
                // Something went wrong! Emit Error state.
                emit(ResultOf.Error(Exception(apiResponse.message())))
            }
        } catch (e: Exception) {
            // Exception occurred! Emit error
            emit(ResultOf.Error(e))
            e.printStackTrace()
        }
    }

    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<T>
}