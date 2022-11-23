package com.example.hw6

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hw4.NoteAdapter


class CloudNotesActivity : AppCompatActivity(), NoteAdapter.AdapterOnClickHandler{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //have to delete this before turning in
        var noteslist = mutableListOf<Note>()




        val recyclerView : RecyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NoteAdapter(noteslist, this)

        val button2 : Button = findViewById(R.id.button)//R.id.button is New Note
        //eventually, this takes to localNoteActivity
        button2.setOnClickListener {

            val text = "Going to AddTake Activity"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()

            var i = Intent(this, EditNoteActivity::class.java)
            i.putExtra("newNote", true)
            startActivity(i)
        }



        val button : Button = findViewById(R.id.button2)//Sign in button
        button.setOnClickListener{
            val text = "Going to Sign In"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()

            var i = Intent(this, AuthenticationActivity::class.java)
            startActivity(i)
        }


        getNotes { myNote ->
            runOnUiThread {
                for(n in myNote){
                    noteslist.add(n)
                }
                recyclerView.adapter = NoteAdapter(noteslist, this)

          }
        }
    }

    override fun onClick(title: String, body: String, poster: String, timestamp:String, id:Int) {
        //update the upvote and downvote counts from here
        val text = "ID: "+id.toString()
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }
}