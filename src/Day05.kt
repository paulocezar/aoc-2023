import kotlin.math.max
import kotlin.math.min
fun main() {
    val d = Day05()
    d.part1().println()
    d.part2().println()
}

class Day05 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val almanac: Almanac = parseAlmanac()

    fun part1() = almanac.seeds.minOf { almanac.seedToLocation(it) }

    fun part2() : Long {
        val seedRanges = almanac.seeds.chunked(2).map { (start, length) -> Almanac.Range(start,length) }
                .sortedBy { it.start }
        val locationRanges = almanac.seedsToLocations(seedRanges)
        return locationRanges.first().start
    }

    data class Almanac(
            val seeds: List<Long>,
            private val seed_to_soil: Map,
            private val soil_to_fertilizer: Map,
            private val fertilizer_to_water: Map,
            private val water_to_light: Map,
            private val light_to_temperature: Map,
            private val temperature_to_humidity: Map,
            private val humidity_to_location: Map)
    {
        private val seed_to_location = listOf(
                seed_to_soil,
                soil_to_fertilizer,
                fertilizer_to_water,
                water_to_light,
                light_to_temperature,
                temperature_to_humidity,
                humidity_to_location
        )

        data class Range(val start: Long, val length: Long) {
            val end: Long
                get() = start + length - 1
        }

        data class Map(private val ranges: List<MapRange>) {
            data class MapRange(val destinationStart: Long, val sourceStart: Long, val length: Long) {
                val sourceEnd: Long
                    get() = sourceStart + length - 1
                fun map(value: Long) = destinationStart + (value - sourceStart)
                fun map(range: Range) : Triple<Range?, Range, Range?> {
                    val first = max(range.start, sourceStart)
                    val last = min(range.end, sourceEnd)
                    val before = if (first < range.start) Range(range.start, sourceStart-first) else null
                    val mapped = Range(map(first), last-first+1)
                    val after = if (last < range.end) Range(last+1, range.end-last) else null
                    return Triple(before, mapped, after)
                }
                fun hasMappingFor(value: Long) : Boolean = value in sourceStart..sourceEnd
                fun hasMappingFor(range: Range) : Boolean = hasMappingFor(range.start) || hasMappingFor(range.end)
            }

            fun get(key: Long) : Long {
                var lo = 0
                var hi = ranges.size - 1
                while (lo <= hi) {
                    val mid = (lo + hi) / 2
                    if (key < ranges[mid].sourceStart) hi = mid - 1
                    else if (key > ranges[mid].sourceEnd) lo = mid + 1
                    else return ranges[mid].map(key)
                }
                return key
            }

            fun get(keyRanges: List<Range>): List<Range> {
                var idx = 0
                return buildList {
                    for (range in keyRanges) {
                        while (idx < ranges.size && ranges[idx].sourceEnd < range.start) ++idx

                        if (idx == ranges.size) add(range)
                        else {
                            var curRange: Range? = range
                            while (curRange != null && idx < ranges.size && ranges[idx].hasMappingFor(curRange))
                            {
                                val (before, mapped, after) = ranges[idx].map(curRange)
                                if (before != null) add(before)
                                add(mapped)
                                if (after != null) ++idx
                                curRange = after
                            }
                            if (curRange != null) add(curRange)
                        }
                    }
                    sortBy { it.start }
                }
            }
        }

        fun seedToLocation(seed: Long) = seed_to_location
                .fold(seed) { value, map -> map.get(value) }

        fun seedsToLocations(seeds: List<Range>) = seed_to_location
                .fold(seeds) { value, map -> map.get(value) }


    }

    private fun parseAlmanac(): Almanac {
        val iterator = input.listIterator()
        val skip = {
            expected: String ->
            val found = iterator.next()
            if (expected != found) throw Exception("expected $expected found $found")
        }
        val parseMap = {
            mapName: String ->
            skip("$mapName map:")
            Almanac.Map(buildList {
                while (iterator.hasNext()) {
                    val range = iterator.next()
                    if (range.isEmpty()) break
                    val (destinationStart, sourceStart, length) = range.toLongList()
                    add(Almanac.Map.MapRange(destinationStart, sourceStart, length))
                }
                sortBy { it.sourceStart }
            })
        }
        val (_, seeds) = iterator.next().split(':')
        skip("")
        return Almanac(
                seeds.toLongList(),
                parseMap("seed-to-soil"),
                parseMap("soil-to-fertilizer"),
                parseMap("fertilizer-to-water"),
                parseMap("water-to-light"),
                parseMap("light-to-temperature"),
                parseMap("temperature-to-humidity"),
                parseMap("humidity-to-location")
        )
    }
}
