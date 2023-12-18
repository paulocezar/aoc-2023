import kotlin.math.abs

fun main() {
    val d = Day18()
    d.part1().println()
    d.part2().println()
}

class Day18 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val regex = """(\w) (\d+) \(#(\w+)\)""".toRegex()
    private val plan = DigPlan(input.map {
        val (dir, dist, _) = regex.matchEntire(it)!!.destructured
        DigInstruction(dir[0], dist.toInt())
    })
    private val actualPlan = DigPlan(input.map {
        val (_, _, hex) = regex.matchEntire(it)!!.destructured
        val dir = when (hex.last()) {
            '0' -> 'R'
            '1' -> 'D'
            '2' -> 'L'
            '3' -> 'U'
            else -> throw Exception("unexpected digit ${hex.last()}")
        }
        DigInstruction(dir, hex.dropLast(1).toInt(16))
    })

    fun part1() = plan.getArea()
    fun part2() = actualPlan.getArea()

    data class DigInstruction(val direction: Char, var distance: Int)
    data class DigPlan(val instructions: List<DigInstruction>) {
        fun getArea() : Long {
            var curVertex = Pair(0L, 0L)
            val vertices = instructions.map {
                val newVertex = when (it.direction) {
                    'U' -> Pair(curVertex.first-it.distance, curVertex.second)
                    'D' -> Pair(curVertex.first+it.distance, curVertex.second)
                    'L' -> Pair(curVertex.first, curVertex.second-it.distance)
                    'R' -> Pair(curVertex.first, curVertex.second+it.distance)
                    else -> throw Exception("unexpected direction ${it.direction}")
                }
                curVertex = newVertex
                newVertex
            }.dropLast(1)
            var area = 0L
            for (i in 0..vertices.size-2) {
                area += vertices[i].first*vertices[i+1].second - vertices[i+1].first*vertices[i].second
            }
            area = abs(area / 2) + (instructions.sumOf { it.distance } / 2) + 1
            return area
        }
    }
}
