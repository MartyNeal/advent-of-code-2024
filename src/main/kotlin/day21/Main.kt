package day21

import kotlin.math.abs

val Pair<Int, Int>.x get() = first
val Pair<Int, Int>.y get() = second

interface Keypad {
    val char: Char
    val pos: Pair<Int, Int>
    val x get() = pos.x
    val y get() = pos.y

    companion object {
        fun pathsImpl(from: Keypad, to: Keypad): List<List<DirectionalKeypad>> = listOfNotNull(
            // horizontal then vertical
            List(abs(to.y - from.y)) { if (to.y > from.y) DirectionalKeypad.DOWN else DirectionalKeypad.UP } +
                    List(abs(to.x - from.x)) { if (to.x > from.x) DirectionalKeypad.RIGHT else DirectionalKeypad.LEFT },
            // vertical then horizontal
            List(abs(to.x - from.x)) { if (to.x > from.x) DirectionalKeypad.RIGHT else DirectionalKeypad.LEFT } +
                    List(abs(to.y - from.y)) { if (to.y > from.y) DirectionalKeypad.DOWN else DirectionalKeypad.UP }
        ).distinct()

        fun pathsAvoiding(from: Keypad, to: Keypad, avoid: Pair<Int, Int>): List<List<DirectionalKeypad>> =
            pathsImpl(from, to).filter {
                !(from.y == avoid.y && to.x == avoid.x && it[0] in listOf(DirectionalKeypad.RIGHT, DirectionalKeypad.LEFT)) &&
                        !(from.x == avoid.x && to.y == avoid.y && it[0] in listOf(DirectionalKeypad.DOWN, DirectionalKeypad.UP))
            }
    }

    fun pathsTo(to: Keypad): List<List<Keypad>>
}

enum class DigitalKeypad(override val char: Char, override val pos: Pair<Int, Int>) : Keypad {
    SEVEN('7', 0 to 0), EIGHT('8', 1 to 0), NINE('9', 2 to 0),
    FOUR('4', 0 to 1), FIVE('5', 1 to 1), SIX('6', 2 to 1),
    ONE('1', 0 to 2), TWO('2', 1 to 2), THREE('3', 2 to 2),
                              ZERO('0', 1 to 3), A('A', 2 to 3);

    companion object {
        fun fromChar(char: Char): DigitalKeypad {
            return entries.first { it.char == char }
        }

        fun paths(from: Keypad, to: Keypad): List<List<DirectionalKeypad>> {
            return Keypad.pathsAvoiding(from, to, 0 to 3)
        }
    }

    override fun pathsTo(to: Keypad): List<List<Keypad>> = paths(this, to)
}

fun List<DigitalKeypad>.toNumber(): Int = filter { it != DigitalKeypad.A }.joinToString("") { it.char.toString() }.toInt()

enum class DirectionalKeypad(override val char: Char, override val pos: Pair<Int, Int>) : Keypad {
                                   UP('^', 1 to 0),     A('A', 2 to 0),
    LEFT('<', 0 to 1), DOWN('v', 1 to 1), RIGHT('>', 2 to 1);

    companion object {
        fun paths(from: Keypad, to: Keypad): List<List<DirectionalKeypad>> {
            return Keypad.pathsAvoiding(from, to, 0 to 0)
        }
    }

    override fun pathsTo(to: Keypad): List<List<Keypad>> = paths(this, to)
}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val exampleInput = """
                029A
                980A
                179A
                456A
                379A
                """
            println(process(parseInput(exampleInput), 2).takeIf { it == 126384L } ?: "Test failed")

            val input = parseInput(Utils.getInput(21))
            println(process(input, 2))
            println(process(input, 25))
        }

        fun parseInput(input: String): List<List<DigitalKeypad>> =
            input.trimIndent().lines().map { line -> line.map { DigitalKeypad.fromChar(it) } }

        val cache = mutableMapOf<Triple<List<Keypad>, Int, Array<Keypad>>, Long>()

        fun process(parsed: List<List<DigitalKeypad>>, directionalRobotCount: Int): Long {
            return parsed.sumOf { it.toNumber() * processLine(it, directionalRobotCount) }
        }

        fun processLine(digits: List<DigitalKeypad>, directionalRobotCount: Int): Long {
            val robots: Array<Keypad> = arrayOf(DigitalKeypad.A, *Array(directionalRobotCount) { DirectionalKeypad.A })
            fun helper(path: List<Keypad>, i: Int): Long =
                cache.getOrPut(Triple(path, i, robots)) {
                    path.sumOf { path0 ->
                        robots[i].pathsTo(path0)
                            .map { it + DirectionalKeypad.A }
                            .minOf { p -> if (i == robots.size - 1) p.size.toLong() else helper(p, i + 1) }
                            .also { robots[i] = path0 }
                    }
                }

            return helper(digits, 0)
        }


    }
}
