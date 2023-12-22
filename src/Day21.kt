fun main() {
    val d = Day21()
    d.part1().println()
}

class Day21 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val distances = let {
        val dist = Array(input.size) { Array(input.first().length) { Int.MAX_VALUE } }
        val start = let {
            if (input.map { it.count { c -> c == 'S' } }.reduce(Int::plus) != 1) error("multiple starting points")
            val r = input.indexOfFirst { it.contains('S') }
            val c = input[r].indexOf('S')
            Pair(r, c)
        }
        val queue = ArrayDeque<Pair<Int, Int>>()
        dist[start.first][start.second] = 0
        queue.add(start)
        while (!queue.isEmpty()) {
            val cur = queue.removeFirst()
            val nextDist = dist[cur.first][cur.second] + 1
            for ((dx, dy) in listOf(Pair(-1, 0), Pair(0, -1), Pair(+1, 0), Pair(0, +1))) {
                val next = Pair(cur.first + dx, cur.second + dy)
                if (next.first !in input.indices
                        || next.second !in input.first().indices
                        || input[next.first][next.second] == '#') continue
                if (nextDist < dist[next.first][next.second]) {
                    dist[next.first][next.second] = nextDist
                    queue.add(next)
                }
            }
        }
        dist.map { it.toList() }
    }

    fun part1() = distances.map { it.count { dist -> dist <= 64 && dist % 2 == 0 }  }.reduce(Int::plus)
}
