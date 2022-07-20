package com.datastructblues.firebaseinstagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.datastructblues.firebaseinstagramclone.R
import com.datastructblues.firebaseinstagramclone.adapter.FeedRecyclerAdapter
import com.datastructblues.firebaseinstagramclone.databinding.ActivityFeedBinding
import com.datastructblues.firebaseinstagramclone.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var postList:ArrayList<Post>
    private lateinit var feedAdapter:FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth=Firebase.auth
        db=Firebase.firestore

        postList = ArrayList<Post>()

        getData()

        binding.recyclerView.layoutManager=LinearLayoutManager(this)

        feedAdapter = FeedRecyclerAdapter(postList)

        binding.recyclerView.adapter = feedAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_post){
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        }else if ( item.itemId == R.id.logout){
            auth.signOut()

            val intent =Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getData(){
        val postRef = db.collection("Posts")

        //order posts with orderBy(...)
        postRef.orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            //value gives you the value, error gives error if there is a error.

            if(error!=null) {
                Toast.makeText(this@FeedActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if(value!=null){
                    if(!value.isEmpty){
                        val documents= value.documents


                        //clears arraylist for the next upload. so we dont upload same thing.
                        postList.clear()


                        for(document in documents){
                            //cast
                            val comment = document.get("comment") as String
                            val email = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            println(comment)
                            val post = Post(email,comment,downloadUrl)
                            postList.add(post)
                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}