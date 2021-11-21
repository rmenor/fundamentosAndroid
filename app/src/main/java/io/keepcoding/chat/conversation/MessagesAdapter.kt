package io.keepcoding.chat.conversation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.chat.Message
import io.keepcoding.chat.R
import io.keepcoding.chat.Repository
import io.keepcoding.chat.databinding.ViewMessageBinding
import io.keepcoding.chat.databinding.ViewMessageUserBinding
import io.keepcoding.chat.extensions.inflater

class MessagesAdapter(
	diffUtilCallback: DiffUtil.ItemCallback<Message> = DIFF
) : ListAdapter<Message, RecyclerView.ViewHolder>(diffUtilCallback) {

	private val VIEW_TYPE_USER = 1 // Indica que es el mismo usuario

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = run {
		if (viewType == VIEW_TYPE_USER)
			MessageViewUserHolder(parent)
		else
			MessageViewHolder(parent)
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = run {
		when (holder) {
			is MessageViewHolder -> holder.bind(getItem(position))
			is MessageViewUserHolder -> holder.bind(getItem(position))
		}
	}

	//
	override fun getItemViewType(position: Int): Int {
		var isUser = 0;
		val message = currentList[position]
		if (message.sender.id == Repository.currentSender.id) {
			isUser = VIEW_TYPE_USER
		}
		return isUser
	}

	companion object {
		val DIFF = object : DiffUtil.ItemCallback<Message>() {
			override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
				oldItem == newItem
		}
	}

	class MessageViewHolder(
		parent: ViewGroup,
		private val binding: ViewMessageBinding = ViewMessageBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.messageText.text = "${message.sender.name}: ${message.text}"
			binding.messageImage.setImageResource(message.sender.profileImageRes)
		}
	}

	// Nuevo holder
	class MessageViewUserHolder(
		parent: ViewGroup,
		private val binding: ViewMessageUserBinding = ViewMessageUserBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.messageText.text = "${message.sender.name}: ${message.text}"
			//binding.messageImage.setImageResource(message.sender.profileImageRes)
		}
	}
}