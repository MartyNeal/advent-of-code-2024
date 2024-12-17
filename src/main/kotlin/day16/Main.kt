package day16

import day16.Direction.*
import java.util.PriorityQueue

enum class Direction { N, E, S, W }

data class Node(val neighbors: MutableList<Pair<Node, Direction>>, val id: Int = Node.id++) {
    companion object {
        var id = 0
    }

    override fun toString(): String {
        return "Node(id=$id [${neighbors.map { "${it.second}->${it.first.id}" }}])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }
}

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
                ###############
                #.......#....E#
                #.#.###.#.###.#
                #.....#.#...#.#
                #.###.#####.#.#
                #.#.#.......#.#
                #.#.#####.###.#
                #...........#.#
                ###.#.#####.#.#
                #...#.....#.#.#
                #.#.#.###.#.#.#
                #.....#...#.#.#
                #.###.#.#.#.#.#
                #S..#.....#...#
                ###############
                """

            val exampleInput2 = """
                #################
                #...#...#...#..E#
                #.#.#.#.#.#.#.#.#
                #.#.#.#...#...#.#
                #.#.#.#.###.#.#.#
                #...#.#.#.....#.#
                #.#.#.#.#.#####.#
                #.#...#.#.#.....#
                #.#.#####.#.###.#
                #.#.#.......#...#
                #.#.###.#####.###
                #.#.#...#.....#.#
                #.#.#.#####.###.#
                #.#.#.........#.#
                #.#.#.#########.#
                #S#.............#
                #################
                """

            Main().process(exampleInput).also(::println).takeIf { it == 7036 } ?: error("example failed")
            Main().process(exampleInput2).also(::println).takeIf { it == 11048 } ?: error("example2 failed")
            val input = Utils.getInput(16)
            println(Main().process(input))
        }
    }

    fun parse(input: String): Pair<Node, Node> {
        var start: Pair<Int, Int>? = null
        var end: Pair<Int, Int>? = null
        val nodes = input.trimIndent().lines().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                (x to y).takeIf { c != '#' }.also {
                    when (c) {
                        'S' -> start = it
                        'E' -> end = it
                    }
                }
            }
        }.toSet()
        start!!
        end!!
        val graph = nodes.associateWith { Node(mutableListOf()) }
        graph.forEach { (k, v) ->
            val (x, y) = k
            v.neighbors.addAll(
                listOf(
                    graph[x + 1 to y] to E,
                    graph[x - 1 to y] to W,
                    graph[x to y + 1] to S,
                    graph[x to y - 1] to N
                ).filter { it.first != null }.map { it as Pair<Node, Direction> })
        }
        return Pair(graph[start]!!, graph[end]!!)
    }

    data class Exploration(val node: Node, val distance: Int, val direction: Direction, val path: List<Node> = emptyList())

    fun process(input: String): Int {
        val (start, end) = parse(input)
        val queue = PriorityQueue<Exploration>(compareBy { it.distance })
        for ((neighbor, direction) in start.neighbors) {
            queue.add(Exploration(neighbor, if (direction == N) 1001 else 1, direction, listOf(start)))
        }
        val seen = mutableMapOf(start to (0 to mutableSetOf<Node>()))

        while (!queue.isEmpty()) {
            val (node, distance, currentDirection, path) = queue.poll()
            when {
                (seen[node]?.first ?: Int.MAX_VALUE) > distance -> seen[node] = distance to path.toMutableSet()
                (seen[node]!!.first) < distance && node.neighbors.size == 2 -> continue // .size == 2 because we can't compare reindeer facing different directions
                else -> {
                    seen[node] = distance to ((seen[node]?.second) ?: path.toMutableSet())
                    seen[node]!!.second.addAll(path)
                }
            }

            //if (node == end) break // can't break here because we need to explore all paths for part 2
            for ((neighbor, direction) in node.neighbors) {
                queue.add(Exploration(neighbor, distance + if (currentDirection == direction) 1 else 1001, direction, path + node))
            }
        }
        return seen[end]?.also { println("path length ${it.second.size + 1}") }?.first ?: error("no path found")
    }

}
