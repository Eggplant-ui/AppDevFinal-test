package com.example.hw6

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_layout)

        var usernameField: EditText = findViewById(R.id.username)
        var passwordField: EditText = findViewById(R.id.password)

        val sharedPref = getSharedPreferences("TOKENS", Context.MODE_PRIVATE)


        var buttonSignUp: Button = findViewById(R.id.signup_button)

        buttonSignUp.setOnClickListener {
            val a = CreateUser(usernameField.text.toString(), passwordField.text.toString())

            registerUser(a, sharedPref) {
                var name = usernameField.text.toString()
                var text = "Thanks for signing up, $name."
                var duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                var sessionToken = sharedPref.getString("SESSION_TOKEN", "Hamburger")
                var sessionExpiration = sharedPref.getString("SESSION_EXPIRATION", "Hamburger")
                var updateToken = sharedPref.getString("UPDATE_TOKEN", "Hamburger")
                if (sharedPref.getString("SESSION_TOKEN", "Hamburger") != "Hamburger") {
                    toast.show()
                    var intent = Intent(this, CloudNotesActivity::class.java)
                    intent.putExtra("SESSION_TOKEN", sessionToken)
                    intent.putExtra("SESSION_EXPIRATION", sessionExpiration)
                    intent.putExtra("UPDATE_TOKEN", updateToken)
                    setResult(1, intent)
                    finish()
                } else {
                    text = "Sorry but sign up failed."
                    duration = Toast.LENGTH_LONG
                    val toasted = Toast.makeText(applicationContext, text, duration)
                    toasted.show()
                }
            }

            var buttonLogIn: Button = findViewById(R.id.login_button)

            buttonLogIn.setOnClickListener {
                val a = CreateUser(usernameField.text.toString(), passwordField.text.toString())

                logUser(a, sharedPref) {}
                var name = usernameField.text.toString()
                var text = "Thanks for logging in, $name."
                var duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                var sessionToken = sharedPref.getString("SESSION_TOKEN", "Hamburger")
                var sessionExpiration = sharedPref.getString("SESSION_EXPIRATION", "Hamburger")
                var updateToken = sharedPref.getString("UPDATE_TOKEN", "Hamburger")
                if (sessionToken != "Hamburger") {
                    toast.show()
                    var intent = Intent(this, CloudNotesActivity::class.java)
                    intent.putExtra("SESSION_TOKEN", sessionToken)
                    intent.putExtra("SESSION_EXPIRATION", sessionExpiration)
                    intent.putExtra("UPDATE_TOKEN", updateToken)
                    setResult(1, intent)
                    finish()
                } else {
                    text = "Sorry but login failed."
                    duration = Toast.LENGTH_LONG
                    val toasted = Toast.makeText(applicationContext, text, duration)
                    toasted.show()
                }

            }


        }
    }
}
