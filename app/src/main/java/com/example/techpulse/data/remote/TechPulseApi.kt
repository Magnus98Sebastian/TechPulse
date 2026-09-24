package com.example.techpulse.data.remote

import com.example.techpulse.data.remote.dto.DevToArticleDto
import com.example.techpulse.data.remote.dto.DevToCommentDto
import com.example.techpulse.data.remote.dto.GithubSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit-Service-Interface zur Definition aller Netzwerkschnittstellen der Anwendung.
 *
 * Stellt Asynchrone (Suspend-)Funktionen für den Datenabruf von den REST-APIs
 * von Dev.to und GitHub bereit.
 */
interface TechPulseApi {

    /**
     * Sucht nach öffentlichen GitHub-Repositorys basierend auf einem Suchbegriff.
     *
     * @param query Der Suchbegriff bzw. Filter (z. B. Programmiersprache oder Thema).
     * @param sort Das Kriterium für die Sortierung (Standard: `"stars"`).
     * @param order Die Sortierreihenfolge (`"asc"` für aufsteigend, `"desc"` für absteigend; Standard: `"desc"`).
     * @param perPage Die Anzahl der Ergebnisse pro Seite (Standard: `100`).
     * @param page Die abzurufende Seitennummer für Paginierung (Standard: `1`).
     * @return Ein [GithubSearchResponseDto] mit der Liste der gefundenen Repositorys.
     */
    @GET("https://api.github.com/search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): GithubSearchResponseDto

    /**
     * Ruft eine Liste der aktuellsten Artikel von Dev.to ab.
     *
     * @return Eine Liste von [DevToArticleDto]-Objekten (maximal 30 Einträge).
     */
    @GET("articles")
    suspend fun getDevToArticles(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<DevToArticleDto>

    /**
     * Ruft die detaillierten Informationen eines spezifischen Dev.to-Artikels anhand seiner ID ab.
     *
     * @param id Die eindeutige Kennung des Artikels.
     * @return Das entsprechende [DevToArticleDto]-Objekt.
     */
    @GET("articles/{id}")
    suspend fun getArticleDetails(
        @retrofit2.http.Path("id") id: String
    ): DevToArticleDto

    /**
     * Ruft alle Kommentare zu einem bestimmten Dev.to-Artikel ab.
     *
     * @param articleId Die eindeutige Kennung des Artikels, dessen Kommentare geladen werden sollen.
     * @return Eine Liste von [DevToCommentDto]-Objekten für den angegebenen Artikel.
     */
    @GET("comments")
    suspend fun getArticleComments(
        @Query("a_id") articleId: String
    ): List<DevToCommentDto>

    companion object {
        /** Basis-URL für Anfragen an die GitHub REST API. */
        const val GITHUB_BASE_URL = "https://api.github.com/"

        /** Basis-URL für Anfragen an die Dev.to REST API. */
        const val DEV_TO_BASE_URL = "https://dev.to/api/"
    }
}