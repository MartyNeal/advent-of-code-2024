package day5

import Utils

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2(
                """
                   47|53
                   97|13
                   97|61
                   97|47
                   75|29
                   61|13
                   75|53
                   29|13
                   97|29
                   53|29
                   61|53
                   97|53
                   61|29
                   47|13
                   75|47
                   97|75
                   47|61
                   75|61
                   47|29
                   75|13
                   53|13
                   
                   75,47,61,53,29
                   97,61,53,29,13
                   75,29,13
                   75,97,47,61,53
                   61,13,29
                   97,13,75,29,47""".trimIndent()
            ).also(::println)

            val input = Utils.getInput(5)
            println(Main().process(input))
            println(Main().process2(input))
        }
    }

    fun process(input: String): Int {
        val (rules, updates) = input.split("\n\n")
        val ruleMap = rules.split("\n").groupBy({ r -> r.split("|")[0].toInt()}, { r -> r.split("|")[1].toInt() })
        return updates
            .split("\n")
            .filter(String::isNotEmpty)
            .map { pages -> pages.split(",").map { it.toInt() } }
            .filter { pages -> pages.indices.all { i ->
                !pages.subList(i + 1, pages.size).any { subpage -> ruleMap[subpage]?.contains(pages[i]) == true }
            } }
            .sumOf { pages -> pages[(pages.size - 1) / 2] }
    }


    fun process2(input: String): Int {
        val (rules, updates) = input.split("\n\n")
        val ruleMap = rules.split("\n").groupBy({ r -> r.split("|")[0].toInt()}, { r -> r.split("|")[1].toInt() })
        return updates
            .split("\n")
            .filter(String::isNotEmpty)
            .map { pages -> pages.split(",").map { it.toInt() } }
            .filter { pages -> !pages.indices.all { i ->
                !pages.subList(i + 1, pages.size).any { subpage -> ruleMap[subpage]?.contains(pages[i]) == true }
            } }
            .map { pages ->
                val ml = pages.toMutableList()
                pages.indices.forEach { i ->
                    ((i+1)..< pages.size).forEach { j ->
                        if (ruleMap[ml[j]]?.contains(ml[i]) == true) {
                            val temp = ml[i]
                            ml[i] = ml[j]
                            ml[j] = temp
                        }
                    }
                }
                ml
            }
            .sumOf { pages -> pages[(pages.size - 1) / 2] }
    }
}