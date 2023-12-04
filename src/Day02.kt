fun main() {
    val d = Day02()
    d.part1().println()
}

class Day02 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    data class Game(val id: Int, val revealed: List<RevealedCubes>) {
        data class RevealedCubes(val red: Int, val green: Int, val blue: Int) {
            fun respectAvailability(): Boolean = red <= 12 && green <= 13 && blue <= 14
        }
        fun isValid(): Boolean = revealed.all { it.respectAvailability() }
    }

    fun part1(): Int = parseGames().filter { it.isValid() }.sumOf { it.id }

    private fun parseGames(): List<Game> = input.map {
        val (gameId, sets) = it.split(':')
        val revealed: List<Game.RevealedCubes> = sets.split(';').map { set ->
            val colorCounts = set.split(',').associate { colorCount ->
                val (count, color) = colorCount.trim().split(' ')
                color to count.toInt()
            }
            Game.RevealedCubes(
                    colorCounts.getOrDefault("red",0),
                    colorCounts.getOrDefault("green",0),
                    colorCounts.getOrDefault("blue",0))
        }
        val (_, id) = gameId.split(' ')
        Game(id.toInt(), revealed)
    }
}
