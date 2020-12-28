package com.elevenetc.treeview

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var treeView: TreeView
    lateinit var adapter: TreeViewAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = mutableListOf<Node<String>>()
        data.add(Node("0").apply {
            opened = true
        })
        data.add(Node("1"))
        data.add(Node("2").apply {
            children.add(Node("2.0"))
            children.add(Node("2.1"))
            children.add(Node("2.2"))
            //opened = true
        })
        data.add(Node("3").apply {
            children.add(Node("3.0"))
            children.add(Node("3.1").apply {
                children.add(Node("3.1.0"))
                children.add(Node("3.1.1"))
                opened = true
            })
            children.add(Node("3.2"))
            opened = true
        })

        treeView = findViewById(R.id.tree_view)

        findViewById<View>(R.id.text_0).setOnClickListener {
            retrieveCmd()
        }

        adapter = TreeViewAdapter(
            data,
            object : TreeItemViewFactory<String> {
                override fun create(context: Context): View {
                    val textView = TextView(context)
                    textView.setSingleLine()
                    textView.maxLines = 1
                    return textView
                }

                override fun bind(data: String, depth: Int, view: View) {
                    (view as TextView).text = getShift(depth, " > ") + data
                }
            })

        treeView.adapter = adapter

        Thread({
            Thread({
                while (true) {
                    Thread.sleep(1)
                }
            }, "app-sample-inside").start()
            while (true) {
                Thread.sleep(1)
            }
        }, "app-sample").start()

        //retrieveCmd()

        //xxx()
        //xxxZ()
    }

    private fun retrieveCmd() {
        val thread = Thread() {
            try {


                val pid = android.os.Process.myPid()
                val treads: Set<Thread> = Thread.getAllStackTraces().keys


                val nodes = mutableListOf<Node<String>>()

                treads.forEach { thread ->

                    val n = Node("THREAD: " + thread.name)
                    n.opened = true
                    nodes.add(n)

                    thread.stackTrace.forEach { st ->
                        if (!st.isNativeMethod) {
                            val name = st.toString()
                            n.children.add(Node("STACK: $name"))
                        } else {
                            println("skipped meth")
                        }

                    }
                }

                treeView.post {
                    adapter.setData(nodes)
                }

                val result = runCmd("top -b -n 1 -H")
                //val result = runCmd("ps -T -w -M")
                //val result = runCmd("ps -A -T -w -f -l")

                result.success.forEach {
                    println(it)
                }

                println(result.success)
                println(result.error)
                println(result.exitCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.name = "app-running-cmd"
        thread.start()
    }

    private fun xxx() {
        val activityManager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val pidsTask = activityManager.runningAppProcesses

        for (i in pidsTask.indices) {
            val pInfo = pidsTask[i]
            println("pid:" + pInfo.pid)
            //nameList.add(pidsTask[i].processName)
            //idList.add(pidsTask[i].uid)
        }
    }

    private fun xxxZ() {
        val time = System.currentTimeMillis()
        val systemService = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val appStatsList = systemService.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 1000, time
        )
        if (appStatsList != null && !appStatsList.isEmpty()) {
            val currentApp = Collections.max(appStatsList) { o1, o2 ->
                java.lang.Long.compare(
                    o1.getLastTimeUsed(),
                    o2.getLastTimeUsed()
                )
            }.getPackageName()
        }
    }

    private fun callCmd(cmd: String): String {

        val output = StringBuffer()
        val p: Process = Runtime.getRuntime().exec(cmd)
        val exitValue = p.exitValue()
        if (exitValue > 0) {
            return readStream(p.errorStream)
        }

        val reader = BufferedReader(InputStreamReader(p.inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            output.append(line.trimIndent())
            p.waitFor()
            line = reader.readLine()
        }
        return output.toString()
    }

    private fun readStream(stream: InputStream): String {
        val output = StringBuffer()
        val reader = BufferedReader(InputStreamReader(stream))
        var line: String? = reader.readLine()
        while (line != null) {
            output.append(line.trimIndent())
            //p.waitFor()
            line = reader.readLine()
        }
        return output.toString()
    }
}

fun toNodes(view: View): List<Node<View>> {
    val node = Node<View>(view)
    if (view is ViewGroup) {

        val g = view as ViewGroup

        g.children.forEach {

        }


    } else {
        val result = mutableListOf<View>()
        //result.add(node)
    }
    return emptyList()
}


class TreeViewAdapter<T>(data: List<Node<T>>, val viewFactory: TreeItemViewFactory<T>) :


    RecyclerView.Adapter<TreeViewAdapter.VH>() {

    val data: MutableList<Node<T>> = data.toMutableList()

    fun setData(data: List<Node<T>>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(viewFactory.create(parent.context))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val result = getByIndex(position, data)
        if (result != null) {
            val depth = getDepth(data, result)
            viewFactory.bind(result.data, depth, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return countOpened(data)
    }


}

class TreeView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        layoutManager = LinearLayoutManager(context)
    }

}

