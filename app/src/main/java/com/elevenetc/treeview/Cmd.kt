package com.elevenetc.treeview

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream


fun runCmd(command: String): CmdResult {
    val pb = ProcessBuilder(command.split(" "))
    val process = pb.start()

    val outResult = mutableListOf<String>()
    val outError = mutableListOf<String>()

    val out = ReadToList(process.inputStream, outResult)
    val err = ReadToList(process.errorStream, outError)
    out.start()
    err.start()

    process.waitFor()
    out.join()
    err.join()

    return CmdResult(
        outResult,
        outError,
        process.exitValue()
    )
}

data class CmdResult(val success: List<String>, val error: List<String>, val exitCode: Int)

class ReadToList(val input: InputStream, val output: MutableList<String>) : Thread() {
    override fun run() {
        val reader = BufferedReader(InputStreamReader(input))

        var line: String? = reader.readLine()
        while (line != null) {
            output.add(line)
            line = reader.readLine()
        }
    }
}

class PipeStream(val input: InputStream, val output: OutputStream) : Thread() {
    override fun run() {
        val buffer = ByteArray(1024)
        var len: Int
        while (input.read(buffer).also { len = it } >= 0) {
            output.write(buffer, 0, len)
        }
    }
}