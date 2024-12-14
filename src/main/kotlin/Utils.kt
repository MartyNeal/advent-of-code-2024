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

        val url = URL("https://adventofcode.com/2024/day/${day}/input")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("accept", "text/plain")
        connection.setRequestProperty(
            "cookie",
            "session=REPLACE_ME"
        )
        val text = connection.inputStream.bufferedReader().readText()
        file.writeText(text)
        return text
    }
}