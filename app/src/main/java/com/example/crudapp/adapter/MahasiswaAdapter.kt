package com.example.crudapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crudapp.R
import com.example.crudapp.model.DataModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class MahasiswaAdapter(
    private val userList: MutableList<DataModel>,  // Change this to ArrayList
    private val context: Context,
    private val editClickListener: (DataModel) -> Unit,
    private val deleteClickListener: (DataModel) -> Unit,
    private val databaseReference: DatabaseReference,
) : RecyclerView.Adapter<MahasiswaAdapter.MyViewHolder>() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("data mahasiswa")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.nama.text = currentItem.nama
        holder.nim.text = currentItem.nim
        holder.prodi.text = currentItem.prodi

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.img)
            .into(holder.img)

        holder.btnEdit.setOnClickListener {
            editClickListener.invoke(currentItem)
        }
        holder.btnDelete.setOnClickListener {
            deleteClickListener.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.ttNamaMahasiswa)
        val nim: TextView = itemView.findViewById(R.id.ttNimMahasiswa)
        val prodi: TextView = itemView.findViewById(R.id.ttProdiMahasiswa)
        val img: ImageView = itemView.findViewById(R.id.imgView)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        init {
            btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val mahasiswa = userList[position]
                    editClickListener.invoke(mahasiswa)
                }
            }
            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val mahasiswa = userList[position]
                    deleteClickListener.invoke(mahasiswa)
                }
            }
        }
    }

    fun setData(newList: List<DataModel>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}