package day20

import kotlin.math.abs

data class Node(val coords: Pair<Int, Int>, val distanceToEnd: Int)

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
                ###############
                #...#...#.....#
                #.#.#.#.#.###.#
                #S#...#.#.#...#
                #######.#.#.###
                #######.#.#...#
                #######.#.###.#
                ###..E#...#...#
                ###.#######.###
                #...###...#...#
                #.#####.#.###.#
                #.#...#.#.#...#
                #.#.#.#.#.#.###
                #...#...#...###
                ###############
                """

            process(parse(exampleInput), 10, 2).also(::println).takeIf { it == 10 } ?: error("example failed")
            process(parse(exampleInput), 50, 20).also(::println).takeIf { it == 285 } ?: error("example failed")
            val input = Utils.getInput(20)
            println(process(parse(input), 100, 2))
            println(process(parse(input), 100, 20))
        }

        fun parse(input: String): ArrayDeque<Node> {
            var start: Pair<Int, Int>? = null
            var end: Pair<Int, Int>? = null
            val pairs = input.trimIndent().lines().flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, c ->
                    (x to y).takeIf { c != '#' }.also {
                        when (c) {
                            'S' -> start = it
                            'E' -> end = it
                        }
                    }
                }
            }.toMutableSet()
            start!!
            end!!

            val list = ArrayDeque<Node>()
            var cur = end
            pairs.remove(end)
            while (cur != null) {
                list.addFirst(Node(cur, list.size))
                val (x, y) = cur
                cur = listOf(
                    x + 1 to y,
                    x - 1 to y,
                    x to y + 1,
                    x to y - 1
                ).firstOrNull { pairs.remove(it) }
            }

            return list
        }

        fun manhattanDistance(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
            return abs(a.first - b.first) + abs(a.second - b.second)
        }

        fun process(list: ArrayDeque<Node>, threshold: Int, cheatTime: Int): Int {
            return list.sumOf { node ->
                list.count { otherNode ->
                    val md = manhattanDistance(node.coords, otherNode.coords)
                    md <= cheatTime && otherNode.distanceToEnd <= node.distanceToEnd - threshold - md
                }
            }
        }
    }
}
