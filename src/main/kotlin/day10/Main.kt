package day10

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2(
                """
                    89010123
                    78121874
                    87430965
                    96549874
                    45678903
                    32019012
                    01329801
                    10456732"""
            ).also(::println).takeIf { it == 81 } ?: error("example failed")
            val input = Utils.getInput(10)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    private fun parse(input: String): List<List<Int>> {
        return input
            .trimIndent()
            .lines()
            .map { it.map { it.digitToInt() } }
    }

    fun process(input: String): Int {
        val grid = parse(input)
        return grid.indices.flatMap { r ->
                grid[r].indices.mapNotNull { c ->
                    if (grid[r][c] == 0) score(Pair(r, c), grid).size else null
                }
            }
            .also { println(it) }
            .sum()
    }

    fun score(p: Pair<Int, Int>, grid: List<List<Int>>): Set<Pair<Int, Int>> {
        val (r, c) = p
        val v = grid[r][c]
        if (v == 9) return setOf(Pair(r, c))
        val ns = neighbors(r, c, grid)
        return ns
            .filter { (r, c) -> grid[r][c] == v + 1 }
            .flatMap { score(it, grid) }
            .toSet()
    }

    fun process2(input: String): Int {
        val grid = parse(input)
        return grid.indices.sumOf { r ->
            grid[r].indices.sumOf { c ->
                if (grid[r][c] == 0) rating(Pair(r, c), grid) else 0
            }
        }
    }


    fun rating(p: Pair<Int, Int>, grid: List<List<Int>>): Int {
        val (r, c) = p
        val v = grid[r][c]
        if (v == 9) return 1
        val ns = neighbors(r, c, grid)
        return ns
            .filter { (r, c) -> grid[r][c] == v + 1 }
            .sumOf { rating(it, grid) }
    }

    fun neighbors(r: Int, c: Int, grid: List<List<Int>>) = listOf(
        Pair(r - 1, c),
        Pair(r, c - 1),
        Pair(r, c + 1),
        Pair(r + 1, c),
    ).filter { (r, c) -> r in grid.indices && c in grid[r].indices }
}
