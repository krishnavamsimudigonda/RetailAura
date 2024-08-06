package com.adina.retailaura.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adina.retailaura.Models.ChatMessage
import com.adina.retailaura.Models.Product
import com.adina.retailaura.R
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
//        apiKey = <API Key>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Display typing indicator and send initial message to AI
        chatMessages.add(ChatMessage("Retail Aura AI", "Typing..."))
        chatAdapter.notifyDataSetChanged()
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)

        CoroutineScope(Dispatchers.IO).launch {
            generateResponse("Hi")
        }

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                // Add user message to the list
                chatMessages.add(ChatMessage("ME", message))
                chatAdapter.notifyDataSetChanged()
                inputMessage.text.clear()

                // Add typing indicator
                chatMessages.add(ChatMessage("Retail Aura AI", "Typing..."))
                chatAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)

                CoroutineScope(Dispatchers.IO).launch {
                    generateResponse(message)
                }
            }
        }
    }

    private suspend fun generateResponse(message: String) {
        val products = intent.getParcelableArrayListExtra<Product>("products")
        val productInfo =
            products?.joinToString(separator = "\n") { "Name: ${it.name}, Price: ${it.price}, Discount Price: ${it.discountPrice}, Quantity: ${it.quantity}" }

        // Create a prompt for the generative model
        val prompt = """
            You are Retail Aura AI, a friendly and helpful assistant specializing in enhancing customer experiences by recommending products and providing smooth, seamless service. Your goal is to assist users by making personalized product suggestions and answering their queries just like a friend would.

            Current User Message: "$message"

            Products in Cart:
            $productInfo

            Previous Chat History:
            ${chatMessages.joinToString("\n") { "${it.sender}: ${it.message}" }}
            
            Based on the products the user has selected, suggest additional items they might like and assist with any questions they have. Keep your responses concise and friendly, making the user feel comfortable and understood.
            Also add how much they saved in shopping with Retail Aura.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)

            withContext(Dispatchers.Main) {
                // Remove the typing indicator
                if (chatMessages.isNotEmpty() && chatMessages.last().message == "Typing...") {
                    chatMessages.removeAt(chatMessages.size - 1)
                }
                // Add the actual response from the AI
                chatMessages.add(ChatMessage("Retail Aura AI", response.text.toString()))
                chatAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ChatActivity, "Failed to get response: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
