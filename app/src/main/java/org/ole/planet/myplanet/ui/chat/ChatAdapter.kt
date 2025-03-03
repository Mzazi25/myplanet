package org.ole.planet.myplanet.ui.chat

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.ole.planet.myplanet.databinding.ItemAiResponseMessageBinding
import org.ole.planet.myplanet.databinding.ItemUserMessageBinding

class ChatAdapter(private val chatList: ArrayList<History>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var textUserMessageBinding: ItemUserMessageBinding
    private lateinit var textAiMessageBinding: ItemAiResponseMessageBinding

    private val VIEW_TYPE_QUERY = 1
    private val VIEW_TYPE_RESPONSE = 2

    class QueryViewHolder(private val textUserMessageBinding: ItemUserMessageBinding) : RecyclerView.ViewHolder(textUserMessageBinding.root) {
        fun bind(query: String) {
            textUserMessageBinding.textGchatMessageMe.text = query
        }
    }

    class ResponseViewHolder(private val textAiMessageBinding: ItemAiResponseMessageBinding) : RecyclerView.ViewHolder(textAiMessageBinding.root) {
        fun bind(response: String) {
            val typingDelayMillis = 10L
            val typingAnimationDurationMillis = response.length * typingDelayMillis
            textAiMessageBinding.textGchatMessageOther.text = ""
            Handler().postDelayed({
                animateTyping(response, typingDelayMillis)
            }, typingAnimationDurationMillis)
        }
        private fun animateTyping(response: String, typingDelayMillis: Long) {
            var currentIndex = 0
            val typingRunnable = object : Runnable {
                override fun run() {
                    if (currentIndex < response.length) {
                        textAiMessageBinding.textGchatMessageOther.text = response.substring(0, currentIndex + 1)
                        currentIndex++
                        Handler().postDelayed(this, typingDelayMillis)
                    }
                }
            }
            Handler().postDelayed(typingRunnable, typingDelayMillis)
        }
    }
    fun addQuery(query: String) {
        chatList.add(History(query, null))
        notifyItemInserted(chatList.size - 1)
    }
    fun addResponse(response: String) {
        chatList.add(History(null, response))
        notifyItemInserted(chatList.size - 1)
    }
    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_QUERY else VIEW_TYPE_RESPONSE
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_QUERY -> {
                textUserMessageBinding = ItemUserMessageBinding.inflate(LayoutInflater.from(context), parent, false)

                QueryViewHolder(textUserMessageBinding)
            }
            VIEW_TYPE_RESPONSE -> {
                textAiMessageBinding = ItemAiResponseMessageBinding.inflate(LayoutInflater.from(context), parent, false)

                ResponseViewHolder(textAiMessageBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatItem = chatList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_QUERY -> {
                val queryViewHolder = holder as QueryViewHolder
                chatItem.query?.let { queryViewHolder.bind(it) }
            }
            VIEW_TYPE_RESPONSE -> {
                val responseViewHolder = holder as ResponseViewHolder
                chatItem.response?.let { responseViewHolder.bind(it) }
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    override fun getItemCount(): Int {
        return chatList.size
    }
}