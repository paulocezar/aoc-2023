fun main() {
    val d = Day10()
    d.part1().println()
    d.part2().println()
}

class Day10 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val loop = Loop.parse(input)

    fun part1() = loop.farthestPipe
    fun part2() = input.mapIndexed { r, row ->
        var isInside = 0
        var cellsInside = 0
        var ending: Char? = null
        row.forEachIndexed { c, cell ->
            if (loop.contains(r, c)) {
                when (cell) {
                    '|' -> isInside = 1 - isInside
                    'L' -> ending = 'J'
                    'F' -> ending = '7'
                    '7', 'J' -> {
                        if (cell != ending!!) isInside = 1-isInside
                        ending = null
                    }
                }
            } else cellsInside += isInside
        }
        cellsInside
    }.reduce(Int::plus)

    data class Loop(
            private val idOf: (Pair<Int,Int>) -> Int,
            private val start: Int,
            private val pipes: Map<Int, Pair<Int, Int>>) {
        val farthestPipe = pipes.size / 2
        fun contains(r: Int, c: Int) = pipes.contains(idOf(Pair(r, c)))

        companion object {
            fun parse(input: List<String>): Loop {
                val columns = input[0].length
                val idOf = { p: Pair<Int,Int> -> p.first*columns + p.second }
                val r = input.indexOfFirst { it.contains('S') }
                val c = input[r].indexOf('S')
                var curPos : Pair<Int,Int>? = Pair(r, c)
                val pipes = buildMap {
                    do {
                        val (n1, n2) = neighborDeltas(input[curPos!!.first][curPos!!.second]).map {
                            Pair(curPos!!.first + it.first, curPos!!.second + it.second) }
                        put(idOf(curPos!!), Pair(idOf(n1), idOf(n2)))
                        curPos = when {
                            !containsKey(idOf(n1)) -> n1
                            !containsKey(idOf(n2)) -> n2
                            else -> null
                        }
                    } while (curPos != null)
                }
                return Loop(idOf, idOf(Pair(r, c)), pipes)
            }

            private fun neighborDeltas(cell: Char) = when (cell) {
                '|' -> listOf(Pair(-1, 0), Pair(+1, 0))
                'L' -> listOf(Pair(-1, 0), Pair(0, +1))
                'J' -> listOf(Pair(-1, 0), Pair(0, -1))
                // v input specific - too lazy to programmatically find out the type of pipe that initial cell should be
                '-', 'S' -> listOf(Pair(0, -1), Pair(0, +1))
                '7' -> listOf(Pair(0, -1), Pair(+1, 0))
                'F' -> listOf(Pair(0, +1), Pair(+1, 0))
                else -> throw Exception("unexpected cell $cell")
            }
        }
    }
}
