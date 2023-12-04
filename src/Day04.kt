fun main() {
    val d = Day04()
    d.part1().println()
}

class Day04 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val scratchcards = input.map { line ->
        val (_, numbers) = line.split(':')
        val (winning, have) = numbers.split('|')
        Scratchcard(winning.toIntList(), have.toIntList())
    }

    data class Scratchcard(val winning: List<Int>, val have: List<Int>) {
        fun points(): Int {
            val matches = have.filter { winning.contains(it) }.size
            return when(matches) { 0 -> 0 else -> 1 shl (matches-1)}
        }
    }

    fun part1(): Int = scratchcards.sumOf { it.points() }
}
