package com.datastructblues.firebaseinstagramclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.datastructblues.firebaseinstagramclone.databinding.RecyclerRowBinding
import com.datastructblues.firebaseinstagramclone.model.Post

class FeedRecyclerAdapter(private val postList: ArrayList<Post>):
    RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) :RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return PostHolder(binding)

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentText.text=postList.get(position).comment
        Glide.with(holder.itemView).load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)


    }

    override fun getItemCount(): Int {
        return postList.size
    }
}