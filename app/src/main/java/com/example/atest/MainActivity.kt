package com.example.atest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {



    //XRK75K-VXXJAX2753
    private val tag: String = "MainActivity"


    private lateinit var requestInput: TextInputEditText
    private lateinit var podsAdapter: SimpleAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var waengine: WAEngine


    private val pods = mutableListOf<HashMap<String,String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        initWolframEngine()

    }

    private fun initViews() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        requestInput = findViewById(R.id.text_input_edit)
        requestInput.setOnEditorActionListener { v, actionid, Event ->
            if(actionid == EditorInfo.IME_ACTION_DONE){
                pods.clear()
                podsAdapter.notifyDataSetChanged()

                val question = requestInput.text.toString()
                askWolfram(question)
            }
            return@setOnEditorActionListener false
        }



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
                requestInput.text?.clear()
                pods.clear()
                podsAdapter.notifyDataSetChanged()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun initWolframEngine(){
        waengine = WAEngine().apply {
            appID = "XRK75K-VXXJAX2753"
            addFormat("plaintext")
        }
    }

    fun showSnackbar(message:String){
        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_INDEFINITE).apply {
            setAction(android.R.string.ok){
                dismiss()
            }
            show()
        }
    }

    fun askWolfram(request:String){
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val query = waengine.createQuery().apply {
                input = request
            }
            kotlin.runCatching {
                waengine.performQuery(query)
            }.onSuccess { result->
                withContext(Dispatchers.Main){
                    progressBar.visibility = View.GONE
                    if(result.isError){
                        showSnackbar(result.errorMessage)
                        return@withContext
                    }

                    if(!result.isSuccess){
                        requestInput.error = getString(R.string.error_do_not_understand)
                        return@withContext
                    }

                    for(pod in result.pods){
                        if(pod.isError)
                            continue
                        val content = StringBuilder()
                        for(subpod in pod.subpods){
                            for(element in subpod.contents){
                                if(element is WAPlainText){
                                    content.append(element.text)
                                }
                            }
                        }
                        pods.add(0,HashMap<String,String>().apply {
                            put("Title",pod.title)
                            put("Content",content.toString())
                        })
                    }
                    podsAdapter.notifyDataSetChanged()
                }
            }.onFailure { t->
                withContext(Dispatchers.Main){
                    progressBar.visibility = View.GONE
                    showSnackbar(t.message?: getString(R.string.error_something_went_wrong))
                }
            }

        }
    }
}