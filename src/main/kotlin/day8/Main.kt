package day8

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2(
                """
                    ............
                    ........0...
                    .....0......
                    .......0....
                    ....0.......
                    ......A.....
                    ............
                    ............
                    ........A...
                    .........A..
                    ............
                    ............"""
            ).also(::println)
            val input = Utils.getInput(8)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    private fun parse(input: String): Pair<Map<Char, List<Pair<Int, Int>>>, Pair<Int, Int>> {
        val rows = input
            .trimIndent()
            .lines()
            .map { it.split("").filter(String::isNotEmpty).map { it[0] } }
        return Pair(buildMap<Char, MutableList<Pair<Int, Int>>> {
            rows.forEachIndexed { r, row ->
                row.forEachIndexed { c, cell ->
                    if (cell != '.') {
                        put(cell, getOrDefault(cell, mutableListOf()).apply { add(Pair(r, c)) })
                    }
                }
            }
        }, Pair(rows.size, rows[0].size))
    }

    fun process(input: String): Int {
        val (antennasMap, citySize) = parse(input)
        fun inRange(r: Int, c: Int) = r in 0..<citySize.first && c in 0..<citySize.second
        return antennasMap.values.flatMap { antennas ->
            val antenodes = mutableListOf<Pair<Int, Int>>()
            antennas.forEachIndexed { i, a1 ->
                antennas.drop(i + 1).forEach { a2 ->
                    val (r1, c1) = a1
                    val (r2, c2) = a2
                    val dr = r2 - r1
                    val dc = c2 - c1
                    Pair(r2 + dr, c2 + dc).takeIf { (a, b) -> inRange(a, b) }?.let { antenodes.add(it) }
                    Pair(r1 - dr, c1 - dc).takeIf { (a, b) -> inRange(a, b) }?.let { antenodes.add(it) }
                }
            }
            antenodes
        }.toSet().also(::println).size
    }

    fun process2(input: String): Int {
        val (antennasMap, citySize) = parse(input)
        fun inRange(r: Int, c: Int) = r in 0..<citySize.first && c in 0..<citySize.second
        return antennasMap.values.flatMap { antennas ->
            val antenodes = mutableListOf<Pair<Int, Int>>()
            antennas.forEachIndexed { i, a1 ->
                antennas.drop(i + 1).forEach { a2 ->
                    val (r1, c1) = a1
                    val (r2, c2) = a2
                    val dr = r2 - r1
                    val dc = c2 - c1
                    generateSequence(a1) { (r, c) -> Pair(r + dr, c + dc) }
                        .takeWhile { (r, c) -> inRange(r, c) }
                        .forEach(antenodes::add)
                    generateSequence(a1) { (r, c) -> Pair(r - dr, c - dc) }
                        .takeWhile { (r, c) -> inRange(r, c) }
                        .forEach(antenodes::add)
                }
            }
            antenodes
        }.toSet().also(::println).size
    }

}
