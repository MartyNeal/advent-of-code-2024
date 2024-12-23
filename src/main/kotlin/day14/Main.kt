package day14

import java.lang.Math.floorMod

class Main(val ROWS: Int = 103, val COLS: Int = 101) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main(7, 11).process(
                """
                      p=0,4 v=3,-3
                      p=6,3 v=-1,-3
                      p=10,3 v=-1,2
                      p=2,0 v=2,-1
                      p=0,0 v=1,3
                      p=3,0 v=-2,-2
                      p=7,6 v=-1,-3
                      p=3,0 v=-1,-2
                      p=9,3 v=2,3
                      p=7,3 v=-1,2
                      p=2,4 v=2,-3
                      p=9,5 v=-3,-3""", 100
            ).also(::println).takeIf { it == 12 } ?: error("example failed")
            val input = Utils.getInput(14)
            println(Main().process(input, 100)) //225552000
            println(Main().process2(input)) // 7371
        }
    }

    fun parseInput(input: String): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> =
        input.trimIndent().lines().map { line ->
            val (p, v) = line.split(" v=", limit = 2)
            val (px, py) = p.split("p=", limit = 2)[1].split(",", limit = 2).map { it.toInt() }
            val (vx, vy) = v.split(",", limit = 2).map { it.toInt() }
            Pair(px to py, vx to vy)
        }

    fun process(input: String, seconds: Int): Int =
        parseInput(input)
            .map { (p, v) ->
                Pair(
                    floorMod((p.first + v.first * seconds), COLS),
                    floorMod((p.second + v.second * seconds), ROWS)
                )
            }
            .also(::printGrid)
            .let(::safetyFactor)

    fun printGrid(positions: List<Pair<Int, Int>>) {
        val grid = Array(ROWS) { CharArray(COLS) { '.' } }
        positions.forEach { (x, y) -> grid[y][x] = '#' }
        println(grid.joinToString("\n") { it.joinToString("") })
    }


    fun safetyFactor(positions: List<Pair<Int, Int>>): Int = positions
        .fold(listOf(0, 0, 0, 0)) { a, e ->
            when {
                e.first < (COLS - 1) / 2 && e.second < (ROWS - 1) / 2 -> listOf(a[0] + 1, a[1], a[2], a[3])
                e.first < (COLS - 1) / 2 && e.second > (ROWS - 1) / 2 -> listOf(a[0], a[1] + 1, a[2], a[3])
                e.first > (COLS - 1) / 2 && e.second < (ROWS - 1) / 2 -> listOf(a[0], a[1], a[2] + 1, a[3])
                e.first > (COLS - 1) / 2 && e.second > (ROWS - 1) / 2 -> listOf(a[0], a[1], a[2], a[3] + 1)
                else -> a
            }
        }.fold(1) { acc, i -> acc * i }


    fun process2(input: String): Int {
        val parsed = parseInput(input)
        val velocities = parsed.map { it.second }
        return generateSequence(parsed.map { it.first }) { positions ->
            positions.zip(velocities) { p, v ->
                floorMod(p.first + v.first, COLS) to floorMod(
                    p.second + v.second,
                    ROWS
                )
            }
        }
            .mapIndexed { i, it -> i to safetyFactor(it) }
            .take(ROWS * COLS)
            .minBy { it.second }
            .also { process(input, it.first) }
            .first
    }
}
