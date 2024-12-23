package day22

import Utils

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
                1
                10
                100
                2024
                """
            process(parseInput(exampleInput), 2000).also(::println).takeIf { it == 37327623L } ?: error("example failed")
            val exampleInput2 = """
                1
                2
                3
                2024
                """
            process2(parseInput(exampleInput2), 2000).also(::println).takeIf { it == 23L } ?: error("example2 failed")

            val input = parseInput(Utils.getInput(22))
            println(process(input, 2000))
            println(process2(input, 2000)) // 2058 is too high
        }

        data class Parsed(val lines: List<UInt>)

        fun parseInput(input: String): Parsed {
            val lines = input.trimIndent().lines().map { it.toUInt() }
            return Parsed(lines)
        }

        fun UInt.mixAndPrune(n:UInt) = this.xor(n) % 16777216U

        fun generateMonkeyNumbers(seed: UInt): Sequence<UInt> = generateSequence(seed) {
            var tmp = it
            tmp = tmp.mixAndPrune(tmp shl 6)
            tmp = tmp.mixAndPrune(tmp shr 5)
            tmp = tmp.mixAndPrune(tmp shl 11)
            tmp
        }

        fun process(parsed: Parsed, iterations: Int) =
            parsed.lines.sumOf { line -> generateMonkeyNumbers(line).elementAt(iterations).toLong() }

        fun process2(parsed: Parsed, iterations: Int): Long {
            val windoweds: List<Map<List<Int>, UInt>> = parsed.lines.map { line ->
                generateMonkeyNumbers(line)
                    .take(iterations)
                    .map { it % 10U }
                    .windowed(5, 1)
                    .toList()
                    .reversed() // because associate uses the last-one-wins strategy
                    .associate { listOf(it[1].toInt() - it[0].toInt(), it[2].toInt() - it[1].toInt(), it[3].toInt() - it[2].toInt(), it[4].toInt() - it[3].toInt()) to it[4] }
            }

            return (-9..9).maxOf { a ->
                (-9..9).maxOf { b ->
                    (-9..9).maxOf { c ->
                        (-9..9).maxOf { d ->
                            windoweds.sumOf { windowed ->
                                windowed[listOf(a, b, c, d)]?.toLong() ?: 0L
                            }
                        }
                    }
                }
            }
        }
    }
}