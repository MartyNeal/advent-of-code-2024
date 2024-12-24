package day23

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
                """
            process(parseInput(exampleInput)).also(::println).takeIf { it == 7 } ?: error("example failed")
            val input = parseInput(Utils.getInput(23))
            println(process(input))
            println(process2(input))
        }

        fun parseInput(input: String): List<List<String>> =
            input.trimIndent().lines().map { it.split('-') }

        fun process(lines: List<List<String>>) =
            groupByNodes(lines).let { nodeMap ->
                lines
                    .filter { it.any { it.startsWith("t") } }
                    .flatMap { (a, b) ->
                        nodeMap
                            .entries
                            .mapNotNull { (k, v) -> setOf(a, b, k).takeIf { v.contains(a) && v.contains(b) } }
                    }
                    .distinct()
                    .size
            }

        fun groupByNodes(lines: List<List<String>>) =
            buildMap {
                lines.groupByTo(this, { (a, _) -> a }, { (_, b) -> b })
                lines.groupByTo(this, { (_, b) -> b }, { (a, _) -> a })
            }

        fun process2(lines: List<List<String>>) =
            groupByNodes(lines)
                .let { nodeMap ->
                    lines
                        .mapNotNull { (k, v) ->
                            val clique = setOf(k, v)
                            val newClique = clique.toMutableSet()
                            nodeMap.forEach<String, List<String>> { (k, v) ->
                                if (newClique.all { v.contains(it) }) newClique.add(
                                    k
                                )
                            }
                            newClique.toSortedSet().takeIf { it.size > clique.size }
                        }
                        .distinct()
                }
                .maxBy { it.size }
                .joinToString(",")
    }
}