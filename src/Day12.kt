import kotlin.math.min
fun main() {
    val d = Day12()
    d.part1().println()
}

class Day12 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = input.map { Record.parse(it).countArrangements() }.reduce(Int::plus)

    data class Record(val condition: String, val brokenGroups: List<Int>) {
        fun countArrangements() = count(0, 0, 0)

        private fun count(spring: Int, broken: Int, group: Int) : Int {
            if (group == brokenGroups.size) return if (condition.lastIndexOf('#') >= spring) 0 else 1
            if (spring == condition.length) return if (broken == brokenGroups[group]) count(spring, 0, group+1) else 0
            val options = when (condition[spring]) {
                '#' -> "#"
                '.' -> "."
                '?' -> "#."
                else -> throw Exception("unexpected spring condition ${condition[spring]}")
            }
            var res = 0
            options.forEach { state ->
                if (state == '.') {
                    if (broken == 0 || broken == brokenGroups[group])
                        res += count(spring+1, 0, group + min(broken, 1))
                } else {
                    if (broken+1 <= brokenGroups[group])
                        res += count(spring+1, broken+1, group)
                }
            }
            return res
        }

        companion object {
            fun parse(input: String): Record {
                val (condition, groups) = input.split(' ')
                return Record(condition, groups.toIntList(','))
            }
        }
    }
}
