package com.example.techpulse.data.repository

import com.example.techpulse.data.remote.RetrofitInstance
import com.example.techpulse.data.remote.TechPulseApi
import com.example.techpulse.data.remote.toDomainModel
import com.example.techpulse.domain.RepositoryItem

class TechPulseRepository {
    private val api: TechPulseApi = RetrofitInstance.api

    suspend fun searchRepositories(query: String): Result<List<RepositoryItem>> {
        return try {
            val response = api.searchRepositories(query = query)
            val domainLIst = response.items.map { it.toDomainModel() }
            Result.success(domainLIst)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}