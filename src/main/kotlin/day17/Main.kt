package day17

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
                Register A: 729
                Register B: 0
                Register C: 0

                Program: 0,1,5,4,3,0
                """

//            Main().process(exampleInput).also(::println).takeIf { it == "4,6,3,5,6,3,5,2,1,0" }
//                ?: error("example failed")
            val input = Utils.getInput(17)
            println(Main().process(input))
        }
    }

    data class Machine(val a: Int, val b: Int, val c: Int, val program: List<Int>)

    fun parse(input: String): Machine =
        input
            .trimIndent()
            .lines()
            .filter(String::isNotEmpty)
            .map { it.split(": ", limit = 2)[1] }
            .let { Machine(it[0].toInt(), it[1].toInt(), it[2].toInt(), it[3].split(",").map(String::toInt)) }

    fun process(input: String): String {
        TODO()
    }

}
