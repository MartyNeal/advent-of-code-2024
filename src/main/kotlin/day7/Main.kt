package day7

import Utils

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process(
                """
                    190: 10 19
                    3267: 81 40 27
                    83: 17 5
                    156: 15 6
                    7290: 6 8 6 15
                    161011: 16 10 13
                    192: 17 8 14
                    21037: 9 7 18 13
                    292: 11 6 16 20""".trimIndent(), Main::canTotal2
            ).also(::println)
            val input = Utils.getInput(7)
            println(Main().process(input, Main::canTotal))
            println(Main().process(input, Main::canTotal2))
        }

        private fun canTotal(total: Long, operands: List<Long>): Boolean {
            if (operands.isEmpty()) return total == 0L
            else if (total == 0L) return false
            val operand = operands.last()
            val remaining = operands.subList(0, operands.size - 1)
            return total % operand == 0L && canTotal(total / operand, remaining) ||
                    total >= operand && canTotal(total - operand, remaining)
        }


        private fun canTotal2(total: Long, operands: List<Long>): Boolean {
            if (operands.isEmpty()) return total == 0L
            else if (total == 0L) return false
            val operand = operands.last()
            val remaining = operands.subList(0, operands.size - 1)
            return total % operand == 0L && canTotal2(total / operand, remaining) ||
                    total >= operand && canTotal2(total - operand, remaining) ||
                    (total.toString().endsWith(operand.toString()) && canTotal2(total.toString().substringBeforeLast(operand.toString()).takeIf { it.length > 0 }?.toLong() ?: 0L, remaining))
        }
    }

    fun process(input: String, canTotalFunc: (Long, List<Long>) -> Boolean): Long {
        val regex = ":? ".toRegex()
        return input.lines()
            .filter(String::isNotEmpty)
            .sumOf { line ->
                val rawLine = line.split(regex).map { it.toLong() }
                val total = rawLine[0]
                val operands = rawLine.drop(1)
                if (canTotalFunc(total, operands)) total else 0L
            }
    }
}