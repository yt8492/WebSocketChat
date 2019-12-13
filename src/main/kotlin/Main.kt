import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.ConcurrentHashMap

fun main() {
    val server = ServerSocket(6789)
    val writers = ConcurrentHashMap.newKeySet<BufferedWriter>()
    Runtime.getRuntime().addShutdownHook(Thread() {
        server.close()
    })
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    while (true) {
        val client = server.accept()
        val clientWriter = client.getOutputStream().bufferedWriter()
        writers.add(clientWriter)
        coroutineScope.launch {
            val reader = withContext(Dispatchers.IO) {
                client.getInputStream() .bufferedReader()
            }
            reader.lineSequence()
                .forEach {
                    println(it)
                    writers.forEach { writer ->
                        try {
                            writer.write("$it\n")
                            writer.flush()
                        } catch (e: SocketException) {
                            writers.remove(writer)
                        }
                    }
                }
        }
    }
}