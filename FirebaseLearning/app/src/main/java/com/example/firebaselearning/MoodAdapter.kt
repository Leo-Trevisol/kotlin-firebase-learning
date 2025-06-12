package com.example.firebaselearning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaselearning.databinding.ItemMoodBinding

class MoodAdapter(private val lista: List<Mood>) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    class MoodViewHolder(val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMoodBinding.inflate(inflater, parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = lista[position]
        holder.binding.txtData.text = mood.data
        holder.binding.txtHumor.text = mood.humor
        holder.binding.cardView.setCardBackgroundColor(mood.cor)
    }

    override fun getItemCount(): Int = lista.size
}