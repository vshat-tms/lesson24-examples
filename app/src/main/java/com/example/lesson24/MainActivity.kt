package com.example.lesson24

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    private val random = Random()

    val handler = Handler(Looper.getMainLooper())

    val executor = Executors.newSingleThreadExecutor()

    private var task: MyAsyncTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val thread = Thread(Runnable {
            Thread.sleep(5000)
            runOnUiThread {
                if(!isDestroyed) {
                    textView.text = "Updated by thread: " + Date().time
                }
            }
        })
        thread.start()

        textView = findViewById(R.id.text)
    }

    val runnables = mutableListOf<Runnable>()

    fun onButtonClicked(view: View) {
        when ((view as Button).text) {
            "Start_handler" -> useHandler()
            "Start_async" -> useAsyncTask()
            "Start_thread" -> useThread()
            "Start_executor" -> useExecutor()
            "Start_async_static" -> useAsyncTaskStatic()

            "Random" -> {
                textView.text = random.nextInt().toString()
            }
            "Stop" -> {
                runnables.forEach {
                    handler.removeCallbacks(it)
                }
                textView.text = "canceled"
            }
            else -> {

            }
        }
    }

    @Suppress("Deprecated")
    private fun useAsyncTask() {
        val myTask = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Unit, String, String>() {
            override fun doInBackground(vararg params: Unit?): String {
                Log.d(TAG, "doInBackground: currentThreadName=${Thread.currentThread().name}")
                Thread.sleep(2000)
                return "Done"
            }

            override fun onPostExecute(result: String?) {
                Log.d(TAG, "onPostExecute: currentThreadName=${Thread.currentThread().name}")
                textView.text = "Done"
            }

        }

        textView.text = "Starting async task"
        myTask.execute()
    }

    private fun useAsyncTaskStatic() {
        task = MyAsyncTask(this)

    }

    override fun onDestroy() {
        task?.activity = null
        super.onDestroy()

    }

    private class MyAsyncTask(var activity: MainActivity?): AsyncTask<Unit, String, String>() {
        override fun doInBackground(vararg params: Unit?): String {
            Log.d(TAG, "doInBackground: currentThreadName=${Thread.currentThread().name}")
            Thread.sleep(2000)
            return "Done"
        }

        override fun onPostExecute(result: String?) {
            Log.d(TAG, "onPostExecute: currentThreadName=${Thread.currentThread().name}")
            activity?.textView?.text = "Done"
        }

    }

    private fun useHandler() {

        Log.d(TAG, "useHandler: currentThreadName=${Thread.currentThread().name}")

        textView.text = "starting"
        for (i in 1..5) {
            val runnable = Runnable {
                Log.d(TAG, "useHandler: Runnable-$i: currentThreadName=${Thread.currentThread().name}")
                textView.text = "$i sec passed"
            }
            runnables.add(runnable)


            handler.postDelayed(runnable, i * 1000L)
        }

        val runnable = Runnable {
            textView.text = "Done"
        }
        runnables.add(runnable)
        handler.postDelayed(runnable, 6000)

    }

    private fun useThread() {
        Log.d(TAG, "useThread(): currentThreadName=${Thread.currentThread().name}")

        thread {
            Log.d(TAG, "useThread: thread: currentThreadName=${Thread.currentThread().name}")
            for(i in 5 downTo 1) {
                Log.d(TAG, "useThread: thread: beforeSleep currentThreadName=${Thread.currentThread().name}")
                Thread.sleep(1000)
                runOnUiThread {
                    Log.d(TAG, "useThread: runOnUi: currentThreadName=${Thread.currentThread().name}")
                    textView.text = "runOn... counter = $i"
                }

                handler.post {
                    textView.text = "post... counter = $i"
                    Log.d(TAG, "useThread: handler.post: currentThreadName=${Thread.currentThread().name}")

                }
            }
        }
    }

    private fun useExecutor() {
        Log.d(TAG, "useExecutor(): currentThreadName=${Thread.currentThread().name}")

        AppExecutors.single.execute {
            Log.d(TAG, "useExecutor: thread: currentThreadName=${Thread.currentThread().name}")
            for(i in 5 downTo 1) {
                Log.d(TAG, "useExecutor: thread: beforeSleep currentThreadName=${Thread.currentThread().name}")
                Thread.sleep(1000)
                runOnUiThread {
                    Log.d(TAG, "useExecutor: runOnUi: currentThreadName=${Thread.currentThread().name}")
                    textView.text = "runOn... counter = $i"
                }
            }
        }
    }



    companion object {
        val TAG = MainActivity::class.simpleName
    }

}