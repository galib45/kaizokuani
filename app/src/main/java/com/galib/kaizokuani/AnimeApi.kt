package com.galib.kaizokuani

import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object AnimeApi {

    // private val BASE_URL = "https://api.allanime.day"
    private val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0"
    private val REFERER = "https://allmanga.to"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", USER_AGENT)
                .header("Referer", REFERER)
                .build()
            chain.proceed(request)
        }
        .build()

    fun sendGQLQuery(variables: String, query: String, callback: (String) -> Unit) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("api.allanime.day")
            .addPathSegment("api")
            .addEncodedQueryParameter("variables", variables)
            .addEncodedQueryParameter("query", query)
            .build()

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string() ?: "Empty")
                } else {
                    callback("Failed: ${response.code}")
                }
            }
        })
    }

    fun searchAnime(query: String, callback: (String) -> Unit) {
        val variables = """{
            "search": {
                "allowAdult": false,
                "allowUnknown": false,
                "query": "$query"
            },
            "limit": 40,
            "page": 1,
            "translationType": "sub",
            "countryOrigin": "ALL"
        }""".trimIndent()

        val gqlQuery = """
            query(
                ${'$'}search: SearchInput, 
                ${'$'}limit: Int, 
                ${'$'}page: Int, 
                ${'$'}translationType: VaildTranslationTypeEnumType, 
                ${'$'}countryOrigin: VaildCountryOriginEnumType
            ) {
                shows(
                    search: ${'$'}search, 
                    limit: ${'$'}limit, 
                    page: ${'$'}page, 
                    translationType: ${'$'}translationType, 
                    countryOrigin: ${'$'}countryOrigin) 
                    {
                        edges {
                            _id name
                        }
                }
            }
        """.trimIndent()
        sendGQLQuery(variables = variables, query = gqlQuery, callback = callback)
    }

    fun getAnimeInfo(id: String, callback: (String) -> Unit) {
        val variables = """{  "showId":  "$id" }""".trimIndent()

        val gqlQuery = """
            query (${'$'}showId: String!) {    
                show(_id: ${'$'}showId) {        
                    _id name englishName 
                    status description thumbnail 
                    score genres tags season 
                    rating episodeCount episodeDuration 
                    availableEpisodesDetail    
                }
            }
        """.trimIndent()
        sendGQLQuery(variables = variables, query = gqlQuery, callback = callback)
    }

    fun getSourceUrls(
        id: String, episodeNo: String,
        translationType: String = "sub",
        callback: (String) -> Unit
    ) {
        val variables = """
            {
              "showId": "$id",
              "episodeString": "$episodeNo",
              "translationType": "$translationType"
            }
        """.trimIndent()

        val gqlQuery = """
            query (
            	${'$'}showId: String!, ${'$'}episodeString: String!, 
                ${'$'}translationType: VaildTranslationTypeEnumType!
            ) {
              episode(
                showId: ${'$'}showId, episodeString: ${'$'}episodeString, 
                translationType: ${'$'}translationType
              ) 
              { sourceUrls }
            }
        """.trimIndent()
        sendGQLQuery(variables = variables, query = gqlQuery, callback = callback)
    }

    suspend fun getEpisodeLink(decodedSourceUrl: String) : String {
        val url = "https://allanime.day$decodedSourceUrl"

        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }
}