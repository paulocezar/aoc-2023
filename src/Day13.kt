import kotlin.math.min
import kotlin.math.max
fun main() {
    val d = Day13()
    d.part1().println()
    d.part2().println()
}

class Day13 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = patterns().map { summarize(it) }.reduce(Int::plus)
    fun part2() = patterns().map { summarize(it, 1) }.reduce(Int::plus)

    private fun summarize(pattern: List<String>, allowedSmudges: Int = 0) : Int {
        val horizontal = (1..<pattern.size).find { split ->
            val sz = min(split, pattern.size - split)
            allowedSmudges == (0..<sz).fold(0) { smudges, it ->
                smudges + pattern[split-it-1].foldIndexed(0) { idx, acc, c ->
                    acc + if (c != pattern[split+it][idx]) 1 else 0
                }
            }
        }
        val vertical = (1..<pattern[0].length).find { split ->
            val sz = min(split, pattern[0].length - split)
            allowedSmudges == (0..<sz).fold(0) { smudges, it ->
                smudges + pattern.indices.fold(0) { acc, row ->
                    acc + if (pattern[row][split-it-1] != pattern[row][split+it]) 1 else 0
                }
            }
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
