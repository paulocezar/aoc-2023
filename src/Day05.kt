fun main() {
    val d = Day05()
    d.part1().println()
}

class Day05 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val almanac: Almanac = parseAlmanac()

    fun part1() = almanac.seeds.map { almanac.seedToLocation(it) }.min()


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
        data class Map(private val ranges: List<Triple<Long,Long,Long>>) {
            fun get(key: Long) : Long {
                for ((destinationStart, sourceStart, length) in ranges) {
                    val delta = key - sourceStart
                    if (delta in 0..<length)
                        return destinationStart + delta
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
                    add(Triple(destinationStart, sourceStart, length))
                }
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
