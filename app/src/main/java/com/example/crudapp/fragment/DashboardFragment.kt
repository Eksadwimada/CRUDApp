package com.example.crudapp.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crudapp.activity.LoginActivity
import com.example.crudapp.activity.RegisterActivity
import com.example.crudapp.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //mendapatkan UID pengguna yang sedang masuk
        val currentUserUid = auth.currentUser?.uid

        //menampilkan nama dan email dari database
        getUserData(currentUserUid)

        binding.btnLogout.setOnClickListener {
            signOut()
        }

        binding.btnDeleteAccount.setOnClickListener {
            deleteAccount(currentUserUid)
        }
    }

    // Fungsi untuk mendapatkan data pengguna dari Firebase Database
    private fun getUserData(uid: String?) {
        val userRef = database.getReference("users").child(uid.orEmpty())

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("username").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)

                    // Menampilkan nama pengguna dan email
                    binding.txtUsername.text = "$username"
                    binding.txtEmail.text = "$email"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Fungsi untuk logout
    private fun signOut() {
        auth.signOut()
        val i = Intent(context, LoginActivity::class.java)
        startActivity(i)
        activity?.finish()
    }

    // Fungsi untuk menghapus akun
    private fun deleteAccount(uid: String?) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Konfirmasi Hapus Akun")
        alertDialogBuilder.setMessage("Apakah Anda yakin ingin menghapus akun?")
        alertDialogBuilder.setPositiveButton("Ya") { dialogInterface, _ ->
            // User clicked "Ya", proceed with account deletion
            deleteAccountConfirmed(uid)
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Batal") { dialogInterface, _ ->
            // User clicked "Batal", do nothing
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteAccountConfirmed(uid: String?) {
        // Continue with the account deletion process
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Hapus data pengguna dari Realtime Database
                    database.getReference("users").child(uid.orEmpty()).removeValue()
                        .addOnCompleteListener { databaseTask ->
                            if (databaseTask.isSuccessful) {
                                Toast.makeText(context, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()
                                // Redirect ke halaman login atau halaman lainnya
                                val intent = Intent(context, RegisterActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            } else {
                                Toast.makeText(context, "Gagal menghapus akun", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Gagal menghapus akun", Toast.LENGTH_SHORT).show()
                }
            }
    }
}