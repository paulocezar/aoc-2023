fun main() {
    val d = Day07()
    d.part1().println()
}

class Day07 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val hands = input.map { line ->
        val (cards, bid) = line.split(' ')
        Hand(cards, bid.toInt())
    }

    fun part1() = hands.sorted().withIndex().sumOf { (idx, hand) -> (idx+1) * hand.bid }

    enum class HandType { HighCard, OnePair, TwoPair, ThreeOf, FullHouse, FourOf, FiveOf }

    data class Hand(val cards: String, val bid: Int): Comparable<Hand> {
        val type: HandType = when (cards.groupingBy { it }.eachCount().values.sorted()) {
            listOf(5) -> HandType.FiveOf
            listOf(1, 4) -> HandType.FourOf
            listOf(2, 3) -> HandType.FullHouse
            listOf(1, 1, 3) -> HandType.ThreeOf
            listOf(1, 2, 2) -> HandType.TwoPair
            listOf(1, 1, 1, 2) -> HandType.OnePair
            else -> HandType.HighCard
        }
        companion object {
            private val cardValue = mapOf('T' to 10, 'J' to 11, 'Q' to 12, 'K' to 13, 'A' to 14)
            private fun valueOf(card: Char): Int = if (card.isDigit()) card.digitToInt() else cardValue[card]!!
        }

        override fun compareTo(other: Hand): Int = when {
            type != other.type -> type compareTo other.type
            else -> when (val diffIdx = cards.indices.find { idx -> cards[idx] != other.cards[idx] }) {
                null -> 0
                else -> valueOf(cards[diffIdx]) compareTo valueOf(other.cards[diffIdx])
            }
        }
    }
}
