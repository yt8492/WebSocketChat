import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.Socket

fun main() = runBlocking {
    withContext(Dispatchers.IO) {
        Socket("localhost", 6789).use { socket ->
            val reader = socket.getInputStream().bufferedReader()
            val writer = socket.getOutputStream().bufferedWriter()
            launch {
                reader.lineSequence().forEach {
                    println(it)
                }
            }
            while (true) {
                val input = readLine()!!
                writer.write("$input\n")
                writer.flush()
            }
        }
    }
}