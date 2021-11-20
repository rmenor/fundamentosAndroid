package io.keepcoding.chat.channels

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.chat.Channel
import io.keepcoding.chat.databinding.ViewChannelBinding
import io.keepcoding.chat.extensions.inflater
import java.util.*

class ChannelsAdapter(
	private val onChannelClick: (Channel) -> Unit,
	diffUtilCallback: DiffUtil.ItemCallback<Channel> = DIFF
) : ListAdapter<Channel, ChannelsAdapter.ChannelViewHolder>(diffUtilCallback) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder =
		ChannelViewHolder(parent, onChannelClick)

	override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
		holder.bind(getItem(position))
	}

	companion object {
		val DIFF = object : DiffUtil.ItemCallback<Channel>() {
			override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean = oldItem.id == newItem.id
			override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean = oldItem == newItem
		}
	}

	class ChannelViewHolder(
		parent: ViewGroup,
		private val onChannelClick: (Channel) -> Unit,
		private val binding: ViewChannelBinding = ViewChannelBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(channel: Channel) {
			binding.channelName.text = channel.name
			binding.channelImage.setImageResource(channel.channelImageRes)
			val df: Date = Date(channel.lastMessageTimestamp)
			binding.msDateTime.text = df.toString()
			binding.root.setOnClickListener { onChannelClick(channel) }
		}
	}
}