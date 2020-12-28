package com.elevenetc.treeview

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun count_isCorrect() {
        val data = mutableListOf<Node<String>>()
        data.add(Node("0").apply {
            opened = true
        })
        data.add(Node("1"))
        data.add(Node("2").apply {
            children.add(Node("2.0"))
            children.add(Node("2.1"))
            children.add(Node("2.2"))
            opened = true
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
        assert(countOpened(data) == 12)
        printTree(data)
    }

    @Test
    fun indexOf_isCorrect() {

        val data = mutableListOf<Node<String>>()

        data.add(Node("0").apply {
            children.add(Node("0.0"))
            opened = true
        })
        data.add(Node("1").apply {
            children.add(Node("1.0"))
            opened = true
        })
        data.add(Node("2").apply {
            children.add(Node("2.0"))
            children.add(Node("2.1"))
        })

        printTree(data)

        val data0 = getByIndex(0, data)
        val data1 = getByIndex(1, data)
        val data2 = getByIndex(2, data)
        val data3 = getByIndex(3, data)
        val data4 = getByIndex(4, data)
        val data5 = getByIndex(5, data)
        val data6 = getByIndex(6, data)

        assert(data0!!.data == "0")
        assert(data1!!.data == "0.0")
        assert(data2!!.data == "1")
        assert(data3!!.data == "1.0")
        assert(data4!!.data == "2")
        assert(data5 == null)
        assert(data6 == null)

        println()
    }
}