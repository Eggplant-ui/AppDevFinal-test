package com.example.hw6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Voting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)

        val ss:String = intent.getStringExtra("id").toString()
        //this id is used for the update call okhttp3
        var te :TextView = findViewById(R.id.takeTitle)
        te.text = ss

        val home : Button = findViewById(R.id.homeButton)
        home.setOnClickListener {
            var i = Intent(this, CloudNotesActivity::class.java)
            startActivity(i)
        }

        val upvote : Button = findViewById(R.id.button3)
        upvote.setOnClickListener{
            //this will call update(id, upvote)
        }

        val downvote : Button = findViewById(R.id.button4)
        downvote.setOnClickListener{
            //this will call update(id, downvote)

        }

    }
}