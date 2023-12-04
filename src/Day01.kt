fun main() {
    val d = Day01()
    d.part1().println()
    d.part2().println()
}

class Day01() {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val validDigits: Map<String, Int> = mapOf(
            "0" to 0,
            "one" to 1, "1" to 1,
            "two" to 2, "2" to 2,
            "three" to 3, "3" to 3,
            "four" to 4, "4" to 4,
            "five" to 5, "5" to 5,
            "six" to 6, "6" to 6,
            "seven" to 7, "7" to 7,
            "eight" to 8, "8" to 8,
            "nine" to 9, "9" to 9
    )

    fun part1(): Int = input.sumOf { calibrationValue(it) }

    fun part2(): Int = input.sumOf { extendedCalibrationValue(it) }

    private fun calibrationValue(line: String): Int {
        val firstDigit = line.first { it.isDigit() }.digitToInt()
        val lastDigit = line.last { it.isDigit() }.digitToInt()
        return 10*firstDigit + lastDigit
    }

    private fun extendedCalibrationValue(line: String): Int {
        val firstDigit = validDigits.get(line.findAnyOf(validDigits.keys)!!.second)!!
        val lastDigit = validDigits.get(line.findLastAnyOf(validDigits.keys)!!.second)!!
        return 10*firstDigit + lastDigit
    }
}
