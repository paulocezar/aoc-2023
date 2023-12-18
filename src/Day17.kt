import java.util.PriorityQueue

fun main() {
    val d = Day17()
    d.part1().println()
}

class Day17 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    data class State(val r: Int, val c: Int, val dir: Int)
    fun part1() : Int {
        val n = input.size
        val m = input[0].length
        val directions = listOf(Pair(0, +1), Pair(+1, 0), Pair(0, -1), Pair(-1, 0))
        val dist = Array(n) { Array(m) { Array(4) { Int.MAX_VALUE } } }

        val pq = PriorityQueue {p1: Pair<State, Int>, p2 : Pair<State, Int> -> p1.second - p2.second}
        pq.add(Pair(State(0, 0, 0), 0))
        pq.add(Pair(State(0, 0, 3), 0))
        while (!pq.isEmpty()) {
            val (cur, cdist) = pq.poll()
            if (cdist > dist[cur.r][cur.c][cur.dir]) continue
            if (cur.r == n-1 && cur.c == m-1) return  cdist
            for (ndir in directions.indices) {
                if (ndir % 2 == cur.dir % 2) continue
                var ndist = cdist
                var nr = cur.r
                var nc = cur.c
                for (moves in 1..3) {
                    nr += directions[ndir].first
                    nc += directions[ndir].second
                    if (nr in input.indices && nc in input[0].indices) {
                        ndist += input[nr][nc].digitToInt()
                        if (ndist < dist[nr][nc][ndir]) {
                            dist[nr][nc][ndir] = ndist
                            pq.add(Pair(State(nr, nc, ndir), ndist))
                        }
                    } else break
                }
            }
        }
        throw Exception("should always be able to find a path")
    }
}
