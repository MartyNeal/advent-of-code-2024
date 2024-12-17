import java.io.File
import java.net.HttpURLConnection
import java.net.URL


object Utils {
    fun getInput(day: Int): String {
        val tempDir = System.getProperty("java.io.tmpdir")
        val file = File(tempDir, "input-day${day}.txt")
        if (file.exists()) {
            return file.readText()
        }

        @Suppress("DEPRECATION")
        val url = URL("https://adventofcode.com/2024/day/${day}/input")
        val connection = url.openConnection() as HttpURLConnection
        val sessionID = System.getenv("AOC_SESSION_ID") ?: error("AOC_SESSION_ID not set")
        connection.setRequestProperty("accept", "text/plain")
        connection.setRequestProperty("cookie", "session=${sessionID}")
        val text = connection.inputStream.bufferedReader().readText().trimIndent()
        file.writeText(text)
        return text
    }
}

fun <T> Sequence<T>.takeWhileInclusive(pred: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}

fun <T> Sequence<T>.takeUntilInclusive(pred: (T) -> Boolean): Sequence<T> {
    return takeWhileInclusive { !pred(it) }
}