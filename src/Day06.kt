fun main() {
    val d = Day06()
    d.part1().println()
}

class Day06 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val races: List<Race> = buildList {
        val times = input[0].split(':')[1].toIntList()
        val distances = input[1].split(':')[1].toIntList()
        times.zip(distances) { time, distance -> add(Race(time, distance))}
    }

    fun part1() = races.fold(1) { ways, race -> ways * race.waysToWin() }

    data class Race(val time: Int, val distance: Int) {
        fun waysToWin(): Int = (0..time).count { charge -> (time-charge)*charge > distance }
    }
}
