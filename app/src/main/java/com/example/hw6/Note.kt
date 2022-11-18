package com.example.hw6

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NoteList (
    val listOfNotes : List<Note>
)

//just need to delete hashedPoster and timestamp and add upvote: Int, downvote: Int
@JsonClass(generateAdapter = true)
data class Note (
    val title : String,
    val body : String,
    val hashedPoster : String,
    val timestamp : String,
    val id : Int
)

@JsonClass(generateAdapter = true)
data class NoteOut(
    val title : String,
    val body: String,
    val poster: String
)

