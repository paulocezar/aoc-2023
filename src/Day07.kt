fun main() {
    val d = Day07()
    d.part1().println()
    d.part2().println()
}

class Day07 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val hands = input.map { line ->
        val (cards, bid) = line.split(' ')
        Hand(cards, bid.toInt())
    }

    fun part1() = hands.sorted().withIndex().sumOf { (idx, hand) -> (idx+1) * hand.bid }

    fun part2() = hands.sortedWith(Hand::jokerComparator).withIndex().sumOf { (idx, hand) -> (idx+1) * hand.bid }

    enum class HandType { HighCard, OnePair, TwoPair, ThreeOf, FullHouse, FourOf, FiveOf }

    data class Hand(val cards: String, val bid: Int): Comparable<Hand> {
        val type: HandType = typeOf(cards)
        val strongestType: HandType = "23456789TQKA".maxOf { typeOf(cards.replace('J', it)) }
        companion object {
            private val cardValue = mapOf('T' to 10, 'J' to 11, 'Q' to 12, 'K' to 13, 'A' to 14)
            private fun valueOf(card: Char, jokerIsWeakest: Boolean = false): Int = when {
                jokerIsWeakest && card == 'J' -> 0
                card.isDigit() -> card.digitToInt()
                else -> cardValue[card]!!
            }

            private fun typeOf(cards: String) = when (cards.groupingBy { it }.eachCount().values.sorted()) {
                listOf(5) -> HandType.FiveOf
                listOf(1, 4) -> HandType.FourOf
                listOf(2, 3) -> HandType.FullHouse
                listOf(1, 1, 3) -> HandType.ThreeOf
                listOf(1, 2, 2) -> HandType.TwoPair
                listOf(1, 1, 1, 2) -> HandType.OnePair
                else -> HandType.HighCard
            }

            fun jokerComparator(h1: Hand, h2: Hand): Int = when {
                h1.strongestType != h2.strongestType -> h1.strongestType compareTo h2.strongestType
                else -> when (val diffIdx = h1.cards.indices.find { idx -> h1.cards[idx] != h2.cards[idx] }) {
                    null -> 0
                    else -> valueOf(h1.cards[diffIdx], true) compareTo valueOf(h2.cards[diffIdx], true)
                }
            }
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
