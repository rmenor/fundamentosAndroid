package io.keepcoding.chat.conversation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.keepcoding.chat.Channel
import io.keepcoding.chat.Message
import io.keepcoding.chat.R
import io.keepcoding.chat.Repository
import io.keepcoding.chat.common.TextChangedWatcher
import io.keepcoding.chat.databinding.ActivityConversationBinding

class ConversationActivity : AppCompatActivity() {

	private val binding: ActivityConversationBinding by lazy {
		ActivityConversationBinding.inflate(layoutInflater)
	}
	private val vm: ConversationViewModel by viewModels {
		ConversationViewModel.ConversationViewModelProviderFactory(Repository)
	}
	private val messagesAdapter: MessagesAdapter = MessagesAdapter()
	private val channelId: String by lazy { intent.getStringExtra(CHANNEL_ID)!! }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		binding.conversation.apply {
			layoutManager = LinearLayoutManager(context).apply {
				stackFromEnd = true
			}
			adapter = messagesAdapter
		}

		vm.state.observe(this) {
			when (it) {
				is ConversationViewModel.State.MessagesReceived -> {
					renderMessages(it.messages)
					hideLoading()
				}
				is ConversationViewModel.State.Error.ErrorLoading -> {
					showError("Se ha producido un error") // Error
					hideLoading()
				}
				is ConversationViewModel.State.Error.ErrorWithMessages -> {
					showError("Se ha producido un error")  // Error
					renderMessages(it.messages)
					hideLoading()
				}
				is ConversationViewModel.State.LoadingMessages.Loading -> {
					showLoading()
				}
				is ConversationViewModel.State.LoadingMessages.LoadingWithMessages -> {
					renderMessages(it.messages)
				}
			}
		}
		vm.message.observe(this) {
			binding.tvMessage.apply {
				setText(it)
				setSelection(it.length)

				// Desactivar botón
				if (it.isBlank()) {
					binding.sendButton.isEnabled = false
					binding.sendButton.setBackgroundColor(Color.WHITE);
				} else {
					binding.sendButton.isEnabled = true
					binding.sendButton.setBackgroundColor(Color.GREEN);
				}
			}
		}
		binding.tvMessage.addTextChangedListener(TextChangedWatcher(vm::onInputMessageUpdated))
		binding.sendButton.setOnClickListener { vm.sendMessage(channelId) }
	}

	private fun renderMessages(messages: List<Message>) {
		messagesAdapter.submitList(messages) {
			if (messages.count()<=0) {
				binding.emptyMessages.isVisible = true
				binding.emptyText.text = "No hay mensajes, se el primero en escribir"
				binding.emptyImage.setImageResource(R.drawable.ic_empty_message)
				binding.conversation.visibility = View.GONE
			} else {
				binding.emptyMessages.isVisible = false
				binding.conversation.isVisible = true
			}
			binding.conversation.smoothScrollToPosition(messages.size) }
	}

	private fun showLoading() {
		binding.pbChannel.isVisible = true;
	}

	private fun hideLoading() {
		binding.pbChannel.isVisible = false;
	}

	override fun onResume() {
		super.onResume()
		vm.loadConversation(channelId)
	}

	private fun showError(error: String) {
		Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
	}

	companion object {
		const val CHANNEL_ID = "CHANNEL_ID"

		fun createIntent(context: Context, channel: Channel): Intent =
			Intent(
				context,
				ConversationActivity::class.java
			).apply {
				putExtra(CHANNEL_ID, channel.id)
			}
	}
}