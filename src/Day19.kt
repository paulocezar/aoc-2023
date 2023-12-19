fun main() {
    val d = Day19()
    d.part1().println()
}

class Day19 {
    private val input: List<String> = readInput(this::class.simpleName!!)
    private val workflows = input.subList(0, input.indexOfFirst { it.isEmpty() })
            .map { Workflow.parse(it) }.associateBy { it.name }
    private val parts = input.subList(input.indexOfFirst { it.isEmpty() } + 1, input.size).map { Part.parse(it) }

    fun part1() = parts.filter { isAccepted(it) }.sumOf { it.ratings.values.sum() }

    private val initialWorkflow = "in"
    private val acceptanceWorkflow = "A"
    private val rejectionWorkflow = "R"
    private val terminalWorkflows = listOf(acceptanceWorkflow, rejectionWorkflow)
    private fun isAccepted(p: Part) = let {
        var curWorkflow = initialWorkflow
        while (!terminalWorkflows.contains(curWorkflow)) curWorkflow = workflows[curWorkflow]!!.process(p)
        curWorkflow == acceptanceWorkflow
    }

    data class Part(val ratings: Map<Char, Int>) {
        companion object {
            private val regex = """\{(x=\d+),(m=\d+),(a=\d+),(s=\d+)}""".toRegex()
            fun parse(line: String) = Part(
                    regex.matchEntire(line)!!.groups.drop(1).associate {
                        it!!.value.substringBefore('=').single() to it.value.substringAfter('=').toInt()
                    })
        }
    }

    sealed class Rule {
        abstract val target: String

        data class Conditional(
                val property: Char,
                val operator: Char,
                val threshold: Int,
                override val target: String) : Rule()

        data class Unconditional(override val target: String) : Rule()

        fun matches(p: Part) = when (this) {
            is Unconditional -> true
            is Conditional -> when (operator) {
                '<' -> p.ratings[property]!! < threshold
                '>' -> p.ratings[property]!! > threshold
                else -> error("unexpected comparison operator $operator")
            }
        }

        companion object {
            private val conditionalRegex = """([xmas])([><])(\d+):(\w+)""".toRegex()
            fun parse(rule: String) : Rule {
                val match = conditionalRegex.matchEntire(rule) ?: return Unconditional(rule)
                val (prop, op, thr, target) = match.destructured
                return Conditional(prop.single(), op.single(), thr.toInt(), target)
            }
        }
    }

    data class Workflow(val name: String, val rules : List<Rule>) {
        fun process(part: Part) = rules.first { it.matches(part) }.target

        companion object {
            private val regex = """(\w+)\{([\w><,:]+)}""".toRegex()
            fun parse(line: String) = let {
                val (name, rules) = regex.matchEntire(line)!!.destructured
                Workflow(name, rules.split(',').map { rawRule ->
                    Rule.parse(rawRule)
                })
            }
        }
    }
}
