package day24

import Utils
import java.math.BigInteger
import kotlin.random.Random
import kotlin.time.measureTime

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput = """
                x00: 1
                x01: 0
                x02: 1
                x03: 1
                x04: 0
                y00: 1
                y01: 1
                y02: 1
                y03: 1
                y04: 1
                
                ntg XOR fgs -> mjb
                y02 OR x01 -> tnw
                kwq OR kpj -> z05
                x00 OR x03 -> fst
                tgd XOR rvg -> z01
                vdt OR tnw -> bfw
                bfw AND frj -> z10
                ffh OR nrd -> bqk
                y00 AND y03 -> djm
                y03 OR y00 -> psh
                bqk OR frj -> z08
                tnw OR fst -> frj
                gnj AND tgd -> z11
                bfw XOR mjb -> z00
                x03 OR x00 -> vdt
                gnj AND wpb -> z02
                x04 AND y00 -> kjc
                djm OR pbm -> qhw
                nrd AND vdt -> hwm
                kjc AND fst -> rvg
                y04 OR y02 -> fgs
                y01 AND x02 -> pbm
                ntg OR kjc -> kwq
                psh XOR fgs -> tgd
                qhw XOR tgd -> z09
                pbm OR djm -> kpj
                x03 XOR y03 -> ffh
                x00 XOR y04 -> ntg
                bfw OR bqk -> z06
                nrd XOR fgs -> wpb
                frj XOR qhw -> z04
                bqk OR frj -> z07
                y03 OR x01 -> nrd
                hwm AND bqk -> z03
                tgd XOR rvg -> z12
                tnw OR pbm -> gnj
                """

            process(parseInput(exampleInput)).also(::println).takeIf { it == 2024L } ?: error("example failed")
            val input = parseInput(Utils.getInput(24))
            println(process(input))
            println(process2(input))
        }

        enum class Operation(val op: (Boolean, Boolean) -> Boolean) {
            AND(Boolean::and), OR(Boolean::or), XOR(Boolean::xor);
        }

        data class Connection(val input1: String, val input2: String, val operation: Operation, val output: String)
        data class Parsed(val initialWires: Map<String, Boolean>, val connections: Map<String, Connection>, val zs: List<String>)
        fun parseInput(input: String): Parsed {
            val parts = input.trimIndent().split("\n\n");
            val wires = parts[0].lines().associate { line ->
                val (wire, value) = line.split(": ")
                wire to (value == "1")
            }
            val connections = parts[1].lines().map { line ->
                val (input1, operation, input2, _, output) = line.split(" ")
                Connection(input1, input2, Operation.valueOf(operation), output)
            }.associateBy { it.output }
            val zs = connections.keys
                .filter { k -> k.startsWith("z") }
                .sortedDescending()
            return Parsed(wires, connections, zs)
        }


        fun processMachine(parsed: Parsed): List<Boolean>? {
            val wires = parsed.initialWires.toMutableMap()
            fun getValue(wire: String, seen: Set<String> = setOf()): Boolean? {
                if (seen.contains(wire)) return null
                if (wire in wires) return wires[wire]!!
                val connection = parsed.connections[wire]!!
                val value = connection.operation.op(getValue(connection.input1, seen + wire) ?: return null, getValue(connection.input2, seen + wire) ?: return null)
                wires[wire] = value
                return value
            }

            return parsed.zs.map { getValue(it) ?: return null }
        }

        fun process(parsed: Parsed) = processMachine(parsed)!!
            .joinToString("") { if (it) "1" else "0" }
            .toLong(2)

        fun process2(parsed: Parsed): String {
            println("building test cases...")
            // 32 test cases seems to be a sweet spot for this problem.
            // On good hardware, this takes about 1 minute to find the weights, and 1 second to find the swaps.
            // reducing the number of test cases to 16 will make the program run faster, but might cause one of the 4 swaps to be way down the list
            // which will make the program run very slow on the subsequent search.
            val testCases = List(32) {
                val (x, y) = List(2) { Random.nextLong(2L.pow(parsed.zs.size - 1)) }
                val z = x + y
                val wires =
                    listOf(x to 'x', y to 'y').fold(emptyMap<String, Boolean>()) { a, e -> a +
                            e.first
                                .toString(2)
                                .padStart(parsed.zs.size, '0')
                                .reversed()
                                .withIndex()
                                .associate { (i, c) -> "${e.second}${i.toString().padStart(2, '0')}" to (c == '1') }
                    }
                val zs = z.toString(2).padStart(parsed.zs.size, '0').map { it == '1' }
                parsed.copy(initialWires = wires) to zs
            }

            println("finding the swaps that gives the most correct zs across all test cases...")
            val weights = mutableMapOf<Pair<String, String>, Int>().withDefault { 0 }
            measureTime {
                val tmpMap = parsed.connections.toMutableMap()
                for (k1 in parsed.connections.keys) {
                    for (k2 in parsed.connections.keys) {
                        if (k1 <= k2) continue
                        val c1 = parsed.connections[k1]!!
                        val c2 = parsed.connections[k2]!!
                        tmpMap[k1] = c2
                        tmpMap[k2] = c1
                        testCases.forEach { (machine, expectedZs) ->
                            processMachine(machine.copy(connections = tmpMap))?.let { result ->
                                val weight = result.zip(expectedZs).count { it.first == it.second }
                                weights[k1 to k2] = weights.getValue(k1 to k2) + weight
                            }
                        }
                        tmpMap[k1] = c1
                        tmpMap[k2] = c2
                    }
                }
            }.also { println("took $it") }

            println("choosing 4 swaps at a time that gives the most correct zs across all test cases...")
            val ret: String
            measureTime {
                ret = weights.entries
                    .sortedByDescending { it.value }
                    .map { it.key }
                    .asSequence()
                    .chooseMaximal(4)
                    .first { (kvp1, kvp2, kvp3, kvp4) ->
                        val tmpMap = parsed.connections.toMutableMap()
                        tmpMap[kvp1.first] = parsed.connections[kvp1.second]!!
                        tmpMap[kvp1.second] = parsed.connections[kvp1.first]!!
                        tmpMap[kvp2.first] = parsed.connections[kvp2.second]!!
                        tmpMap[kvp2.second] = parsed.connections[kvp2.first]!!
                        tmpMap[kvp3.first] = parsed.connections[kvp3.second]!!
                        tmpMap[kvp3.second] = parsed.connections[kvp3.first]!!
                        tmpMap[kvp4.first] = parsed.connections[kvp4.second]!!
                        tmpMap[kvp4.second] = parsed.connections[kvp4.first]!!
                        testCases.all { (machine, expectedZs) ->
                            processMachine(machine.copy(connections = tmpMap))?.zip(expectedZs)
                                ?.all { it.first == it.second } ?: false
                        }
                    }
                    .flatMap { it.toList() }
                    .sorted()
                    .joinToString(",")
            }.also { println("took $it") }
            return ret
        }

        // A choose function that returns all possible combinations of n elements from a list.
        // ordered by maximum element, then next maximum element and so on.
        fun <T> Sequence<T>.chooseMaximal(n: Int): Sequence<List<T>> {
            if (n == 0) return sequenceOf(emptyList())
            val iterator = iterator()
            if (!iterator.hasNext()) return emptySequence()
            val list = mutableListOf<T>()
            repeat(n - 1) { list.add(iterator.next()) }
            return sequence {
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    yieldAll(list.asSequence().chooseMaximal(n-1).map { it + next })
                    list.add(next)
                }
            }
        }

        fun Long.pow(exp: Int) = BigInteger.valueOf(this).pow(exp).toLong()
    }
}