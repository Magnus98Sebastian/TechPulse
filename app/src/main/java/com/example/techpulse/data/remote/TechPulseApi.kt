package com.example.techpulse.data.remote

import com.example.techpulse.data.remote.dto.GithubSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TechPulseApi {
    @GET ("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): GithubSearchResponseDto
    companion object {
        const val GITHUB_BASE_URL = "https://api.github.com/"
    }
}