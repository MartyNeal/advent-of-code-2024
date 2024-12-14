package day3

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process(
                "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
            ).also(::println)
            Main().process2(
                "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"
            ).also(::println)

            val memory = Utils.getInput(3)
            println(Main().process(memory))
            println(Main().process2(memory))
        }
    }

    fun process(memory: String) = Regex("""mul\((\d+),(\d+)\)""")
        .findAll(memory)
        .map { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
        .sum()

    fun process2(memory: String) = Regex("""mul\((\d+),(\d+)\)|do\(\)|don't\(\)""")
        .findAll(memory)
        .fold(Pair(true, 0)) { acc, matchResult ->
            when (matchResult.value) {
                "do()" -> acc.copy(first = true)
                "don't()" -> acc.copy(first = false)
                else -> acc.copy(
                    second = if (acc.first)
                        acc.second + matchResult.groupValues[1].toInt() * matchResult.groupValues[2].toInt()
                    else acc.second
                )
            }
        }.second
}
