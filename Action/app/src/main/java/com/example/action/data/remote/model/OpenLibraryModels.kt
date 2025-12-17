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
    @SerializedName("subject") val subject: List<String>?,
    @SerializedName("first_sentence") val firstSentence: List<String>?,
    @SerializedName("number_of_pages_median") val numberOfPages: Int?,
    @SerializedName("cover_url") val directCoverUrl: String? = null // URL directa para casos especiales
) {
    val coverUrl: String?
        // Priorizar coverUrl directo si existe, sino usar coverId
        get() = directCoverUrl ?: coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }

    
    fun getAuthor(): String? {
        return authorName?.firstOrNull()
    }
    
    fun getBookId(): String {
        return key.removePrefix("/works/")
    }
    
    fun getDescription(): String? {
        return firstSentence?.joinToString(" ")
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

// Modelo para obtener detalles completos de un libro
data class OpenLibraryWorkDetails(
    val description: Any?, // Puede ser String o objeto con "value"
    val title: String?,
    val subjects: List<String>?,
    val covers: List<Int>?,
    @SerializedName("first_publish_date") val firstPublishDate: String?
) {
    fun getDescriptionText(): String? {
        return when (description) {
            is String -> description
            is Map<*, *> -> description["value"] as? String
            else -> null
        }
    }
}
