import kotlin.math.abs

fun main() {
    val d = Day03()
    d.part1().println()
}

class Day03 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    val schematic: Schematic = parseSchematic()

    fun part1(): Int = schematic.getPartNumbers().sum()

    data class Schematic(val numbers: List<Number>, val symbols: List<Symbol>) {
        data class Number(val number: Int, val row: Int, val column: Int, val digits: Int)
        data class Symbol(val symbol: Char, var row: Int, val column: Int)

        fun getPartNumbers() : List<Int> = numbers.filter {
            number -> symbols.any { symbol -> isAdjacent(number, symbol) }
        }.map { it.number}

        private fun isAdjacent(number: Number, symbol: Symbol): Boolean {
            if (abs(number.row - symbol.row) > 1) return false
            for (col in number.column..<number.column+number.digits) {
                if (abs(col-symbol.column) <= 1) return true
            }
            return false
        }
    }

    private fun parseSchematic(): Schematic {
        var currentNumber: Int = 0
        var digits: Int = 0
        val numbers: MutableList<Schematic.Number> = mutableListOf()
        val symbols: MutableList<Schematic.Symbol> = mutableListOf()
        input.forEachIndexed { row, s ->
            s.forEachIndexed { col, char ->
                if (char.isDigit()) {
                    currentNumber = currentNumber * 10 + char.digitToInt()
                    digits += 1
                } else {
                    if (digits > 0) {
                        numbers.add(Schematic.Number(currentNumber, row, col - digits, digits))
                        currentNumber = 0
                        digits = 0
                    }
                    if (char != '.') symbols.add(Schematic.Symbol(char, row, col))
                }
            }
            if (digits > 0) numbers.add(Schematic.Number(currentNumber, row, s.length-digits, digits))
        }
        return Schematic(numbers, symbols)
    }
}
