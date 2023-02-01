package com.example.fbtest

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.drawable.toBitmap
import com.example.fbtest.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var selectImg: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")




        binding.button.setOnClickListener {
            if(binding.editTextTextPersonName.text.isNotEmpty() && binding.editTextTextPersonName2.text.isNotEmpty()
                && binding.editTextTextPersonName3.text.isNotEmpty()){
                uploadImg()
                Toast.makeText(this@MainActivity ," Added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity ," Error fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        val lau = registerForActivityResult(ActivityResultContracts.GetContent()) {


            if(it != null){
                selectImg = it
                binding.imageView.setImageURI(selectImg)
                binding.button.visibility = View.VISIBLE
            }

        }

        storage = FirebaseStorage.getInstance()



        binding.button2.setOnClickListener {

            lau.launch("image/*")


        }

    }

    private fun uploadImg() {

        val storRef = storage.getReference("images/${Math.random() * 8}")


        storRef.putFile(selectImg).addOnCompleteListener{
            if(it.isSuccessful){
                storRef.downloadUrl.addOnSuccessListener { task ->
                    val name = binding.editTextTextPersonName2.text.toString()
                    val idea = binding.editTextTextPersonName.text.toString()
                    val url = binding.editTextTextPersonName3.text.toString()



                    if (inputCheck(name, idea, url)) {
                        val empId = dbRef.push().key!!

                        val user = MainModel(empId, name, idea, task.toString(), url)
                        dbRef.child(empId).setValue(user)

                        binding.editTextTextPersonName.text.clear()
                        binding.editTextTextPersonName2.text.clear()
                        binding.editTextTextPersonName3.text.clear()
                    }
                }
            }
        }




    }


    private fun inputCheck(name: String, idea: String, url: String): Boolean {
        return name.isNotEmpty() && idea.isNotEmpty() && url.isNotEmpty()
    }

}
