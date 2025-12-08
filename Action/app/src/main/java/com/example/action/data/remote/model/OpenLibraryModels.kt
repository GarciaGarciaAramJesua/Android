package com.example.action.data.remote.model

import com.google.gson.annotations.SerializedName

// Modelos para Open Library API

data class OpenLibrarySearchResponse(
    @SerializedName("numFound") val numFound: Int,
    val start: Int,
    @SerializedName("numFoundExact") val numFoundExact: Boolean?,
    val docs: List<OpenLibraryBook>
)

data class OpenLibraryBook(
    val key: String,
    val title: String,
    @SerializedName("author_name") val authorName: List<String>?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    @SerializedName("cover_i") val coverId: Int?,
    val isbn: List<String>?,
    @SerializedName("publisher") val publisher: List<String>?,
    @SerializedName("language") val language: List<String>?,
    @SerializedName("subject") val subject: List<String>?
) {
    fun getCoverUrl(): String? {
        return coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
    }
    
    fun getAuthor(): String? {
        return authorName?.firstOrNull()
    }
    
    fun getBookId(): String {
        return key.removePrefix("/works/")
    }
}

data class OpenLibraryAuthorResponse(
    @SerializedName("numFound") val numFound: Int,
    val start: Int,
    val docs: List<OpenLibraryAuthor>
)

data class OpenLibraryAuthor(
    val key: String,
    val name: String,
    @SerializedName("birth_date") val birthDate: String?,
    @SerializedName("top_work") val topWork: String?,
    @SerializedName("work_count") val workCount: Int?,
    @SerializedName("top_subjects") val topSubjects: List<String>?
)
