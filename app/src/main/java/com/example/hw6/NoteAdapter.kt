package com.example.hw4


import android.graphics.Color
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.hw6.Note
import com.example.hw6.R


class NoteAdapter (
    private val notelist : List<Note>,
    private val mAdapterOnClickHandler: AdapterOnClickHandler
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){

    interface AdapterOnClickHandler{
        fun onClick(title:String, body:String, poster:String, timestamp:String, id:Int)
    }
    class NoteViewHolder(view : View, mAdapterOnClickHandler: AdapterOnClickHandler): RecyclerView.ViewHolder(view){
        val titleText : TextView = view.findViewById(R.id.titleYY)
        val bodyText : TextView = view.findViewById(R.id.body)
        val button : ConstraintLayout = view.findViewById(R.id.id)
        val up : Button = view.findViewById(R.id.button5)
        val down : Button = view.findViewById(R.id.button6)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return NoteViewHolder(view, mAdapterOnClickHandler)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note : Note = notelist[position]

        holder.titleText.text = note.title

        holder.bodyText.text = note.body
        var red = 255- note.id

        holder.up.setOnClickListener{
            //call update(id, upvote)
            holder.titleText.text = "upvoted "+note.id.toString()
        }
        holder.down.setOnClickListener{
            holder.titleText.text = "downvoted "+note.id.toString()
            //call update(id, upvote
        }


        holder.itemView.setBackgroundColor(Color.argb(50,red, (note.id*1337)%255, 0))
        holder.titleText.setTextColor(Color.argb(200, red, (note.id*1337)%255, 0));

        holder.button.setOnClickListener{
            mAdapterOnClickHandler.onClick(note.title, note.body, note.hashedPoster, note.timestamp, note.id)
        }
    }


    override fun getItemCount(): Int {
        return notelist.size
    }

}
