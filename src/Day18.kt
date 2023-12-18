import kotlin.math.abs

fun main() {
    val d = Day18()
    d.part1().println()
}

class Day18 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val regex = """(\w) (\d+) \(#(\w+)\)""".toRegex()
    private val plan = DigPlan(input.map {
        val (dir, dist, color) = regex.matchEntire(it)!!.destructured
        DigInstruction(dir[0], dist.toInt(), color)
    })

    fun part1() = plan.getArea()

    data class DigInstruction(val direction: Char, var distance: Int, val color: String)
    data class DigPlan(val instructions: List<DigInstruction>) {
        fun getArea() : Int {
            var curVertex = Pair(0, 0)
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
            var area = 0
            for (i in 0..vertices.size-2) {
                area += vertices[i].first*vertices[i+1].second - vertices[i+1].first*vertices[i].second
            }
            area = abs(area / 2) + (instructions.sumOf { it.distance } / 2) + 1
            return area
        }
    }
}
