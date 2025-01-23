package com.id22.dicodingevent.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.databinding.ItemEventListBinding

class EventListAdapter :
    ListAdapter<EventModel, EventListAdapter.RecyclerViewHolder>(DiffCallback) {

    private var actionAdapter: ActionAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val bind = ItemEventListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RecyclerViewHolder(bind)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickCallback(onItemClickCallback: ActionAdapter) {
        actionAdapter = onItemClickCallback
    }

    inner class RecyclerViewHolder(private val bind: ItemEventListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(data: EventModel) {
            bind.tvTitle.text = data.name
            bind.tvSummary.text = data.summary

            Glide.with(itemView.context)
                .load(data.imageLogo)
                .into(bind.ivCover)

            itemView.setOnClickListener {
                actionAdapter!!.onItemClick(data.id)
            }
        }
    }

    interface ActionAdapter {
        fun onItemClick(id: Int)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EventModel>() {
        override fun areItemsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
            return oldItem == newItem
        }
    }
}
