fun main() {
    val d = Day15()
    d.part1().println()
    d.part2().println()
}

class Day15 {
    private val input: List<String> = readInput(this::class.simpleName!!)

    fun part1() = input[0].split(',').map { hash(it) }.reduce(Int::plus)

    fun part2() : Int {
        val boxes = mutableMapOf<Int, MutableList<Pair<String, Int>>>()
        input[0].split(',').forEach { op ->
            val sep = op.indexOfAny(charArrayOf('=', '-'))
            val label = op.substring(0, sep)
            val box = hash(label)
            when (op[sep]) {
                '-' -> boxes.getOrPut(box) { mutableListOf() }.removeIf { it.first == label }
                '=' -> {
                    val focalLength = op.substring(sep+1).toInt()
                    val lenses = boxes.getOrPut(box) { mutableListOf() }
                    val existing = lenses.indexOfFirst { it.first == label }
                    if (existing >= 0) lenses.set(existing, Pair(label, focalLength))
                    else lenses.add(Pair(label, focalLength))
                }
            }
        }
        var focusingPower = 0
        boxes.forEach { (box, lenses) ->
            lenses.forEachIndexed { index, (_, focalLength) ->
                focusingPower += (box+1) * (index+1) * focalLength
            }
        }
        return focusingPower
    }

    private fun hash(step: String) = step.fold(0) { currentValue, char ->
        ((currentValue + char.code) * 17) % 256
    }
}
