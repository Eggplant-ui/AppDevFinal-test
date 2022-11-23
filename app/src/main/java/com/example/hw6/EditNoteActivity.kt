package com.example.hw6

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.hw4.NoteAdapter


//clicking on a note in the recyclerView brings us to this class
//it presents said Note's title and body
//

class EditNoteActivity : AppCompatActivity(), NoteAdapter.AdapterOnClickHandler {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_note_layout)

        //set a boolean newNote
        //then, within save button, if newNote is true then make a call to networkingUtils
        //if newNote is false, save to repository, then redirect back to Cloud Notes

        //add name to take?
        val name = intent.extras?.getString("name") ?: "Anonymous"


        var titleText: TextView = findViewById(R.id.takeTitle)
        var titleText2: TextView = findViewById(R.id.nameTitle)

        var editTextTake: EditText = findViewById(R.id.editTake)

        var editTextBody: EditText = findViewById(R.id.editName)

        val homeButton: Button = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {
            var intent = Intent(this, CloudNotesActivity::class.java)
            startActivity(intent)
        }

        var button: Button = findViewById(R.id.button3)





        button.setOnClickListener{
            val a = NoteOut(editTextTake.text.toString(), editTextBody.text.toString(), "adw98")

            postNote(a){
                val text = "Added note to cloud: $it"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
            var intent = Intent(this, CloudNotesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(title: String, body: String, poster: String, timestamp: String, id: Int) {
        val text = "this is a note"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }
}