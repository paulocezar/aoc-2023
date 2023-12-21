fun main() {
    val d = Day20()
    d.part1().println()
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

    enum class Pulse { High, Low }
    data class PulseSent(val type: Pulse, val process: () -> List<PulseSent>)

    sealed class Module {
        abstract val name: String
        protected val inputs = mutableMapOf<String, Pulse>()
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
            override fun receive(pulse: Pulse, fromModule: Module) = let {
                inputs[fromModule.name] = pulse
                send(if (inputs.values.all { it == Pulse.High }) Pulse.Low else Pulse.High)
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
                        val previous = conjunction.inputs.put(module.name, Pulse.Low)
                        if (previous != null)
                            error("${module.name} shows up multiple times as input to ${conjunction.name}")
                    }
                }

                return modules
            }
        }
    }
}
