package day2

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2(
                listOf(
                    listOf(7, 6, 4, 2, 1),
                    listOf(1, 2, 7, 8, 9),
                    listOf(9, 7, 6, 2, 1),
                    listOf(1, 3, 2, 4, 5),
                    listOf(8, 6, 4, 4, 1),
                    listOf(1, 3, 6, 7, 9),
                )
            ).also(::println)

            val levels = Utils.getInput(2)
                .split("\n")
                .filter(String::isNotEmpty)
                .map { it.split(" ").map { it.trim().toInt() } }
                .toList()
            println(Main().process(levels))
            println(Main().process2(levels))
        }
    }

    fun process(levels: List<List<Int>>) = levels.count(::isSafe)

    fun isSafe(level: List<Int>): Boolean {
        val pairs = level.zipWithNext { a, b -> a - b }
        return (pairs.all { it > 0 } || pairs.all { it < 0 }) &&
                pairs.all { -3 <= it && it != 0 && it <= 3 }
    }

    fun process2(levels: List<List<Int>>) =
        levels.count { level ->
            isSafe(level) ||
            level.indices.any { i -> isSafe(level.filterIndexed { j, _ -> i != j }) } }
}