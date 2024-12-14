package day4

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2(
                """
                   MMMSXXMASM
                   MSAMXMSMSA
                   AMXSXMAAMM
                   MSAMASMSMX
                   XMASAMXAMM
                   XXAMMXXAMA
                   SMSMSASXSS
                   SAXAMASAAA
                   MAMMMXMMMM
                   MXMXAXMASX"""
            ).also(::println)
            val input = Utils.getInput(4)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    fun process(input: String): Int {
        val rows = input
            .trimIndent()
            .lines()
            .map { it.split("").filter(String::isNotEmpty).map { it[0] } }
        return rows.indices.sumOf { r ->
            rows[r].indices.sumOf { c ->
                if (rows[r][c] != 'X') 0
                else
                    0 +
                            (if (
                                rows.getOrNull(r + 1)?.getOrNull(c - 1) == 'M' &&
                                rows.getOrNull(r + 2)?.getOrNull(c - 2) == 'A' &&
                                rows.getOrNull(r + 3)?.getOrNull(c - 3) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r + 1)?.getOrNull(c + 0) == 'M' &&
                                rows.getOrNull(r + 2)?.getOrNull(c + 0) == 'A' &&
                                rows.getOrNull(r + 3)?.getOrNull(c + 0) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r + 1)?.getOrNull(c + 1) == 'M' &&
                                rows.getOrNull(r + 2)?.getOrNull(c + 2) == 'A' &&
                                rows.getOrNull(r + 3)?.getOrNull(c + 3) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r + 0)?.getOrNull(c + 1) == 'M' &&
                                rows.getOrNull(r + 0)?.getOrNull(c + 2) == 'A' &&
                                rows.getOrNull(r + 0)?.getOrNull(c + 3) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r + 0)?.getOrNull(c - 1) == 'M' &&
                                rows.getOrNull(r + 0)?.getOrNull(c - 2) == 'A' &&
                                rows.getOrNull(r + 0)?.getOrNull(c - 3) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r - 1)?.getOrNull(c - 1) == 'M' &&
                                rows.getOrNull(r - 2)?.getOrNull(c - 2) == 'A' &&
                                rows.getOrNull(r - 3)?.getOrNull(c - 3) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r - 1)?.getOrNull(c + 0) == 'M' &&
                                rows.getOrNull(r - 2)?.getOrNull(c + 0) == 'A' &&
                                rows.getOrNull(r - 3)?.getOrNull(c + 0) == 'S'
                            ) 1 else 0) +
                            (if (
                                rows.getOrNull(r - 1)?.getOrNull(c + 1) == 'M' &&
                                rows.getOrNull(r - 2)?.getOrNull(c + 2) == 'A' &&
                                rows.getOrNull(r - 3)?.getOrNull(c + 3) == 'S'
                            ) 1 else 0)
            }
        }
    }

    fun process2(input: String): Int {
        val rows = input
            .trimIndent()
            .lines()
            .map { it.split("").filter(String::isNotEmpty).map { it[0] } }
        return rows.indices.drop(1).dropLast(1).sumOf { r ->
            rows[r].indices.drop(1).dropLast(1).count { c ->
                if (rows[r][c] != 'A') false
                else listOf(rows[r - 1][c - 1], rows[r + 1][c + 1]).sorted() == listOf('M', 'S') &&
                        listOf(rows[r + 1][c - 1], rows[r - 1][c + 1]).sorted() == listOf('M', 'S')
            }
        }
    }
}