fun main() {
    val d = Day14()
    d.part1().println()
    d.part2().println()
}

class Day14 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = Platform(input).tiltNorth().calculateLoad()

    fun part2() : Int {
        val platform = Platform(input)
        val cache = mutableMapOf<List<String>, Int>()
        cache[platform.state] = 0
        var step = 1
        var cyclesNeeded = 1_000_000_000
        while (cyclesNeeded > 0) {
            platform.cycle()
            cyclesNeeded -= 1
            if (cache.containsKey(platform.state)) {
                val prev = cache[platform.state]!!
                val cycleLen = step - prev
                cyclesNeeded %= cycleLen
            } else cache[platform.state] = step
            step += 1
        }
        return platform.calculateLoad()
    }

    data class Platform(var state: List<String>) {
        fun cycle() = tiltNorth()
                .rotate().tiltNorth()
                .rotate().tiltNorth()
                .rotate().tiltNorth()
                .rotate()

        fun calculateLoad() = state.foldIndexed(0) { rowIdx, load, row ->
            load + row.fold(0) { rowLoad, cell -> rowLoad + if (cell == 'O') state.size-rowIdx else 0 }
        }

        fun tiltNorth() : Platform {
            state = buildList(state.size) {
                val nextRock = mutableListOf<Int>()
                state[0].indices.forEach { column -> nextRock.add(state.indexOfFirst { "O#".contains(it[column]) }) }
                state.forEach { row ->
                    add(buildString(row.length) {
                        row.forEachIndexed { col, chr ->
                            if (nextRock[col] >= 0) {
                                val rockRow = nextRock[col]
                                if (state[rockRow][col] == '#' && chr != '#') append('.')
                                else {
                                    append(if (chr == '#') '#' else 'O')
                                    nextRock[col] = state.subList(rockRow + 1, state.size)
                                            .indexOfFirst { "O#".contains(it[col]) }
                                    nextRock[col] += if (nextRock[col] >= 0) rockRow + 1 else 0
                                }
                            } else append('.')
                        }
                    })
                }
            }
            return this
        }

        private fun rotate() : Platform {
            state = buildList(state[0].length) {
                state[0].indices.forEach { column ->
                    add(buildString(state.size) {
                        state.asReversed().forEach { row -> append(row[column]) }
                    })
                }
            }
            return this
        }
    }
}
