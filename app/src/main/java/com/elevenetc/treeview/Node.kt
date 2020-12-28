package com.elevenetc.treeview

import android.content.Context
import android.view.View

class Node<T>(
    val data: T,
    val paren: Node<T>? = null,
    val children: MutableList<Node<T>> = mutableListOf()
) {
    var opened = false


    override fun toString(): String {
        return "Node(data=$data)"
    }


}

fun <T> contains(d: List<Node<T>>, value: T): Boolean {
    d.forEach {
        if (it.data == value) {
            return true
        }
    }
    return false
}

interface TreeItemViewFactory<T> {
    fun create(context: Context): View
    fun bind(data: T, depth: Int, view: View)
}

fun <T> getByIndex(idx: Int, d: List<Node<T>>): Node<T>? {
    return getByOpenedIndexInternal(idx, 0, d).node
}

private fun <T> getByOpenedIndexInternal(
    searchIdx: Int,
    currentIdx: Int,
    d: List<Node<T>>
): IdxNode<T> {
    var cI = currentIdx

    d.forEach { n ->
        if (searchIdx == cI) {
            return IdxNode(searchIdx, n)
        } else {
            cI++
            if (n.opened) {
                val result = getByOpenedIndexInternal(searchIdx, cI, n.children)

                if (result.node != null) {
                    return result
                } else {
                    cI = result.idx
                }

            }
        }
    }
    return IdxNode(cI, null)
}

private class IdxNode<T>(val idx: Int, val node: Node<T>? = null)

fun getDepth(nodes: List<Node<*>>, find: Node<*>, depth: Int = 0): Int {
    nodes.forEach {
        if (find == it) {
            return depth
        } else {
            val result = getDepth(it.children, find, depth + 1)
            if (result > -1) return result
        }
    }
    return -1
}

fun countOpened(d: List<Node<*>>): Int {
    var counter = 0

    d.forEach {
        counter++
        if (it.opened) {
            counter += countOpened(it.children)
        }
    }

    return counter
}

fun printTree(d: List<Node<*>>) {
    printTree(d, 0, 0)
}

private fun printTree(d: List<Node<*>>, depth: Int, line: Int): Int {
    val shift = getShift(depth)
    var counted = line
    d.forEach { node ->
        println("$counted: " + shift + node.data)
        counted++
        if (node.opened) {
            counted = printTree(node.children, depth + 1, counted)
        }
    }
    return counted
}

fun getShift(depth: Int, value: String = " "): String {

    if (depth <= 0) return ""
    if (depth == 1) return value

    val sb = StringBuilder()
    for (i in 1..depth) {
        sb.append(value)
    }
    return sb.toString()
}