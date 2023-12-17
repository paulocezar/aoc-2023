fun main() {
    val d = Day16()
    d.part1().println()
}

typealias Position = Pair<Int, Int>
class Day16 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = let {
        visit(Position(0, 0), Direction.Right)
        visited.map { it.first }.toSet().size
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

    private val visited = mutableSetOf<Pair<Position, Direction>>()
    private fun visit(position: Position, direction: Direction) {
        if (!(position.first in input.indices && position.second in input[0].indices)) return
        if (!visited.add(Pair(position, direction))) return
        val movesTo = GridCell.fromValue(input[position.first][position.second]).next(direction)
        for (dir in movesTo) {
            visit(position.move(dir), dir)
        }
    }
}
