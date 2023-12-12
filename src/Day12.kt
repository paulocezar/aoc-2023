import kotlin.math.min
fun main() {
    val d = Day12()
    d.part1().println()
    d.part2().println()
}

class Day12 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = input.map { Record.parse(it).countArrangements() }.reduce(Long::plus)
    fun part2() = input.map { Record.parseUnfolded(it).countArrangements() }.reduce(Long::plus)

    data class Record(val condition: String, val brokenGroups: List<Int>) {
        private val cache = mutableMapOf<Triple<Int,Int,Int>, Long>()
        fun countArrangements() = count(0, 0, 0)

        private fun count(spring: Int, broken: Int, group: Int) : Long {
            if (group == brokenGroups.size) return if (condition.lastIndexOf('#') >= spring) 0 else 1
            if (spring == condition.length) return if (broken == brokenGroups[group]) count(spring, 0, group+1) else 0
            return cache.getOrPut(Triple(spring,broken,group)) {
                var res = 0L
                if (".?".contains(condition[spring]) && (broken == 0 || broken == brokenGroups[group])) {
                    res += count(spring+1, 0, group + min(broken, 1))
                }
                if ("#?".contains(condition[spring]) && broken < brokenGroups[group]) {
                    res += count(spring+1, broken+1, group)
                }
                res
            }
        }

        companion object {
            fun parse(input: String): Record {
                val (condition, groups) = input.split(' ')
                return Record(condition, groups.toIntList(','))
            }
            fun parseUnfolded(input: String): Record {
                val (condition, groups) = input.split(' ')
                val unfoldedCondition = List(5) { condition }.joinToString("?")
                val unfoldedGroups = List(5) { groups }.joinToString(",")
                return Record(unfoldedCondition, unfoldedGroups.toIntList(','))
            }
        }
    }
}
