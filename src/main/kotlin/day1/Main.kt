package day1

import Utils

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process(
                listOf(
                    Pair(3, 4),
                    Pair(4, 3),
                    Pair(2, 5),
                    Pair(1, 3),
                    Pair(3, 9),
                    Pair(3, 3)
                )
            ).also(::println)

            val pairs = Utils.getInput(1)
                .split("\n")
                .filter(String::isNotEmpty)
                .map { it.split(Regex(" {3}")).map { it.trim().toInt() } }
                .map { Pair(it[0], it[1]) }
                .toList()
            println(Main().process(pairs))
            println(Main().process2(pairs))
        }
    }

    fun process(pairs: List<Pair<Int, Int>>): Int =
        pairs.map { p -> p.first }.sorted()
            .zip(pairs.map { p -> p.second }.sorted())
            .sumOf { Math.abs(it.first - it.second) }

    fun process2(pairs: List<Pair<Int, Int>>): Int =
        pairs.map { p -> p.second }
            .groupBy { it }.let { rightHistogram ->
                pairs.map { p -> p.first }
                    .sumOf { left ->
                        (rightHistogram[left]?.size ?: 0) * left
                    }
            }
}