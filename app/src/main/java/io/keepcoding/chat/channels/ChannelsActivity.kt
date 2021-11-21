package io.keepcoding.chat.channels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import io.keepcoding.chat.Channel
import io.keepcoding.chat.Repository
import io.keepcoding.chat.conversation.ConversationActivity
import io.keepcoding.chat.databinding.ActivityChannelsBinding

class ChannelsActivity : AppCompatActivity() {
	val binding: ActivityChannelsBinding by lazy { ActivityChannelsBinding.inflate(layoutInflater) }
	val channelsAdapter: ChannelsAdapter by lazy { ChannelsAdapter(::openChannel) }
	val vm: ChannelsViewModel by viewModels {
		ChannelsViewModel.ChannelsViewModelProviderFactory(Repository)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		binding.topics.apply {
			adapter = channelsAdapter
			addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
		}

		// Lookup the swipe container view
		val swipeContainer = binding.swipeContainer
		swipeContainer.setOnRefreshListener {
			vm.loadChannels()
			swipeContainer.setRefreshing(false)
		}

		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
			android.R.color.holo_green_light,
			android.R.color.holo_orange_light,
			android.R.color.holo_red_light);

		vm.state.observe(this) {
			when (it) {
				is ChannelsViewModel.State.ChannelsReceived -> {
					channelsAdapter.submitList(it.channels)
					hideLoading()
				}
				is ChannelsViewModel.State.Error.ErrorLoading -> {
					Toast.makeText(this, "Se ha producido un error de carga", Toast.LENGTH_SHORT).show() // Error
					hideLoading()
				}
				is ChannelsViewModel.State.Error.ErrorWithChannels -> {
					Toast.makeText(this, "Se ha producido un error al recibir los canales", Toast.LENGTH_SHORT).show() // Error
					channelsAdapter.submitList(it.channels)
					hideLoading()
				}
				is ChannelsViewModel.State.LoadingChannels.Loading -> {
					showLoading()
				}
				is ChannelsViewModel.State.LoadingChannels.LoadingWithChannels -> {
					channelsAdapter.submitList(it.channels)
				}
			}
		}
	}

	private fun showLoading() {
		binding.pbChannel.isVisible = true;
	}

	private fun hideLoading() {
		binding.pbChannel.isVisible = false;
	}

	override fun onResume() {
		super.onResume()
		vm.loadChannels()
	}



	private fun openChannel(channel: Channel) {
		startActivity(ConversationActivity.createIntent(this, channel))
	}
}