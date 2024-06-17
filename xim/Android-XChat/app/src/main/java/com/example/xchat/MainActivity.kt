package com.example.xchat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
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
    private var messages: ArrayList<Message>? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerAdapter? = null
    private var sendButton: ImageButton? = null
    private var msgInput: EditText? = null

    private var settings: ChatSettings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        // Set RecyclerView layout manager.
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView?.setLayoutManager(layoutManager)

        // Set an animation
        recyclerView?.setItemAnimator(DefaultItemAnimator())

        messages = ArrayList()
        adapter = RecyclerAdapter(messages)
        adapter?.setHasStableIds(true)

        // Make screen not flash
        val animator: ItemAnimator? = recyclerView?.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        recyclerView?.setItemAnimator(null)


        recyclerView?.setAdapter(adapter)
        sendButton = findViewById<View>(R.id.msgButton) as ImageButton
        msgInput = findViewById<View>(R.id.msgInput) as EditText

        sendButton!!.setOnClickListener {
            val message = msgInput!!.getText().toString()
            if (message.isNotEmpty()) {
                messages!!.add(Message(true, message))
                val newPosition = messages!!.size - 1
                adapter!!.notifyItemInserted(newPosition)
                recyclerView?.scrollToPosition(newPosition)
                msgInput!!.setText("")
                sendChatRequest(message)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Get SharedPreferences
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)

        // Retrieve values from SharedPreferences
        val url =
            sharedPreferences.getString("url", "http://101.201.111.141:8000/v1/chat/completions")
        val modelName = sharedPreferences.getString("model_name", "chatglm2-6b")
        val apiKey = sharedPreferences.getString("api_key", "")
        val maxTokensString = sharedPreferences.getString("max_tokens", "512")
        val maxTokens = try {
            maxTokensString!!.toInt()
        } catch (e: NumberFormatException) {
            512
        }

        // Create a Settings object
        settings = ChatSettings(url, modelName, apiKey, maxTokens)
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendChatRequest(message: String) {
        val client = OkHttpClient()
        val url = settings!!.url

        // Create a JSONObject and put the values into it
        val json = JSONObject()
        try {
            json.put("model", settings!!.modelName)

            val promptArray = JSONArray()
            val promptObject = JSONObject()
            promptObject.put("role", "user")
            promptObject.put("content", message)
            promptArray.put(promptObject)
            json.put("messages", promptArray)

            json.put("max_tokens", settings!!.maxTokens)
            json.put("temperature", 0)
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
            .url(url)
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
                }
            }
        })
    }

    // isFirst: first token/word or not
    private fun onReply(reply: String, position: Int = -1): Int {
        return if (position == -1) {
            messages!!.add(Message(false, reply))
            val newPosition = messages!!.size - 1
            adapter!!.notifyItemInserted(newPosition)
            recyclerView!!.scrollToPosition(newPosition)
            newPosition
        } else {
            messages?.get(position)?.message = reply
            adapter!!.notifyItemChanged(position)
            recyclerView!!.scrollToPosition(position)
            position
        }
    }
}