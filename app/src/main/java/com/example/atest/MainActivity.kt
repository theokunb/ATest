package com.example.atest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private val tag: String = "MainActivity"


    private lateinit var requestInput: TextInputEditText
    private lateinit var podsAdapter: SimpleAdapter
    private lateinit var progressBar: ProgressBar



    private val pods = mutableListOf(
        HashMap<String, String>().apply {
            put("Title", "Title 1")
            put("Content", "Content 1")
        },
        HashMap<String, String>().apply {
            put("Title", "Title 2")
            put("Content", "Content 2")
        },
        HashMap<String, String>().apply {
            put("Title", "Title 3")
            put("Content", "Content 3")
        },
        HashMap<String, String>().apply {
            put("Title", "Title 4")
            put("Content", "Content 4")
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        requestInput = findViewById(R.id.text_input_edit)
        val podslist: ListView = findViewById(R.id.pods_list)
        podsAdapter = SimpleAdapter(
            applicationContext,
            pods,
            R.layout.item_pod,
            arrayOf("Title", "Content"),
            intArrayOf(R.id.title, R.id.content)
        )

        podslist.adapter = podsAdapter

        val button: FloatingActionButton = findViewById(R.id.voice_input_button)
        button.setOnClickListener {
            Log.d(tag, "floating action button tapped")
        }

        progressBar = findViewById(R.id.progress_bar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_stop -> {
                Log.d(tag, "stop tapped")
                return true
            }
            R.id.action_clear -> {
                Log.d(tag, "clear tapped")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}