fun main() {
    val d = Day06()
    d.part1().println()
    d.part2().println()
}

class Day06 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val smallRaces: List<Race> = buildList {
        val times = input[0].split(':')[1].toIntList()
        val distances = input[1].split(':')[1].toLongList()
        times.zip(distances) { time, distance -> add(Race(time, distance))}
    }
    private val fixKerning = { str: String -> str.filter { c -> c.isDigit() } }
    private val bigRace = Race(fixKerning(input[0]).toInt(), fixKerning(input[1]).toLong())

    fun part1() = smallRaces.fold(1) { totalWays, race -> totalWays * race.waysToWin() }
    fun part2() = bigRace.waysToWin()

    data class Race(val time: Int, val distance: Long) {
        fun waysToWin(): Int = (1..<time).count { charge -> (time-charge) > distance/charge }
    }
}
