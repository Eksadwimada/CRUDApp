package com.example.crudapp.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.crudapp.R
import com.example.crudapp.databinding.ActivityAddBinding
import com.example.crudapp.fragment.HomeFragment
import com.example.crudapp.model.DataModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.view.View
import java.io.ByteArrayOutputStream

class AddActivity : AppCompatActivity() {

    private lateinit var db : DatabaseReference
    private lateinit var binding: ActivityAddBinding

    var img: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun insertData(view: android.view.View) {
        val name = binding.nameEt.text.toString()
        val nim = binding.nimEt.text.toString()
        val prodi  = binding.prodiEt.text.toString()
        db = FirebaseDatabase.getInstance().getReference("data mahasiswa")
        val data = DataModel(name, nim, prodi, img)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val id = databaseReference.push().key
        db.child(id.toString()).setValue(data).addOnSuccessListener {
            binding.nameEt.text!!.clear()
            binding.nimEt.text!!.clear()
            binding.prodiEt.text!!.clear()
            img = ""
            Toast.makeText(this, "Data berhasil ditambah!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menambahkan data!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addImg (view: android.view.View) {
        var myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myFileIntent.setType("image/*")
        resultLauncher.launch(myFileIntent)

    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode== RESULT_OK) {
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                img = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.imgView.setImageBitmap(myBitmap)
                inputStream!!.close()
                Toast.makeText(this, "Menambahkan gambar!", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}