package day11

import kotlin.time.measureTime

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().process2("125 17", 25).also(::println).takeIf { it == 55312L } ?: error("example failed")
            val input = Utils.getInput(11)
            println(Main().process2(input, 25))
            println(Main().process2(input, 75))
        }
    }

    fun process(input: String, blinkCount: Int): Int {
        val stones = input.split(" ").map { it.toLong() }
        return generateSequence(stones) { blink(it) }
            .take(blinkCount + 1)
            .onEach { println(it) }
            .last().size
    }

    fun blink(stones: List<Long>) = stones.flatMap { transformStone(it) }

    val L1: List<Long> = listOf(1L)

    fun transformStone(stone: Long): List<Long> =
        stone.toString().let { s ->
            when {
                stone == 0L -> L1
                s.length % 2 == 0 -> listOf(s.substring(0, s.length / 2).toLong(), s.substring(s.length / 2).toLong())
                else -> listOf(stone * 2024)
            }
        }

    fun process2(input: String, blinkCount: Int): Long {
        val stones = input.split(" ").map { it.toLong() }
        return stones.sumOf { lenAfterSteps(it, blinkCount) }
    }

    val cache = mutableMapOf<Pair<Long, Int>, Long>()
    fun lenAfterSteps(stone: Long, blinkCount: Int): Long {
        return cache.getOrPut(stone to blinkCount) {
            if (blinkCount == 0) 1
            else transformStone(stone).sumOf { lenAfterSteps(it, blinkCount - 1) }
        }
    }
}
