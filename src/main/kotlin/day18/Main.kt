package day18

import Utils
import java.util.*

data class Main(val ROWS: Int, val COLS: Int) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val examplePairs = listOf(
                Pair(5, 4),
                Pair(4, 2),
                Pair(4, 5),
                Pair(3, 0),
                Pair(2, 1),
                Pair(6, 3),
                Pair(2, 4),
                Pair(1, 5),
                Pair(0, 6),
                Pair(3, 3),
                Pair(2, 6),
                Pair(5, 1),
                Pair(1, 2),
                Pair(5, 5),
                Pair(2, 5),
                Pair(6, 5),
                Pair(1, 4),
                Pair(0, 4),
                Pair(6, 4),
                Pair(1, 1),
                Pair(6, 1),
                Pair(1, 0),
                Pair(0, 5),
                Pair(1, 6),
                Pair(2, 0)
            )
            Main(7, 7).process(examplePairs.take(12)).takeIf { it == 22 } ?: error("process1 example failed")
            Main(7, 7).process2(examplePairs).takeIf { it == Pair(6, 1) } ?: error("process2 example failed")

            val pairs = Utils.getInput(18)
                .split("\n")
                .filter(String::isNotEmpty)
                .map { it.split(',').map { it.trim().toInt() } }
                .map { Pair(it[0], it[1]) }
                .toList()
            println(Main(71, 71).process(pairs.take(1024)))
            println(Main(71, 71).process2(pairs).let { "${it.first},${it.second}" })
        }
    }

    fun process(pairsList: List<Pair<Int, Int>>): Int? {
        val pairs = pairsList.toSet()
        fun neighbors(p: Pair<Int, Int>): List<Pair<Int, Int>> = listOf(
            Pair(p.first - 1, p.second),
            Pair(p.first + 1, p.second),
            Pair(p.first, p.second - 1),
            Pair(p.first, p.second + 1)
        ).filter { !pairs.contains(it) && it.first in 0 until COLS && it.second in 0 until ROWS }

        data class Exploration(val node: Pair<Int, Int>, val distanceTraveled: Int) {
            fun neighbors(): List<Exploration> = neighbors(node).map { Exploration(it, distanceTraveled + 1) }
        }

        val start = Pair(0, 0)
        val end = Pair(COLS - 1, ROWS - 1)
        val queue = PriorityQueue<Exploration>(compareBy { it.distanceTraveled })
        queue.add(Exploration(start, 0))
        val visited = mutableSetOf<Pair<Int, Int>>()
        while (queue.isNotEmpty()) {
            val current = queue.poll()
            if (current.node in visited) continue
            if (current.node == end) return current.distanceTraveled
            visited.add(current.node)
            current.neighbors().filter { it.node !in visited }.forEach { queue.add(it) }
        }
        return null
    }


    fun process2(pairsList: List<Pair<Int, Int>>): Pair<Int, Int> {
        var min = 0
        var max = pairsList.size
        var mid = max / 2
        while (min < max) {
            val result = process(pairsList.take(mid))
            if (result == null) {
                max = mid
            } else {
                min = mid + 1
            }
            mid = (min + max) / 2
        }
        return pairsList[min - 1]
    }
}