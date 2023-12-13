import kotlin.math.min
fun main() {
    val d = Day13()
    d.part1().println()
}

class Day13 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = patterns().map { summarize(it) }.reduce(Int::plus)

    private fun summarize(pattern: List<String>) : Int {
        val horizontal = (1..<pattern.size).findLast { split ->
            val sz = min(split, pattern.size - split)
            (0..<sz).all { pattern[split-it-1] == pattern[split+it] }
        }
        val vertical = (1..<pattern[0].length).findLast { split ->
            val sz = min(split, pattern[0].length - split)
            (0..<sz).all { pattern.indices.all { row -> pattern[row][split-it-1] == pattern[row][split+it] } }
        }
        return 100 * (horizontal ?: 0) + (vertical ?: 0)
    }

    private fun patterns() = sequence<List<String>> {
        var prevEnd = -1
        input.forEachIndexed { index, s ->
            if (s.isEmpty()) {
                yield(input.subList(prevEnd+1, index))
                prevEnd = index
            }
        }
        yield(input.subList(prevEnd+1, input.size))
    }
}
