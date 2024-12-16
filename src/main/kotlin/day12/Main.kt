package day12

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process(
                """
                    AAAA
                    BBCD
                    BBCC
                    EEEC
                    """
            ).also(::println).takeIf { it == 140L } ?: error("example1 failed")

            Main().process(
                """
                    OOOOO
                    OXOXO
                    OOOOO
                    OXOXO
                    OOOOO
                    """
            ).also(::println).takeIf { it == 772L } ?: error("example2 failed")

            Main().process(
                """
                    RRRRIICCFF
                    RRRRIICCCF
                    VVRRRCCFFF
                    VVRCCCJFFF
                    VVVVCJJCFE
                    VVIVCCJJEE
                    VVIIICJJEE
                    MIIIIIJJEE
                    MIIISIJEEE
                    MMMISSJEEE
                    """
            ).also(::println).takeIf { it == 1930L } ?: error("example3 failed")
            val input = Utils.getInput(12)
            println(Main().process(input))

            Main().process2(
                """
                    AAAA
                    BBCD
                    BBCC
                    EEEC
                    """
            ).also(::println).takeIf { it == 80L } ?: error("example1 failed")
            println(Main().process2(input))
        }
    }

    private fun parse(input: String): List<List<Char>> {
        return input
            .trimIndent()
            .lines()
            .map { it.map { it } }
    }

    fun process(input: String): Long {
        val grid = parse(input)
        return findRegions(grid).sumOf { r ->
            r.area.toLong() * r.perimeter.toLong()
        }
    }

    fun process2(input: String): Long {
        val grid = parse(input)
        return findRegions(grid).sumOf { r ->
            r.area.toLong() * r.sides.toLong()
        }
    }

    fun findRegions(grid: List<List<Char>>): List<Region> {
        val regions = mutableListOf<Region>()
        val seen = mutableSetOf<Pair<Int, Int>>()
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (!seen.contains(Pair(r, c))) {
                    val region = Region(grid[r][c])
                    findRegion(r, c, grid, region, seen)
                    regions.add(region)
                }
            }
        }
        return regions
    }

    fun findRegion(r: Int, c: Int, grid: List<List<Char>>, region: Region, seen: MutableSet<Pair<Int, Int>>) {
        if (r !in grid.indices || c !in grid[r].indices || grid[r][c] != region.symbol || seen.contains(Pair(r, c))) return
        seen.add(Pair(r, c))
        region.area++

        val ns = neighbors(r, c, grid).filter { (r, c) -> grid[r][c] == region.symbol && seen.contains(Pair(r, c)) }
        region.perimeter += 4 - neighbors(r, c, grid).count { (r, c) -> grid[r][c] == region.symbol }
        region.sides += when (ns.size) {
            0 -> 4 // starting point
            1,2,3 -> {
                // number of kiddy corners of self that share a side with the neighbor
                listOf(
                    Pair(r - 1, c - 1),
                    Pair(r - 1, c + 1),
                    Pair(r + 1, c - 1),
                    Pair(r + 1, c + 1)
                ).count { (r, c) ->
                    grid.getOrNull(r)?.getOrNull(c) == region.symbol &&
                            seen.contains(Pair(r, c)) &&
                            (neighbors(r, c, grid).intersect(ns).size) == 1
                } * 2 - (ns.size - 1) * 2
            }
            4 -> -4 // filling in a hole
            else -> throw IllegalStateException()
        }
        if (ns.size == 2 && (ns[0].first == ns[1].first || ns[0].second == ns[1].second)) {
            region.sides -= 2
        }
        neighbors(r, c, grid).forEach { (r, c) -> findRegion(r, c, grid, region, seen) }
    }

    data class Region(var symbol: Char, var area: Int = 0, var perimeter: Int = 0, var sides: Int = 0)

    fun neighbors(r: Int, c: Int, grid: List<List<Any>>) = listOf(
        Pair(r - 1, c),
        Pair(r, c - 1),
        Pair(r, c + 1),
        Pair(r + 1, c),
    ).filter { (r, c) -> r in grid.indices && c in grid[r].indices }
}
