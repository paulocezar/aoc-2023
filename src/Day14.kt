fun main() {
    val d = Day14()
    d.part1().println()
}

class Day14 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = calculateLoad(tiltNorth())

    private fun calculateLoad(platform: List<String>) = platform.foldIndexed(0) { rowIdx, load, row ->
        load + row.fold(0) { rowLoad, cell -> rowLoad + if (cell == 'O') platform.size-rowIdx else 0 }
    }

    private fun tiltNorth() = buildList<String>(input.size) {
        val nextRock = mutableListOf<Int>()
        input[0].indices.forEach { column -> nextRock.add(input.indexOfFirst { "O#".contains(it[column]) }) }
        input.forEach { row ->
            add(buildString(row.length) {
                row.forEachIndexed { col, chr ->
                    if (nextRock[col] >= 0) {
                        val rockRow = nextRock[col]
                        if (input[rockRow][col] == '#' && chr != '#') append('.')
                        else {
                            append(if (chr == '#') '#' else 'O')
                            nextRock[col] = input.subList(rockRow+1, input.size).indexOfFirst { "O#".contains(it[col]) }
                            nextRock[col] += if (nextRock[col] >= 0) rockRow+1 else 0
                        }
                    } else append('.')
                }
            })
        }
    }
}
