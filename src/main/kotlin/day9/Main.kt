package day9

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2("2333133121414131402").also(::println)
            val input = Utils.getInput(9)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    fun process(input: String): Long {
        val digits = input.map { it.digitToInt() }
        val l = digits.flatMapIndexed { i, d -> if (i % 2 == 0) List(d) { i/2 } else List(d) { null } }.toMutableList()
        var i = 0
        var j = l.size - 1
        while (i < j) {
            if (l[i] == null) {
                while (l[j] == null) j--
                l[i] = l[j]
                l[j] = null
                j--
            }
            i++
        }
        return l.foldIndexed(0L) { i, acc, d -> acc + i * (d ?: 0) }
    }

    fun process2(input: String): Long {
        // Good luck reading this code in a few months
        val digits = input.map { it.digitToInt() }
        val l = digits.flatMapIndexed { i, d -> if (i % 2 == 0) List(d) { i/2 } else List(d) { null } }.toMutableList()
        var j = l.size - 1
        while (0 < j) {
            while (l[j] == null) j--
            var i = j
            if (l[j] == 0) break
            while (l[i] == l[j]) i--
            val s = j - i

            i = 0
            while (i < j) {
                while (l[i] != null) i++
                if (i >= j) {
                    var i = j
                    while (l[i] == l[j]) i--
                    j = i
                    break
                }
                var k = i
                while (l[k] == null) k++
                val e = k - i
                if (e >= s) {
                    repeat(s) {
                        l[i] = l[j]
                        l[j] = null
                        i++
                        j--
                    }
                    break
                }
                i = k
            }
        }
        return l.foldIndexed(0L) { i, acc, d -> acc + i * (d ?: 0) }
    }
}
