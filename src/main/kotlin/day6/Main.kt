package day6

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2(
                """
                    ....#.....
                    .........#
                    ..........
                    ..#.......
                    .......#..
                    ..........
                    .#..^.....
                    ........#.
                    #.........
                    ......#..."""
            ).also(::println)
            val input = Utils.getInput(6)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    fun process(input: String): Int {
        val (rows, obstacles, start) = parse(input)
        return pathSpots(rows, obstacles, start)!!.size - 1 // -1 because we don't count the ending position outside the grid
    }

    private fun parse(input: String): Triple<List<List<Char>>, Set<Pair<Int, Int>>, Pair<Int, Int>> {
        val rows = input
            .trimIndent()
            .lines()
            .map { it.split("").filter(String::isNotEmpty).map { it[0] } }
        val obstacles = rows.indices.flatMap { r ->
            rows[r].indices.mapNotNull { c ->
                if (rows[r][c] == '#') Pair(r, c) else null
            }
        }.toSet()
        val start = rows.indices.flatMap { r ->
            rows[r].indices.mapNotNull { c ->
                if (rows[r][c] == '^') Pair(r, c) else null
            }
        }.first()
        return Triple(rows, obstacles, start)
    }

    // returns null if the path is a loop
    fun pathSpots(rows: List<List<Char>>, obstacles: Set<Pair<Int, Int>>, start: Pair<Int, Int>): MutableSet<Pair<Int, Int>>? {
        var currentPosition = start
        var currentDirection = Pair(-1, 0)
        val seen = mutableMapOf(currentPosition to currentDirection)
        while (currentPosition.first in rows.indices && currentPosition.second in rows[currentPosition.first].indices) {
            val nextPosition = Pair(currentPosition.first + currentDirection.first, currentPosition.second + currentDirection.second)
            if (nextPosition in obstacles) {
                currentDirection = when (currentDirection) {
                    Pair(-1, 0) -> Pair(0, 1)
                    Pair(0, 1) -> Pair(1, 0)
                    Pair(1, 0) -> Pair(0, -1)
                    Pair(0, -1) -> Pair(-1, 0)
                    else -> throw IllegalStateException()
                }
            } else {
                currentPosition = nextPosition
                if (seen.put(currentPosition, currentDirection) == currentDirection) {
                    return null
                }
            }
        }
        return seen.keys
    }

    fun process2(input: String): Int {
        val (rows, obstacles, start) = parse(input)
        val path = pathSpots(rows, obstacles, start)!!
        path.remove(start)

        return path.filter { point ->
            obstacles.toMutableSet().apply { add(point) }.let {
                (pathSpots(rows, it, start) == null)
            }
        }.toMutableSet().apply { remove(start); removeIf { it.first < 0 || it.second < 0} }.size
    }
}