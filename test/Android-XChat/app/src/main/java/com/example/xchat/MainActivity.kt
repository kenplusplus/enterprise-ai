package com.example.xchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    private var messages: ArrayList<Message> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerAdapter? = null
    private var sendButton: ImageButton? = null
    private var msgInput: EditText? = null

    private var settings: ChatSettings? = null

    // If user scrolls, don't scroll to the bottom
    private var isUserScrolling = false

    // List of HTTP clients
    private var httpClients: ArrayList<OkHttpClient> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        // Set RecyclerView layout manager.
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView?.setLayoutManager(layoutManager)

        // Set an animation
        recyclerView?.setItemAnimator(DefaultItemAnimator())

        adapter = RecyclerAdapter(messages, baseContext)
        adapter?.setHasStableIds(true)

        // Make screen not flash
//        val animator: ItemAnimator? = recyclerView?.itemAnimator
//        if (animator is SimpleItemAnimator) {
//            animator.supportsChangeAnimations = false
//        }
        recyclerView?.setItemAnimator(null)

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isUserScrolling = true
                }
            }
        })


        recyclerView?.setAdapter(adapter)
        sendButton = findViewById<View>(R.id.msgButton) as ImageButton
        msgInput = findViewById<View>(R.id.msgInput) as EditText

        sendButton!!.setOnClickListener {
            val message = msgInput!!.getText().toString()
            if (message.isNotEmpty()) {
                val newPosition = messages.size - 1
                adapter!!.notifyItemInserted(newPosition)
                recyclerView?.scrollToPosition(newPosition)
                msgInput!!.setText("")
                isUserScrolling = false
                sendChatRequest(message)
                messages.add(Message(true, message))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Create a Settings object
        settings = ChatSettings.fromPreferences(this);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.action_clear) {
            for (client in httpClients) {
                client.dispatcher.cancelAll()
            }
            messages.clear()
            adapter?.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun composeMessage(message: String): JSONArray {
        val historyRounds = settings!!.historyRounds
        val systemPrompt = settings!!.systemPrompt

        val promptArray = JSONArray()
        val promptSystem = JSONObject()
        promptSystem.put("role", "system")
        promptSystem.put("content", systemPrompt)
        promptArray.put(promptSystem)

        val offset: Int = if (messages.size > historyRounds * 2) messages.size - historyRounds * 2 else 0
        var i = offset
        while (i < messages.size) {
            val msg = messages[i]
            if (msg.isFromUser) {
                val promptObject = JSONObject()
                promptObject.put("role", "user")
                promptObject.put("content", msg.message)
                promptArray.put(promptObject)
            } else {
                val promptObject = JSONObject()
                promptObject.put("role", "assistant")
                promptObject.put("content", msg.message)
                promptArray.put(promptObject)
            }
            i += 1
        }

        val promptObject = JSONObject()
        promptObject.put("role", "user")
        promptObject.put("content", message)
        promptArray.put(promptObject)
        return promptArray
    }

    private fun sendChatRequest(message: String) {
        val client = OkHttpClient()
        httpClients.add(client)

        // Create a JSONObject and put the values into it
        val json = JSONObject()
        try {
            json.put("model", settings!!.modelName)
            json.put("messages", composeMessage(message))

            json.put("max_tokens", settings!!.maxTokens)
            json.put("temperature", settings!!.temperature)
            json.put("top_p", settings!!.topP)
            json.put("top_k", settings!!.topK)
            json.put("repetition_penalty", settings!!.penalty)

            json.put("stream", true)
        } catch (e: JSONException) {
            onReply("Request failed: " + e.message)
            return
        }

        // Convert the JSONObject to a JSON string
        val jsonString = json.toString()
        val body: RequestBody =
            jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())
        val builder = Request.Builder()
            .url(settings!!.url)
            .post(body)
        if (settings!!.apiKey != null) {
            builder.addHeader("Authorization", "Bearer " + settings!!.apiKey)
        }

        val request: Request = builder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread { onReply("Request failed: " + e.message) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread { onReply("Unexpected code $response") }
                    return
                }
                // Read the response as a stream
                BufferedReader(InputStreamReader(response.body!!.byteStream())).use { reader ->
                    val result = StringBuilder()
                    var line: String?
                    var position: Int = -1
                    line = reader.readLine()
                    while (reader.readLine().also { line = it } != null) {
                        if (line!!.startsWith("data: ")) {
                            line = line!!.substring(6) // Remove the "data: " prefix
                            try {
                                val jsonObject = JSONObject(line!!)
                                val choicesArray = jsonObject.getJSONArray("choices")
                                if (choicesArray.length() > 0) {
                                    val firstChoice = choicesArray.getJSONObject(0)
                                    val delta = firstChoice.getJSONObject("delta")
                                    val content = delta.getString("content")
                                    runOnUiThread {
                                        result.append(content)
                                        position = onReply(result.toString(), position)
                                    }
                                }
                            } catch (e: Exception) {
                                // The response like "data: [DONE]" or any other parsing error will reach here
                                Log.d("DEBUG", e.toString())
                            }
                        }
                    } // end while
                    httpClients.remove(client)
                }
            }
        })
    }

    // isFirst: first token/word or not
    private fun onReply(reply: String, position: Int = -1): Int {
        return if (position == -1) {
            messages.add(Message(false, reply))
            val newPosition = messages.size - 1
            adapter!!.notifyItemInserted(newPosition)
            if (!isUserScrolling) recyclerView!!.scrollToPosition(newPosition)
            newPosition
        } else {
            // No message at the given position (maybe cleared)
            if (messages.size <= position) return position

            messages[position].message = reply
            adapter!!.notifyItemChanged(position)
            if (!isUserScrolling) recyclerView!!.scrollToPosition(position)
            position
        }
    }
}