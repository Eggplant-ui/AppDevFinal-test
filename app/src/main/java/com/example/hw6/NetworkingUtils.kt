package com.example.hw6

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


fun getNotes(callback : (List<Note>) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://143.198.115.54:8080/posts/")//change the URL
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            } else {
                val body = response.body
                //val jsonObject = JSONObject(body).toString()
                print(body.toString())
                val noteArrayJsonResponse = body!!.source()

                val moshi: Moshi = Moshi.Builder().build()
                val type = Types.newParameterizedType(List::class.java, Note::class.java)
                val adapter = moshi.adapter<List<Note>>(type)
                val notes : List<Note>? = adapter.fromJson(noteArrayJsonResponse)

                if (notes != null) {
                    callback(notes)
                }
            }
        }
    })
}

fun postNote(note :NoteOut, callback: (NoteOut) -> Unit){
    val client = OkHttpClient()

    val moshi: Moshi = Moshi.Builder().build()
    val adapter : JsonAdapter<NoteOut> = moshi.adapter(NoteOut::class.java)
    val json :String = adapter.toJson(note)
    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

    //change the url
    val postRequest = Request.Builder().post(requestBody).url("http://143.198.115.54:8080/posts/").build()

    client.newCall(postRequest).enqueue(object: Callback{
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if(!response.isSuccessful){
                throw IOException("Unexpected code $response")
            }else{
                val body = response.body
            }
        }
    })

}

fun logUser(User :CreateUser, sharedPrefs : SharedPreferences, callback: (CreateUser) -> Unit){
    //log in user

    val client = OkHttpClient()

    val moshi: Moshi = Moshi.Builder().build()
    val adapter : JsonAdapter<CreateUser> = moshi.adapter(CreateUser::class.java)
    val json :String = adapter.toJson(User)
    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

    //change the url POST /login/
    val postRequest = Request.Builder().post(requestBody).url("http://143.198.115.54:8080/posts/").build()

    client.newCall(postRequest).enqueue(object: Callback{
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()

        }

        override fun onResponse(call: Call, response: Response) {
            if(!response.isSuccessful){
                throw IOException("Unexpected code $response")
            }else{
                val body = response.body

                val jsonObject = JSONObject(body!!.string())

                sharedPrefs.edit().putString("SESSION_TOKEN", jsonObject.getString("session_token")).commit()

                sharedPrefs.edit().putString("SESSION_EXPIRATION", jsonObject.getString("session_expiration")).commit()

                sharedPrefs.edit().putString("UPDATE_TOKEN", jsonObject.getString("update_token")).commit()

            }
        }
    })

}

fun registerUser(User :CreateUser, sharedPrefs : SharedPreferences, callback: (CreateUser) -> Unit){
    //register a user

    val client = OkHttpClient()

    val moshi: Moshi = Moshi.Builder().build()
    val adapter : JsonAdapter<CreateUser> = moshi.adapter(CreateUser::class.java)
    val json :String = adapter.toJson(User)
    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

    //change the url POST /register/
    val postRequest = Request.Builder().post(requestBody).url("http://143.198.115.54:8080/posts/").build()

    client.newCall(postRequest).enqueue(object: Callback{
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()

        }

        override fun onResponse(call: Call, response: Response) {
            if(!response.isSuccessful){
                throw IOException("Unexpected code $response")
            }else{
                val body = response.body

                val jsonObject = JSONObject(body!!.string())

                sharedPrefs.edit().putString("SESSION_TOKEN", jsonObject.getString("session_token")).commit()

                sharedPrefs.edit().putString("SESSION_EXPIRATION", jsonObject.getString("session_expiration")).commit()

                sharedPrefs.edit().putString("UPDATE_TOKEN", jsonObject.getString("update_token")).commit()

            }
        }
    })

}


//need to fix -> add response body?
fun getUser(createUser :CreateUser, sharedPrefs : SharedPreferences, callback: (CreateUser) -> Unit){
    val client = OkHttpClient()

    val username = createUser.username
    val password = createUser.password
    var sessionToken = sharedPrefs.getString("SESSION_TOKEN", "Hamburger")


    val request = Request.Builder()
        .url("http://143.198.115.54:8080/posts/")//change the URL
        .header("Authorization", "Bearer $sessionToken")
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            } else {
                val body = response.body
                val moshi: Moshi = Moshi.Builder().build()
                val adapter : JsonAdapter<User> = moshi.adapter(User::class.java)
                var user = adapter.fromJson(body!!.source())

                val jsonObject = JSONObject(body.string())







            }
        }
    })
}