fun main() {
    val d = Day08()
    d.part1().println()
}

class Day08 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val map = DesertMap.parse(input)

    fun part1() = map.stepsBetween("AAA", "ZZZ")

    data class Node(val left: Int, val right: Int)
    data class DesertMap(val route: String, val nodes: List<Node>, private val labelToId: Map<String, Int>) {
        fun stepsBetween(source: String, destination: String) : Int {
            var at = labelToId[source]!!
            val dest = labelToId[destination]!!
            var steps = 0
            while (at != dest) {
                at = when (val dir = route[steps % route.length]) {
                    'L' -> nodes[at].left
                    'R' -> nodes[at].right
                    else -> throw Exception("unexpected $dir on route")
                }
                ++steps
            }
            return steps
        }

        companion object {
            fun parse(input: List<String>): DesertMap {
                val route = input[0]
                val regex = """(\w+) = \((\w+), (\w+)\)""".toRegex()
                val desc = input.slice(2..<input.size).map {
                    val (node, left, right) = regex.matchEntire(it)!!.destructured
                    Triple(node, left, right)
                }
                val labelToId = desc.withIndex().associate { it.value.first to it.index }
                return DesertMap(route, desc.map { Node(labelToId[it.second]!!, labelToId[it.third]!!) }, labelToId)
            }
        }
    }
}
