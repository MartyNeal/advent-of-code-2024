package day25

import Utils
import day25.Main.KeyOrLock.Key
import day25.Main.KeyOrLock.Lock

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val exampleInput =
                    """
                    #####
                    .####
                    .####
                    .####
                    .#.#.
                    .#...
                    .....
                    
                    #####
                    ##.##
                    .#.##
                    ...##
                    ...#.
                    ...#.
                    .....
                    
                    .....
                    #....
                    #....
                    #...#
                    #.#.#
                    #.###
                    #####
                    
                    .....
                    .....
                    #.#..
                    ###..
                    ###.#
                    ###.#
                    #####
                    
                    .....
                    .....
                    .....
                    #....
                    #.#..
                    #.#.#
                    #####
                    """
            Main().process(Main().parse(exampleInput).also(::println)).also(::println).takeIf { it == 3 } ?: error("example failed")

            val keyOrLocks = Main().parse(Utils.getInput(25))
            println(Main().process(keyOrLocks))
        }
    }

    sealed class KeyOrLock(open val heights: List<Int>) {
        data class Key(override val heights: List<Int>) : KeyOrLock(heights)
        data class Lock(override val heights: List<Int>) : KeyOrLock(heights)
    }

    fun parse(input: String): List<KeyOrLock> =
        input.trimIndent().split("\n\n")
            .map { block ->
                val lines = block.lines()
                lines
                    .drop(1)
                    .take(5)
                    .fold(List(5) { 0 }) { a, e ->
                        a.zip(e.map { it }).map { (a: Int, b: Char) -> a + if (b == '#') 1 else 0 }
                    }
                    .let {
                        when (lines[0]) {
                            "#####" -> Lock(it)
                            "....." -> Key(it)
                            else -> error("Unknown type")
                        }
                    }
            }

    fun process(keyOrLocks: List<KeyOrLock>) =
        keyOrLocks.filterIsInstance<Lock>().sumOf { lock ->
            keyOrLocks.filterIsInstance<Key>().count { key ->
                key.heights.zip(lock.heights).all { (a, b) -> a + b <= 5 }
            }
        }
}