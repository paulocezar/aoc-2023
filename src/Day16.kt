import kotlin.math.max
fun main() {
    val d = Day16()
    d.part1().println()
    d.part2().println()
}

typealias Position = Pair<Int, Int>
class Day16 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = countVisited(Position(0, 0), Direction.Right)
    fun part2() = let {
        var maxEnergized = 0
        for (row in input.indices) {
            for ((column, dir) in listOf(Pair(0, Direction.Right), Pair(input[0].length-1, Direction.Left))) {
                maxEnergized = max(maxEnergized, countVisited(Position(row, column), dir))
            }
        }
        for (column in input[0].indices) {
            for ((row, dir) in listOf(Pair(0, Direction.Down), Pair(input.size-1, Direction.Top))) {
                maxEnergized = max(maxEnergized, countVisited(Position(row, column), dir))
            }
        }
        maxEnergized
    }

    enum class Direction { Right, Left, Top, Down }
    enum class GridCell (val code: Char) {
        Empty('.'),
        RightMirror('/'),
        LeftMirror('\\'),
        HorizontalSplitter('-'),
        VerticalSplitter('|');
        fun next(dir: Direction) = when (this) {
            Empty -> listOf(dir)
            RightMirror -> listOf(when (dir) {
                Direction.Right -> Direction.Top
                Direction.Left -> Direction.Down
                Direction.Top -> Direction.Right
                Direction.Down -> Direction.Left
            })
            LeftMirror -> listOf(when (dir) {
                Direction.Right -> Direction.Down
                Direction.Left -> Direction.Top
                Direction.Top -> Direction.Left
                Direction.Down -> Direction.Right
            })
            HorizontalSplitter -> when (dir) {
                Direction.Right, Direction.Left -> listOf(dir)
                Direction.Top, Direction.Down -> listOf(Direction.Left, Direction.Right)
            }
            VerticalSplitter -> when (dir) {
                Direction.Top, Direction.Down -> listOf(dir)
                Direction.Left, Direction.Right -> listOf(Direction.Top, Direction.Down)
            }
        }
        companion object {
            fun fromValue(code: Char) = entries.first { it.code == code }
        }
    }

    fun Position.move(direction: Direction) = when (direction) {
        Direction.Right -> Position(first, second+1)
        Direction.Left -> Position(first, second-1)
        Direction.Top -> Position(first-1, second)
        Direction.Down -> Position(first+1, second)
    }

    private fun countVisited(initialPosition: Position, initialDirection: Direction) : Int {
        val visited = mutableSetOf<Pair<Position, Direction>>()
        val queue = ArrayDeque<Pair<Position, Direction>>()
        visited.add(Pair(initialPosition, initialDirection))
        queue.addLast(Pair(initialPosition, initialDirection))
        while (!queue.isEmpty()) {
            val (pos, dir) = queue.removeFirst()
            val movesTo = GridCell.fromValue(input[pos.first][pos.second]).next(dir)
            for (nextDir in movesTo) {
                val nextPos = pos.move(nextDir)
                if (!(nextPos.first in input.indices && nextPos.second in input[0].indices)) continue
                if (!visited.add(Pair(nextPos, nextDir))) continue
                queue.addLast(Pair(nextPos, nextDir))
            }
        }
        return visited.map { it.first }.toSet().size
    }
}
