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
fun String.toIntList() = split(' ').map { it.trim() }.filterNot { it.isEmpty() }.map { it.toInt() }

/**
 * Turns a string with space separated integers into a list of Longs.
 */
fun String.toLongList() = split(' ').map { it.trim() }.filterNot { it.isEmpty() }.map { it.toLong() }

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
