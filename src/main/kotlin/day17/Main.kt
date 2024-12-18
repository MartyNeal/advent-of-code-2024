package day17

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val exampleMachine1 = Machine(0L, 0L, 9L, listOf(2, 6))
            val exampleMachine2 = Machine(10L, 0L, 0L, listOf(5, 0, 5, 1, 5, 4))
            val exampleMachine3 = Machine(2024L, 0L, 0L, listOf(0, 1, 5, 4, 3, 0))
            val exampleMachine4 = Machine(0L, 29L, 0L, listOf(1, 7))
            val exampleMachine5 = Machine(0L, 2024L, 43690L, listOf(4, 0))
            val exampleMachine6 = Machine(729L, 0L, 0L, listOf(0, 1, 5, 4, 3, 0))
            val exampleMachine7 = Machine(117440L, 0L, 0L, listOf(0, 3, 5, 4, 3, 0))

            Main().processMachine(exampleMachine1).takeIf { (machine, output) -> machine.b == 1L }
                ?: error("example1 failed")
            Main().processMachine(exampleMachine2).takeIf { (machine, output) -> output == listOf(0, 1, 2) }
                ?: error("example2 failed")
            Main().processMachine(exampleMachine3)
                .takeIf { (machine, output) -> machine.a == 0L && output == listOf(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0) }
                ?: error("example3 failed")
            Main().processMachine(exampleMachine4).takeIf { (machine, output) -> machine.b == 26L }
                ?: error("example4 failed")
            Main().processMachine(exampleMachine5).takeIf { (machine, output) -> machine.b == 44354L }
                ?: error("example5 failed")
            Main().processMachine(exampleMachine6)
                .takeIf { (machine, output) -> output == listOf(4, 6, 3, 5, 6, 3, 5, 2, 1, 0) }
                ?: error("example6 failed")
            Main().processMachine(exampleMachine7).takeIf { (machine, output) -> output == machine.program }
                ?: error("example7 failed")

            val input = Utils.getInput(17)
            println(Main().process(input))
            val reasonableCheckpoint = Main().process2(input, 8, 0)
            println(Main().process2(input, 16, reasonableCheckpoint))
        }
    }

    data class Machine(var a: Long, var b: Long, var c: Long, val program: List<Int>) {
        fun combo(operand: Int): Long = when (operand) {
            in 0..3 -> operand.toLong()
            4 -> a
            5 -> b
            6 -> c
            else -> throw IllegalArgumentException("Invalid operand: $operand")
        }
    }

    fun parse(input: String): Machine =
        input
            .trimIndent()
            .lines()
            .filter(String::isNotEmpty)
            .map { it.split(": ", limit = 2)[1] }
            .let { Machine(it[0].toLong(), it[1].toLong(), it[2].toLong(), it[3].split(",").map(String::toInt)) }

    fun process(input: String) = processMachine(parse(input)).second.joinToString(",")

    fun process2(input: String, i: Int, offset: Long): Long {
        val machine = parse(input)
        var l = 0L
        var s = 0
        val offsetBits = offset.toString(2).length
        while (true) {
            val j = (l shl offsetBits) or offset
            fastMachine(machine.copy(a = j))
                .takeIf { it >= i }
                ?.also { return j }
            l++
        }
    }

    fun processMachine(machine: Machine, abortIfNotQuine: Boolean = false): Pair<Machine, List<Int>> {
        val output = mutableListOf<Int>()
        var instructionPointer = 0
        while (true) {
            val opcode = machine.program.getOrNull(instructionPointer) ?: break
            val operand = machine.program[instructionPointer + 1]
            when (opcode) {
                0 -> machine.a = machine.a.shr(machine.combo(operand).toInt())   // adv
                1 -> machine.b = machine.b.xor(operand.toLong())                  // bxl
                2 -> machine.b = machine.combo(operand).and(7L)     // bst
                3 -> if (machine.a != 0L) instructionPointer = operand - 2 // jnz
                4 -> machine.b = machine.b.xor(machine.c)                // bxc
                5 -> {
                    output.add(machine.combo(operand).and(7L).toInt()) // out
                    if (abortIfNotQuine && output.last() != machine.program[output.size -1]) break
                }

                6 -> machine.b = machine.a.shr(machine.combo(operand).toInt())   // bdv
                7 -> machine.c = machine.a.shr(machine.combo(operand).toInt())   // cdv
            }
            instructionPointer += 2
        }
        return machine to output
    }

    fun fastMachine(machine: Machine): Int {
        var i = 0
        with(machine) {
            while (a > 0L) {
                b = a and 7L
                b = b xor 2L
                c = a shr b.toInt()
                b = b xor c
                b = b xor 3L
                if ((b and 7L) != machine.program[i++].toLong()) return i - 1
                a = a shr 3
            }
        }
        return i

    }
}
