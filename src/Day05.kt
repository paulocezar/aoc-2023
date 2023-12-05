fun main() {
    val d = Day05()
    d.part1().println()
}

class Day05 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val almanac: Almanac = parseAlmanac()

    fun part1() = almanac.seeds.minOf { almanac.seedToLocation(it) }

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
        data class Map(private val ranges: List<MapRange>) {
            data class MapRange(val destinationStart: Long, val sourceStart: Long, val length: Long) {
                val sourceEnd: Long
                    get() = sourceStart + length
                fun map(value: Long) = destinationStart + (value - sourceStart)
            }

            fun get(key: Long) : Long {
                var lo = 0
                var hi = ranges.size - 1
                while (lo <= hi) {
                    val mid = (lo + hi) / 2
                    if (key < ranges[mid].sourceStart) hi = mid - 1
                    else if (key >= ranges[mid].sourceEnd) lo = mid + 1
                    else return ranges[mid].map(key)
                }
                return key
            }
        }

        fun seedToLocation(seed: Long) = listOf(
                seed_to_soil,
                soil_to_fertilizer,
                fertilizer_to_water,
                water_to_light,
                light_to_temperature,
                temperature_to_humidity,
                humidity_to_location
            ).fold(seed) { value, map -> map.get(value) }
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
