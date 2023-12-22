import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("inputs/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Turns a string with space separated integers into a list of Ints.
 */
fun String.toIntList(delimiter: Char = ' ') = split(delimiter).map {
    it.trim() }.filterNot { it.isEmpty() }.map { it.toInt() }

/**
 * Turns a string with space separated integers into a list of Longs.
 */
fun String.toLongList(delimiter: Char = ' ') = split(delimiter).map {
    it.trim() }.filterNot { it.isEmpty() }.map { it.toLong() }

fun <T> cartesianProduct(sets: List<List<T>>): Sequence<List<T>> = sequence {
    val lengths = ArrayList<Int>()
    val remaining = ArrayList(listOf(1))

    sets.reversed().forEach {
        lengths.add(0, it.size)
        remaining.add(0, it.size * remaining[0])
    }

    val products = remaining.removeAt(0)

    (0 until products).forEach { product ->
        val result = ArrayList<T>()
        sets.indices.forEach { setIdx ->
            val elementIdx = product / remaining[setIdx] % lengths[setIdx]
            result.add(sets[setIdx][elementIdx])
        }
        yield(result.toList())
    }
}

fun IntRange.size() = if (isEmpty()) 0 else last - start + 1

fun IntRange.merge(other: IntRange) =
        if (isEmpty()) this
        else if (other.isEmpty()) other
        else (maxOf(first, other.first)..minOf(last, other.last))

fun gcd(a: Long, b: Long) : Long {
    var (aa, bb) = listOf(a, b)
    while (bb != 0L) {
        val ax = bb
        bb = aa % bb
        aa = ax
    }
    return aa
}

fun lcm(a: Long, b: Long) : Long = (a / gcd(a, b)) * b
