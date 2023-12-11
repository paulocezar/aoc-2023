import kotlin.math.abs
fun main() {
    val d = Day11()
    d.part1().println()
    d.part2().println()
}

class Day11 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = sumPairwiseDistances(getExpandedGalaxyPositions())
    fun part2() = sumPairwiseDistances(getExpandedGalaxyPositions(1_000_000))

    private fun sumPairwiseDistances(galaxyPositions: List<Pair<Int,Int>>) = galaxyPositions.foldIndexed(0L)
        { index, sumDistances, galaxy1 ->
            sumDistances + galaxyPositions.subList(index+1, galaxyPositions.size).fold(0L)
                { g1Distances, galaxy2 -> g1Distances + distance(galaxy1, galaxy2) }
        }

    private fun distance(g1: Pair<Int, Int>, g2: Pair<Int,Int>) =
            abs(g1.first - g2.first) + abs(g1.second - g2.second)

    private fun getExpandedGalaxyPositions(expansionFactor: Int = 2) : List<Pair<Int,Int>> = buildList {
        val emptyColumns = input[0].indices.filter { column ->
            input.all { it[column] == '.' }
        }
        var actualRow = 0
        input.forEach { row ->
            var isEmpty = true
            var actualColumn = 0
            row.forEachIndexed { index, cell ->
                if (cell == '#') {
                    isEmpty = false
                    add(Pair(actualRow, actualColumn))
                }
                actualColumn += if (emptyColumns.binarySearch(index) >= 0) expansionFactor else 1
            }
            actualRow += if (isEmpty) expansionFactor else 1
        }
    }
}
