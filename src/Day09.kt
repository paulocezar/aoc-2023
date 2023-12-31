fun main() {
    val d = Day09()
    d.part1().println()
    d.part2().println()
}

class Day09 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = input.map { extrapolate(it.toIntList()) }.reduce(Int::plus)
    fun part2() = input.map { extrapolateBackwards(it.toIntList()) }.reduce(Int::plus)

    private fun extrapolate(values: List<Int>): Int {
        if (values.all { it == values[0] }) return values[0]
        val next = extrapolate(values.zipWithNext { a, b -> b - a })
        return values.last() + next
    }

    private fun extrapolateBackwards(values: List<Int>): Int {
        if (values.all { it == values[0] }) return values[0]
        val prev = extrapolateBackwards(values.zipWithNext { a, b -> b - a })
        return values.first() - prev
    }
}
