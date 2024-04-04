package data

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val title: String,
    val author: String,
    val category: String,
    val published:Int,
    val img: String,
)
