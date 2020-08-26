package com.example.easyfindapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfindapp.R
import kotlinx.android.synthetic.main.skill_recycler_view_item.view.*

class SkillsRecyclerAdapter(private val skills: MutableList<String>) :
    RecyclerView.Adapter<SkillsRecyclerAdapter.SkillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SkillViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.skill_recycler_view_item, parent, false)
        )

    override fun getItemCount() = skills.size

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.onBindSkill()
    }

    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun onBindSkill() {
            itemView.recyclerSkillView.text = skills[adapterPosition]
        }
    }
}