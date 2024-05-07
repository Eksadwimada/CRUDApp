package com.example.crudapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudapp.activity.AddActivity
import com.example.crudapp.adapter.MahasiswaAdapter
import com.example.crudapp.databinding.FragmentHomeBinding
import com.example.crudapp.model.DataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MahasiswaAdapter

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("data_mahasiswa")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MahasiswaAdapter(
            mutableListOf(),
            requireContext(),
            this::editItem,
            this::deleteItem,
            databaseReference
        )
        recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddActivity::class.java))
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = mutableListOf<DataModel>()
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(DataModel::class.java)
                    item?.let { data.add(it) }
                }
                Log.d("HomeFragment", "Data retrieved: $data")
                adapter.setData(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("HomeFragment", "Error retrieving data: ${databaseError.message}")
            }
        })
    }

    private fun editItem(data: DataModel) {
        // Handle item edit click
    }

    private fun deleteItem(data: DataModel) {
        // Handle item delete click
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
