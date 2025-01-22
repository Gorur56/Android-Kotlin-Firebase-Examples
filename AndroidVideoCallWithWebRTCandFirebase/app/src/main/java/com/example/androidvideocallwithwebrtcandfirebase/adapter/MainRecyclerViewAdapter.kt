package com.example.androidvideocallwithwebrtcandfirebase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvideocallwithwebrtcandfirebase.R
import com.example.androidvideocallwithwebrtcandfirebase.databinding.ItemMainRecyclerViewBinding

class MainRecyclerViewAdapter(private val listener: Listener) : RecyclerView.Adapter<MainRecyclerViewAdapter.MainRecyclerViewHolder>(){

    private var usersList:List<Pair<String,String>>?=null

    fun updateList(list:List<Pair<String,String>>){
        this.usersList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewHolder {
        val binding = ItemMainRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return MainRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainRecyclerViewHolder, position: Int) {
        usersList?.let { list->
            val user = list[position]
            holder.bind(user,{
                listener.onVideoCallClicked(it)
            },{
                listener.onAudioCallClicked(it)
            })
        }
    }

    interface Listener {
        fun onVideoCallClicked(username:String)
        fun onAudioCallClicked(username:String)
    }

    override fun getItemCount(): Int {
        return usersList?.size ?: 0
    }

    class MainRecyclerViewHolder(private val binding: ItemMainRecyclerViewBinding):
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind(
            user: Pair<String, String>,
            videoCallClicked: (String) -> Unit,
            audioCallClicked: (String) -> Unit
        ) {
            binding.apply {
                when (user.second) {
                    "ONLINE" -> {
                        imageViewVideoCallBtn.isVisible = true
                        imageViewAudioCallBtn.isVisible = true
                        imageViewVideoCallBtn.setOnClickListener {
                            videoCallClicked.invoke(user.first)
                        }
                        imageViewAudioCallBtn.setOnClickListener {
                            audioCallClicked.invoke(user.first)
                        }
                        textviewStatusTv.setTextColor(context.resources.getColor(R.color.light_green, null))
                        textviewStatusTv.text = "Online"
                    }

                    "OFFLINE" -> {
                        imageViewVideoCallBtn.isVisible = false
                        imageViewAudioCallBtn.isVisible = false
                        textviewStatusTv.setTextColor(context.resources.getColor(R.color.red, null))
                        textviewStatusTv.text = "Offline"
                    }

                    "IN_CALL" -> {
                        imageViewVideoCallBtn.isVisible = false
                        imageViewAudioCallBtn.isVisible = false
                        textviewStatusTv.setTextColor(context.resources.getColor(R.color.yellow, null))
                        textviewStatusTv.text = "In Call"
                    }
                }
                textviewUsernameTv.text = user.first
            }
        }
    }

}