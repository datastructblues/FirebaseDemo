package com.datastructblues.firebaseinstagramclone.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datastructblues.firebaseinstagramclone.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        storage = Firebase.storage
        firestore=Firebase.firestore

        registerLauncher()

    }

     fun uploadClicked(view: View){

         val uuid = UUID.randomUUID()
         val imageName = "$uuid.jpg"

         val reference = storage.reference
         val imageReference = reference.child("images").child(imageName)

         if(selectedPicture!=null){
             imageReference.putFile(selectedPicture!!).addOnSuccessListener(){
                 val uploadReference = reference.child("images").child(imageName)
                 uploadReference.downloadUrl.addOnSuccessListener{ url->
                     val downloadUrl = url.toString()
                     if(auth.currentUser!=null){
                         val postMap = hashMapOf<String,Any>()
                         postMap.put("downloadUrl",downloadUrl)
                         postMap.put("userEmail",auth.currentUser!!.email!!)
                         postMap.put("comment",binding.commentText.text.toString())
                         postMap.put("date",Timestamp.now())

                         firestore.collection("Posts")
                             .add(postMap)
                             .addOnSuccessListener {
                                 finish()
                             }.addOnFailureListener{
                                 Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                             }

                     }

                 }

             }.addOnFailureListener(){
                 Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
             }
         }


     }

    fun selectImage(view:View){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    // request permission
                    requestPermission()
                }.show()
            }else{
                requestPermission()
            }
        }else{
            intentToGallery()
        }

    }

    private fun registerLauncher(){
        activityResultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == RESULT_OK){
                val result_intent = result.data
                if(result_intent!=null){
                  selectedPicture = result_intent.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                intentToGallery()
            }else{
                Toast.makeText(this@UploadActivity,"Permission Needed!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun intentToGallery(){
        val intent_gallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intent_gallery)
    }
    private fun requestPermission(){
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}