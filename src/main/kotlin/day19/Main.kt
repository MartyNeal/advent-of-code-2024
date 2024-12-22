package day19

import Utils

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
                r, wr, b, g, bwu, rb, gb, br
                
                brwrr
                bggr
                gbbr
                rrbgbr
                ubwu
                bwurrg
                brgr
                bbrgwb
                """

            process(parseInput(exampleInput)).also(::println).takeIf { it == 6 } ?: error("example1 failed")
            process2(parseInput(exampleInput)).also(::println).takeIf { it == 16L } ?: error("example2 failed")
            // clear cache between tests... forgetting to do this cost me hours for an otherwise easy problem
            cache.clear()
            cache[listOf()] = 1
            val input = parseInput(Utils.getInput(19))
            println(process(input))
            println(process2(input))
        }

        data class Parsed(val towelTypes: List<List<Char>>, val desiredPatterns: List<List<Char>>)

        fun parseInput(input: String): Parsed {
            val (towelTypesStr, towelPatternsStr) = input.trimIndent().split("\n\n")
            return Parsed(
                towelTypes = towelTypesStr.split(", ").map { it.map { it } }.sortedByDescending { it.size },
                desiredPatterns = towelPatternsStr.lines().take(4000).map { it.map { it } }
            )
        }

        var cache: MutableMap<List<Char>, Long> = mutableMapOf(listOf<Char>() to 1)
        fun arrangementsAll(towelTypes: List<List<Char>>, desiredPattern: List<Char>): Long =
            cache.getOrPut(desiredPattern) {
                towelTypes
                    .filter { desiredPattern.size >= it.size && desiredPattern.zip(it).all { (a, b) -> a == b } }
                    .sumOf { next -> arrangementsAll(towelTypes, desiredPattern.drop(next.size)) }
            }

        fun process(parsed: Parsed) = parsed.desiredPatterns.count { arrangementsAll(parsed.towelTypes, it) > 0L }

        fun process2(parsed: Parsed) = parsed.desiredPatterns.sumOf { arrangementsAll(parsed.towelTypes, it) }
    }
}