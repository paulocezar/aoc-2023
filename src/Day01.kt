fun main() {
    val d = Day01()
    d.part1().println()
}

class Day01() {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1(): Int {
        return input.sumOf { calibrationValue(it) }
    }

    private fun calibrationValue(line: String): Int {
        val firstDigit = line.first { it.isDigit() }.digitToInt()
        val lastDigit = line.last { it.isDigit() }.digitToInt()
        return 10*firstDigit + lastDigit
    }
}
