fun main() {
    val d = Day15()
    d.part1().println()
}

class Day15 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = input[0].split(',').map { hash(it) }.reduce(Int::plus)

    private fun hash(step: String) = step.fold(0) { currentValue, char ->
        ((currentValue + char.code) * 17) % 256
    }
}
