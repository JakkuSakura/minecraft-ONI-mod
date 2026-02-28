package mconi.common.sim.subsystem

interface OniSystem {
    fun id(): String
    fun run(context: SystemContext)
}
