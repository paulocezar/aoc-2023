fun main() {
    val d = Day20()
    d.part1().println()
    d.part2().println()
}

class Day20 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val modules = Module.parseConfiguration(input)

    fun part1() = let {
        val broadcaster = modules.filterIsInstance<Module.Broadcaster>().single()
        val buttonPush = PulseSent(Pulse.Low) { broadcaster.receive(Pulse.Low, broadcaster) }
        val pendingPulses = ArrayDeque<PulseSent>()
        val pulseCounters = mutableMapOf<Pulse, Int>()
        repeat(1_000) {
            pendingPulses.add(buttonPush)
            while (!pendingPulses.isEmpty()) {
                val (pulse, process) = pendingPulses.removeFirst()
                pulseCounters.merge(pulse, 1, Int::plus)
                pendingPulses.addAll(process())
            }
        }
        pulseCounters.getOrDefault(Pulse.Low, 0) * pulseCounters.getOrDefault(Pulse.High, 0)
    }

    fun part2() = let {
        val broadcaster = modules.filterIsInstance<Module.Broadcaster>().single()
        val rx = modules.single { it.name == "rx" }
        val rxChecker = modules.single { it.destinations.contains(rx) }
        if (rxChecker !is Module.Conjunction || rxChecker.inputs.size != broadcaster.destinations.size)
            error("broadcaster not incrementing the same number of counters used to activate rx")
        broadcaster.destinations.map { initialFF ->
            if (initialFF !is Module.FlipFlop) error("expected broadcaster to be connected only to counters")
            if (initialFF.destinations.size > 2) error("invalid counter: too many outputs for least significant bit")
            val counterReset = initialFF.destinations.filterIsInstance<Module.Conjunction>().single()
            if (!rxChecker.inputs.contains(counterReset.destinations.filterIsInstance<Module.Conjunction>().single()))
                error("counter is not feeding rx checker")
            if (!counterReset.destinations.contains(initialFF)) error("invalid counter: not properly reset")
            var ff = initialFF.destinations.singleOrNull { it is Module.FlipFlop }
            var pw = 1L
            var countTo = 1L
            while (ff != null) {
                if (ff.destinations.size !in 1..2) error("invalid counter: flip-flop has extra outputs")
                if (pw > Long.MAX_VALUE / 2) error("counter is too large")
                pw *= 2
                if (ff.destinations.contains(counterReset)) {
                    countTo += pw
                    if (counterReset.destinations.contains(ff)) error("invalid counter: not reset to zero")
                } else if (!counterReset.destinations.contains(ff)) error("invalid counter: not properly reset")

                ff = ff.destinations.singleOrNull { it is Module.FlipFlop }
            }
            countTo
        }.reduce(::lcm)
    }

    enum class Pulse { High, Low }
    data class PulseSent(val type: Pulse, val process: () -> List<PulseSent>)

    sealed class Module {
        abstract val name: String
        protected val _inputs = mutableMapOf<Module, Pulse>()
        private val _destinations = mutableListOf<Module>()
        val destinations: List<Module>
            get() = _destinations

        abstract fun receive(pulse: Pulse, fromModule: Module) : List<PulseSent>

        protected fun send(pulse: Pulse) = destinations.map { PulseSent(pulse) { it.receive(pulse, this) } }

        data class FlipFlop(override val name: String) : Module() {
            private var isOn = false
            override fun receive(pulse: Pulse, fromModule: Module) = if (pulse == Pulse.Low) {
                isOn = !isOn
                send(if (isOn) Pulse.High else Pulse.Low)
            } else listOf()
        }

        data class Conjunction(override val name: String) : Module() {
            val inputs : Collection<Module>
                get() = _inputs.keys
            override fun receive(pulse: Pulse, fromModule: Module) = let {
                _inputs[fromModule] = pulse
                send(if (_inputs.values.all { it == Pulse.High }) Pulse.Low else Pulse.High)
            }
        }

        class Broadcaster : Module() {
            override val name: String = NAME
            override fun receive(pulse: Pulse, fromModule: Module) = send(pulse)

            companion object {
                const val NAME: String = "broadcaster"
            }
        }

        data class Output(override val name: String) : Module() {
            override fun receive(pulse: Pulse, fromModule: Module) = listOf<PulseSent>()
        }

        companion object {
            private const val DELIMITER = " -> "
            fun parseConfiguration(config: List<String>) : List<Module> {
                val modulesAndDestinationsByName = config.associate {
                    val (name, destinations) = it.split(DELIMITER)
                    val module = when {
                        name.first() == '%' -> FlipFlop(name.drop(1))
                        name.first() == '&' -> Conjunction(name.drop(1))
                        name == Broadcaster.NAME -> Broadcaster()
                        else -> error("unexpected name: '$name'")
                    }
                    module.name to Pair(module, destinations)
                }

                val outputModulesByName = mutableMapOf<String, Module>()

                modulesAndDestinationsByName.values.forEach { (module, destinations) ->
                    module._destinations.addAll(destinations.split(',').map { rawDestinationName ->
                        val destinationName = rawDestinationName.trim()
                        if (!modulesAndDestinationsByName.containsKey(destinationName))
                            outputModulesByName.putIfAbsent(destinationName, Output(destinationName))
                        modulesAndDestinationsByName[destinationName]?.first ?: outputModulesByName[destinationName]!!
                    })
                }

                val modules = modulesAndDestinationsByName.values.map { it.first } + outputModulesByName.values
                modules.forEach { module ->
                    module.destinations.filter { it is Conjunction }.forEach { conjunction ->
                        val previous = conjunction._inputs.put(module, Pulse.Low)
                        if (previous != null)
                            error("${module.name} shows up multiple times as input to ${conjunction.name}")
                    }
                }

                return modules
            }
        }
    }
}
