package day15

import takeUntilInclusive

enum class Tile(val c: Char) {
    WALL('#'),
    EMPTY('.'),
    CURRENT_POS('@'),
    BOX('O'),
    LEFT_BOX('['),
    RIGHT_BOX(']')
}

typealias Warehouse = MutableMap<Pair<Int, Int>, Tile>
typealias Direction = Pair<Int, Int>

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val smallInput =
                """
                ########
                #..O.O.#
                ##@.O..#
                #...O..#
                #.#.O..#
                #...O..#
                #......#
                ########

                <^^>>>vv<v>>v<<
                """

            val exampleInput = """
                ##########
                #..O..O.O#
                #......O.#
                #.OO..O.O#
                #..O@..O.#
                #O#..O...#
                #O..O..O.#
                #.OO.O.OO#
                #....O...#
                ##########

                <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
                vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
                ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
                <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
                ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
                ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
                >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
                <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
                ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
                v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
                """

            Main().process(smallInput).also(::println).takeIf { it == 2028 } ?: error("small example failed")
            Main().process(exampleInput).also(::println).takeIf { it == 10092 } ?: error("example failed")
            val input = Utils.getInput(15)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    fun gpsPosition(pair: Pair<Int, Int>): Int = pair.first + pair.second * 100

    fun parse(input: String): Pair<Warehouse, List<Direction>> =
        input
            .trimIndent()
            .split("\n\n")
            .let { (a, b) -> Pair(parseWarehouse(a), parseDirections(b)) }

    fun parse2(input: String): Pair<Warehouse, List<Direction>> =
        input
            .trimIndent()
            .split("\n\n")
            .let { (a, b) -> Pair(parseWarehouse2(a), parseDirections(b)) }

    private fun parseDirections(directionsStr: String) = directionsStr.trim().mapNotNull {
        when (it) {
            'v' -> Pair(0, 1)
            '^' -> Pair(0, -1)
            '<' -> Pair(-1, 0)
            '>' -> Pair(1, 0)
            else -> null
        }
    }

    fun parseWarehouse(input: String): Warehouse =
        input.lines().mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                Pair(x, y) to when (c) {
                    Tile.WALL.c -> Tile.WALL
                    Tile.EMPTY.c -> Tile.EMPTY
                    Tile.CURRENT_POS.c -> Tile.CURRENT_POS
                    Tile.BOX.c -> Tile.BOX
                    else -> error("Unknown tile $c")
                }
            }
        }.flatten().toMap().toMutableMap()

    fun parseWarehouse2(input: String): Warehouse =
        input.lines().mapIndexed { y, line ->
            line.flatMapIndexed { x, c ->
                when (c) {
                    Tile.WALL.c -> listOf(Tile.WALL, Tile.WALL)
                    Tile.EMPTY.c -> listOf(Tile.EMPTY, Tile.EMPTY)
                    Tile.CURRENT_POS.c -> listOf(Tile.CURRENT_POS, Tile.EMPTY)
                    Tile.BOX.c -> listOf(Tile.LEFT_BOX, Tile.RIGHT_BOX)
                    else -> error("Unknown tile $c")
                }.let { it.mapIndexed { i, tile -> Pair(x * 2 + i, y) to tile } }
            }
        }
            .flatten().toMap().toMutableMap()

    fun process(input: String): Int {
        val (warehouse, directions) = parse(input)
        var currentPos = warehouse.entries.first { it.value == Tile.CURRENT_POS }.key
        for (direction in directions) {
            val (dx, dy) = direction
            val path = generateSequence(currentPos) { Pair(it.first + dx, it.second + dy) }
                .takeUntilInclusive { warehouse[it] == Tile.WALL || warehouse[it] == Tile.EMPTY }
                .toList()
            if (warehouse[path.last()] != Tile.EMPTY) continue
            path.reversed().zipWithNext().forEach { (to, from) ->
                warehouse[to] = warehouse[from]!!
            }
            currentPos = path[1]
            warehouse[path[0]] = Tile.EMPTY
        }
        return warehouse.filterValues { it == Tile.BOX }.keys.sumOf(::gpsPosition)
    }

    fun process2(input: String): Int {
        val (warehouse, directions) = parse2(input)
        var currentPos = warehouse.entries.first { it.value == Tile.CURRENT_POS }.key
        for (direction in directions) {
            val (dx, dy) = direction
            if (dy == 0) {
                val path = generateSequence(currentPos) { Pair(it.first + dx, it.second) }
                    .takeUntilInclusive { warehouse[it] == Tile.WALL || warehouse[it] == Tile.EMPTY }
                    .toList()
                if (warehouse[path.last()] != Tile.EMPTY) continue
                path.reversed().zipWithNext().forEach { (to, from) ->
                    warehouse[to] = warehouse[from]!!
                }
            } else {
                val path = generateSequence(listOf(currentPos)) { positions ->
                    positions.flatMap { pos ->
                        buildList {
                            val newPos = Pair(pos.first, pos.second + dy)
                            when (warehouse[newPos]) {
                                Tile.LEFT_BOX -> { add(Pair(newPos.first + 1, newPos.second)); add(newPos) }
                                Tile.RIGHT_BOX -> { add(Pair(newPos.first - 1, newPos.second)); add(newPos) }
                                Tile.WALL -> { add(newPos) }
                                else -> {}
                            }
                        }
                    }
                }
                    .takeUntilInclusive { positions -> positions.any { warehouse[it] == Tile.WALL } || positions.all { warehouse[it] == Tile.EMPTY } }
                    .toList()
                if (path.last().any { warehouse[it] == Tile.WALL }) continue
                path.reversed().drop(1).forEach { positions ->
                    for (position in positions) {
                        warehouse[Pair(position.first, position.second + dy)] = warehouse[position]!!
                    }
                    for (position in positions) {
                        warehouse[position] = Tile.EMPTY
                    }
                }
            }
            warehouse[currentPos] = Tile.EMPTY
            currentPos = currentPos.let { it.first + dx to it.second + dy }
        }
        return warehouse
            .filterValues { it == Tile.LEFT_BOX }.keys.sumOf(::gpsPosition)
    }
}
