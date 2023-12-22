import kotlin.math.min
import kotlin.math.max
fun main() {
    val d = Day08()
    d.part1().println()
    d.part2().println()
}

class Day08 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val map = DesertMap.parse(input)

    fun part1() = map.stepsBetween("AAA", "ZZZ")
    fun part2() = map.ghostRoute('A', 'Z')

    data class Node(val label: String, val left: Int, val right: Int)
    data class DesertMap(val route: String, val nodes: List<Node>, private val labelToId: Map<String, Int>) {
        private fun getNext(curId: Int, atStep: Int) : Int = when (val dir = route[atStep % route.length]) {
            'L' -> nodes[curId].left
            'R' -> nodes[curId].right
            else -> throw Exception("unexpected $dir on route")
        }

        fun stepsBetween(source: String, destination: String) : Int {
            var at = labelToId[source]!!
            val dest = labelToId[destination]!!
            var steps = 0
            while (at != dest) {
                at = getNext(at, steps)
                ++steps
            }
            return steps
        }

        fun ghostRoute(sourceEnding: Char, destinationEnding: Char) : Long {
            val terminalNodes = labelToId.keys.filter { it.endsWith(destinationEnding) }.map { labelToId[it]!! }.sorted()
            val getCongruences = { label: String ->
                var cycleStart: Int
                var cycleLen: Int
                val visitedTimes = buildMap {
                    var at = labelToId[label]!!
                    var steps = 0
                    do {
                        set(Pair(at, steps % route.length), steps + 1)
                        at = getNext(at, steps)
                        ++steps
                    } while (!containsKey(Pair(at, steps % route.length)))

                    cycleStart = get(Pair(at, steps % route.length))!!
                    cycleLen = steps + 1 - cycleStart
                }
                buildList {
                    visitedTimes.entries.forEach { (state, seenAt) ->
                        if (terminalNodes.binarySearch(state.first) >= 0) {
                            add(Pair(seenAt, if (seenAt < cycleStart) 0 else cycleLen))
                        }
                    }
                }
            }
            val congruences = labelToId.keys.filter { it.endsWith(sourceEnding) }.map {
                getCongruences(it)
            }

            if (congruences.any { it.isEmpty() })
                throw Exception("no solution: one of the ghosts never reaches a terminal node")

            val solution = cartesianProduct(congruences).fold(Long.MAX_VALUE) { acc, candidate ->
                val fixed = candidate.find{ it.second == 0 }
                val candidateAns: Long? = if (fixed == null) {
                    crt(candidate)
                } else {
                    val fixedT = fixed.first
                    val isValid = candidate.all { (seenAt, cycleLen) ->
                        (cycleLen == 0 && seenAt == fixedT) || (fixedT >= seenAt && ((fixedT % cycleLen) == (seenAt % cycleLen)))
                    }
                    if (isValid) fixedT.toLong() else null
                }
                min(acc, candidateAns ?: acc)
            }
            if (solution == Long.MAX_VALUE)
                throw Exception("no solution: can't place all ghosts on terminal nodes at the same time")
            return solution - 1
        }

        private fun normalize(x: Long, mod: Long) = ((x % mod) + mod) % mod

        private fun extendedGcd(a: Long, b: Long) : Triple<Long,Long,Long> {
            if (b == 0L) return Triple(1L, 0L, a);
            val pom = extendedGcd(b, a % b);
            return Triple(pom.second, pom.first - a / b * pom.second, pom.third)
        }

        private fun crt(congruences: List<Pair<Int,Int>>) : Long? {
            var minAns = congruences[0].first.toLong()
            var x = (congruences[0].first % congruences[0].second).toLong()
            var lcm = congruences[0].second.toLong()
            congruences.slice(1..<congruences.size).forEach {
                minAns = max(minAns, it.first.toLong())
                val na = normalize(it.first.toLong(), it.second.toLong())
                val n = it.second.toLong()
                val (x1, _, d) = extendedGcd(lcm, n)
                if ((na - x) % d != 0L) return null
                x = normalize(x + x1 * (na - x) / d % (n / d) * lcm, lcm * n / d)
                lcm = lcm(lcm, n)
            }
            while (x < minAns) x += lcm
            return x
        }

        fun naiveGhostRoute(sourceEnding: Char, destinationEnding: Char) : Int {
            val atNodes = labelToId.keys.filter { it.endsWith(sourceEnding) }.map { labelToId[it]!! }.sorted().toMutableList()
            var steps = 0
            val seen = mutableMapOf<Pair<List<Int>, Int>, Int>()
            seen[Pair(atNodes, 0)] = 0
            while (atNodes.any { !nodes[it].label.endsWith(destinationEnding) }) {
                atNodes.replaceAll { getNext(it, steps) }
                atNodes.sort()
                ++steps
                val state = Pair(atNodes, steps % route.length)
                val bef = seen.get(state)
                if (bef != null) {
                    throw Exception("no solution: non-terminal loop detected $bef ... $steps")
                }
                seen[state] = steps
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
                return DesertMap(route, desc.map { Node(it.first, labelToId[it.second]!!, labelToId[it.third]!!) }, labelToId)
            }
        }
    }
}
