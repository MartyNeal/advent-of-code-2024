package day13

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process(
                """
                    Button A: X+94, Y+34
                    Button B: X+22, Y+67
                    Prize: X=8400, Y=5400
                    
                    Button A: X+26, Y+66
                    Button B: X+67, Y+21
                    Prize: X=12748, Y=12176
                    
                    Button A: X+17, Y+86
                    Button B: X+84, Y+37
                    Prize: X=7870, Y=6450
                    
                    Button A: X+69, Y+23
                    Button B: X+27, Y+71
                    Prize: X=18641, Y=10279
                    """
            ).also(::println).takeIf { it == 480L } ?: error("example failed")
            val input = Utils.getInput(13)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    data class Machine(val aButton: Pair<Int, Int>, val bButton: Pair<Int, Int>, var prize: Pair<Long, Long>) {
        companion object {
            fun fromString(input: String): Machine {
                val lines = input.lines()
                val aButton = lines[0].let { it.slice(12..13).toInt() to it.slice(18..19).toInt() }
                val bButton = lines[1].let { it.slice(12..13).toInt() to it.slice(18..19).toInt() }
                val prize = lines[2].let { it.slice(it.indexOf('=') + 1 until it.lastIndexOf(",")).toLong() to it.slice(it.lastIndexOf('=') + 1 until it.length).toLong() }
                return Machine(aButton, bButton, prize)
            }

            fun fromString2(input: String) = fromString(input).let { it.copy(prize = it.prize.first + 10000000000000L to it.prize.second + 10000000000000L) }
        }

        fun cramer(a: Int, b: Int, c: Int, d: Int, e: Long, f: Long): Pair<Long, Long>? {
            val det = a * d - b * c
            val xNumerator = e * d - b * f
            val yNumerator = a * f - e * c
            if (det == 0 || xNumerator % det != 0L || yNumerator % det != 0L) return null
            return Pair(xNumerator / det, yNumerator / det)
        }

        fun findMinCostToPrize() =
            cramer(aButton.first, bButton.first, aButton.second, bButton.second, prize.first, prize.second)
                ?.let { it.first * 3 + it.second }
                ?: 0L
    }


    fun process(input: String) =
        input
            .trimIndent()
            .split("\n\n")
            .map { Machine.fromString(it) }
            .sumOf { it.findMinCostToPrize() }


    fun process2(input: String) =
        input
            .trimIndent()
            .split("\n\n")
            .map { Machine.fromString2(it) }
            .sumOf { it.findMinCostToPrize() }
}
