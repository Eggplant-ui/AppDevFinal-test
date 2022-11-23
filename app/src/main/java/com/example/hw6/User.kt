package com.example.hw6

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass




@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "session_token") var sessionToken : String,
    @Json(name = "session_expiration") var sessionExpiration: String,
    @Json(name = "update_token") var updateToken: String

)

data class CreateUser(
    @Json(name = "username") var username : String,
    @Json(name = "password") var password: String
)

