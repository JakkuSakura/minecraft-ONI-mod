/*
 *    This file is part of the minecraft-ONI-mod
 *    licensed under the GNU GPL v3 License.
 *    (some parts of this file are originally from the Distant Horizons mod by James Seibel)
 *
 *    Copyright (C) 2024  Leander Knuttel
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mconi.common

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import mconi.common.sim.OniBlueprintRegistry
import mconi.common.sim.OniConstructionState
import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationSnapshot
import mconi.common.sim.OniSystemInspector
import mconi.common.sim.OniWorldFoundation
import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.LayerProperty
import mconi.common.sim.model.OniCellState
import mconi.common.sim.model.SystemLens
import mconi.common.wrappers.Utils
import net.minecraft.commands.CommandSourceStack
import net.minecraft.util.Mth
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Base for all mod loader initializers
 * and handles most setup.
 *
 * @author James Seibel
 * @author Leander Knuttel
 * @version 02.07.2024
 */
abstract class AbstractModInitializer {
    protected abstract fun createInitialBindings()
    protected abstract fun createClientProxy(): IEventProxy
    protected abstract fun createServerProxy(isDedicated: Boolean): IEventProxy
    protected abstract fun initializeModCompat()

    lateinit var loaderType: LoaderType

    fun onInitializeClient() {
        LOGGER.info("Initializing $MOD_NAME")

        startup()
        printModInfo()

        createClientProxy().registerEvents()
        createServerProxy(false).registerEvents()

        initializeModCompat()
        initConfig()

        LOGGER.info("$MOD_NAME Initialized")
    }

    fun onInitializeServer() {
        LOGGER.info("Initializing $MOD_NAME")

        startup()
        printModInfo()

        createServerProxy(true).registerEvents()

        initConfig()

        LOGGER.info("$MOD_NAME Initialized")
    }

    private fun startup() {
        INSTANCE = this
        createInitialBindings()
        OniServices.bootstrap()
    }

    private fun printModInfo() {
        LOGGER.info("$MOD_NAME, Version: $VERSION")
    }

    private fun initConfig() {
    }

    interface IEventProxy {
        fun registerEvents()
    }

    companion object {
        const val MOD_ID: String = "mconi"
        const val MOD_NAME: String = "Oxygen Not Included"
        const val VERSION: String = "1.0.0"

        @JvmField
        val LOGGER: Logger = LogManager.getLogger("MCONI")

        @JvmField
        var INSTANCE: AbstractModInitializer? = null

        @JvmStatic
        fun registerClientCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
            val exampleCommand = literal("client_example_command")
                .then(argument("example_string", StringArgumentType.word())
                    .then(argument("example_int", IntegerArgumentType.integer(0))
                        .executes { context ->
                            val exampleString = StringArgumentType.getString(context, "example_string")
                            val exampleInt = IntegerArgumentType.getInteger(context, "example_int")
                            Utils.sendToClientChat("Example Feedback:  example_string: $exampleString example_int: $exampleInt")
                            1
                        }))
            dispatcher.register(exampleCommand)
        }

        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun registerServerCommands(dispatcher: CommandDispatcher<CommandSourceStack>, _allOrDedicated: Boolean) {
            val oniCommand = literal("oni")
                .then(literal("sim")
                    .then(literal("status")
                        .executes { context ->
                            val snapshot: OniSimulationSnapshot = OniServices.simulationRuntime().snapshot()
                            Utils.SendFeedback(
                                context,
                                "ONI Simulation: running=${snapshot.running()}"
                                    + " serverTicks=${snapshot.serverTicks()}"
                                    + " simTicks=${snapshot.simulationTicks()}"
                                    + " lastSimTick=${snapshot.lastSimulationTick()}"
                                    + " interval=${snapshot.tickInterval()}"
                                    + " cellSize=${snapshot.cellSize()}"
                                    + " activeCells=${snapshot.activeCells()}"
                                    + " power=${String.format("%.1f", snapshot.powerGenerationW())}/${String.format("%.1f", snapshot.powerDemandW())}W"
                                    + " storedJ=${String.format("%.1f", snapshot.storedEnergyJ())}"
                                    + " powerTripped=${snapshot.powerTripped()}"
                                    + " stress=${String.format("%.2f", snapshot.colonyStress())}"
                                    + " researchUnlocked=${snapshot.unlockedResearchCount()}"
                                    + " buildQueue=${snapshot.activeConstructionCount()}",
                                true
                            )
                            1
                        })
                    .then(literal("pipeline")
                        .executes { context ->
                            Utils.SendFeedback(
                                context,
                                "ONI Simulation pipeline: " + OniServices.simulationRuntime().pipelineOrder().joinToString(" -> "),
                                true
                            )
                            1
                        })
                    .then(literal("pause")
                        .executes { context ->
                            OniServices.simulationRuntime().setRunning(false)
                            Utils.SendFeedback(context, "ONI Simulation paused.", true)
                            1
                        })
                    .then(literal("resume")
                        .executes { context ->
                            OniServices.simulationRuntime().setRunning(true)
                            Utils.SendFeedback(context, "ONI Simulation resumed.", true)
                            1
                        })
                    .then(literal("step")
                        .executes { context ->
                            OniServices.simulationRuntime().runOneSimulationStep(
                                OniServices.simulationRuntime().snapshot().serverTicks()
                            )
                            Utils.SendFeedback(context, "ONI Simulation executed one manual step.", true)
                            1
                        })
                    .then(literal("set_interval")
                        .then(argument("ticks", IntegerArgumentType.integer(1, 1200))
                            .executes { context ->
                                val ticks = IntegerArgumentType.getInteger(context, "ticks")
                                OniServices.simulationRuntime().config().setTickInterval(ticks)
                                Utils.SendFeedback(context, "ONI Simulation interval set to $ticks ticks.", true)
                                1
                            }))
                    .then(literal("set_cell_size")
                        .then(argument("blocks", IntegerArgumentType.integer(1, 16))
                            .executes { context ->
                                val size = IntegerArgumentType.getInteger(context, "blocks")
                                OniServices.simulationRuntime().config().setCellSize(size)
                                Utils.SendFeedback(context, "ONI Simulation cell size set to $size blocks.", true)
                                1
                            }))
                    .then(literal("touch_here")
                        .executes { context ->
                            val source = context.source
                            val x = Mth.floor(source.position.x)
                            val y = Mth.floor(source.position.y)
                            val z = Mth.floor(source.position.z)
                            OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
                                x,
                                y,
                                z,
                                OniServices.simulationRuntime().config().cellSize()
                            )
                            Utils.SendFeedback(context, "Created/loaded simulation cell at current position.", true)
                            1
                        })
                    .then(literal("inspect_here")
                        .executes { context ->
                            val source = context.source
                            val x = Mth.floor(source.position.x)
                            val y = Mth.floor(source.position.y)
                            val z = Mth.floor(source.position.z)
                            val cell: OniCellState = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
                                x,
                                y,
                                z,
                                OniServices.simulationRuntime().config().cellSize()
                            )
                            Utils.SendFeedback(
                                context,
                                "Cell: occupancy=${cell.occupancyState()}"
                                    + " pressure=${String.format("%.2f", cell.pressureKpa())}kPa"
                                    + " tempK=${String.format("%.2f", cell.temperatureK())}"
                                    + " o2Frac=${String.format("%.4f", cell.o2Fraction())}"
                                    + " co2Frac=${String.format("%.4f", cell.co2Fraction())}"
                                    + " breathBand=${cell.breathingBand()}"
                                    + " fluid=${cell.fluidSpecies()}"
                                    + " fluidMassKg=${String.format("%.3f", cell.fluidMassKg())}"
                                    + " O2kg=${String.format("%.3f", cell.gasMassKg(GasSpecies.O2))}"
                                    + " CO2kg=${String.format("%.3f", cell.gasMassKg(GasSpecies.CO2))}"
                                    + " H2kg=${String.format("%.3f", cell.gasMassKg(GasSpecies.H2))}",
                                true
                            )
                            1
                        })
                    .then(literal("inject_gas")
                        .then(argument("species", StringArgumentType.word())
                            .then(argument("mass_kg", DoubleArgumentType.doubleArg(0.0, 1000.0))
                                .executes { context ->
                                    val source = context.source
                                    val x = Mth.floor(source.position.x)
                                    val y = Mth.floor(source.position.y)
                                    val z = Mth.floor(source.position.z)
                                    val speciesInput = StringArgumentType.getString(context, "species")
                                    val massKg = DoubleArgumentType.getDouble(context, "mass_kg")
                                    val species = try {
                                        GasSpecies.valueOf(speciesInput.uppercase())
                                    } catch (_: IllegalArgumentException) {
                                        Utils.SendError(context, "Invalid gas species: $speciesInput. Use O2/CO2/H2.", true)
                                        return@executes 0
                                    }
                                    val cell: OniCellState = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
                                        x,
                                        y,
                                        z,
                                        OniServices.simulationRuntime().config().cellSize()
                                    )
                                    val updatedMass = cell.gasMassKg(species) + massKg
                                    cell.setGasMassKg(species, updatedMass)
                                    Utils.SendFeedback(context, "Injected $massKg kg of $species into current cell.", true)
                                    1
                                })))
                    .then(literal("inject_fluid")
                        .then(argument("species", StringArgumentType.word())
                            .then(argument("mass_kg", DoubleArgumentType.doubleArg(0.0, 10000.0))
                                .executes { context ->
                                    val source = context.source
                                    val x = Mth.floor(source.position.x)
                                    val y = Mth.floor(source.position.y)
                                    val z = Mth.floor(source.position.z)
                                    val speciesInput = StringArgumentType.getString(context, "species")
                                    val massKg = DoubleArgumentType.getDouble(context, "mass_kg")
                                    val species = try {
                                        FluidSpecies.valueOf(speciesInput.uppercase())
                                    } catch (_: IllegalArgumentException) {
                                        Utils.SendError(context, "Invalid fluid species. Use WATER/POLLUTED_WATER/CRUDE_OIL/LAVA.", true)
                                        return@executes 0
                                    }
                                    val cell: OniCellState = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
                                        x,
                                        y,
                                        z,
                                        OniServices.simulationRuntime().config().cellSize()
                                    )
                                    cell.setFluidState(species, massKg)
                                    Utils.SendFeedback(context, "Set fluid $species mass to $massKg kg in current cell.", true)
                                    1
                                })))
                    .then(literal("set_power")
                        .then(argument("generation_w", DoubleArgumentType.doubleArg(0.0, 10000000.0))
                            .then(argument("demand_w", DoubleArgumentType.doubleArg(0.0, 10000000.0))
                                .executes { context ->
                                    val generationW = DoubleArgumentType.getDouble(context, "generation_w")
                                    val demandW = DoubleArgumentType.getDouble(context, "demand_w")
                                    OniServices.simulationRuntime().powerState().setGenerationW(generationW)
                                    OniServices.simulationRuntime().powerState().setDemandW(demandW)
                                    Utils.SendFeedback(context, "Set power generation/demand to $generationW W/$demandW W.", true)
                                    1
                                })))
                    .then(literal("set_stress")
                        .then(argument("score", DoubleArgumentType.doubleArg(0.0, 100.0))
                            .executes { context ->
                                val score = DoubleArgumentType.getDouble(context, "score")
                                OniServices.simulationRuntime().stressState().setScore(score)
                                Utils.SendFeedback(context, "Set colony stress to $score.", true)
                                1
                            }))
                    .then(literal("research")
                        .then(literal("status")
                            .executes { context ->
                                Utils.SendFeedback(
                                    context,
                                    "Research unlocked nodes: ${OniServices.simulationRuntime().researchState().unlockedCount()} "
                                        + "${OniServices.simulationRuntime().researchState().unlockedNodes()}",
                                    true
                                )
                                1
                            })
                        .then(literal("unlock")
                            .then(argument("node", StringArgumentType.word())
                                .executes { context ->
                                    val node = StringArgumentType.getString(context, "node")
                                    OniServices.simulationRuntime().researchState().unlock(node)
                                    Utils.SendFeedback(context, "Unlocked research node: $node", true)
                                    1
                                })))
                    .then(literal("build")
                        .then(literal("status")
                            .executes { context ->
                                Utils.SendFeedback(
                                    context,
                                    "Build queue size: ${OniServices.simulationRuntime().constructionState().activeCount()}",
                                    true
                                )
                                for (task in OniServices.simulationRuntime().constructionState().tasks()) {
                                    Utils.SendFeedback(
                                        context,
                                        "- ${task.blueprintId}"
                                            + " progress=${String.format("%.2f", task.progressSeconds)}/${task.buildTimeSeconds}s"
                                            + " materials=${task.depositedMaterials}/${task.requiredMaterialUnits}"
                                            + " pausedReason=${task.pausedReason}",
                                        true
                                    )
                                }
                                1
                            })
                        .then(literal("blueprints")
                            .executes { context ->
                                Utils.SendFeedback(context, "Blueprints: ${OniBlueprintRegistry.allIds()}", true)
                                1
                            })
                        .then(literal("queue_blueprint")
                            .then(argument("blueprint", StringArgumentType.word())
                                .executes { context ->
                                    val blueprint = StringArgumentType.getString(context, "blueprint")
                                    val task: OniConstructionState.BuildTask? =
                                        OniServices.simulationRuntime().constructionState().queueBlueprint(blueprint)
                                    if (task == null) {
                                        Utils.SendError(context, "Unknown blueprint: $blueprint", true)
                                        return@executes 0
                                    }
                                    Utils.SendFeedback(context, "Queued build task for blueprint: $blueprint", true)
                                    1
                                }))
                        .then(literal("queue")
                            .then(argument("blueprint", StringArgumentType.word())
                                .then(argument("required_research", StringArgumentType.word())
                                    .then(argument("materials", IntegerArgumentType.integer(1, 100000))
                                        .then(argument("build_seconds", IntegerArgumentType.integer(1, 100000))
                                            .executes { context ->
                                                val blueprint = StringArgumentType.getString(context, "blueprint")
                                                val requiredResearch = StringArgumentType.getString(context, "required_research")
                                                val materials = IntegerArgumentType.getInteger(context, "materials")
                                                val buildSeconds = IntegerArgumentType.getInteger(context, "build_seconds")
                                                OniServices.simulationRuntime().constructionState().queueTask(
                                                    OniConstructionState.BuildTask(
                                                        blueprint,
                                                        requiredResearch,
                                                        materials,
                                                        buildSeconds,
                                                        0,
                                                        0.0,
                                                        "Missing materials"
                                                    )
                                                )
                                                Utils.SendFeedback(context, "Queued build task for blueprint: $blueprint", true)
                                                1
                                            })))))
                        .then(literal("deposit")
                            .then(argument("index", IntegerArgumentType.integer(0, 10000))
                                .then(argument("materials", IntegerArgumentType.integer(1, 100000))
                                    .executes { context ->
                                        val index = IntegerArgumentType.getInteger(context, "index")
                                        val materials = IntegerArgumentType.getInteger(context, "materials")
                                        val tasks = OniServices.simulationRuntime().constructionState().tasks()
                                        if (index >= tasks.size) {
                                            Utils.SendError(context, "Invalid task index.", true)
                                            return@executes 0
                                        }
                                        val task = tasks[index]
                                        task.depositedMaterials += materials
                                        Utils.SendFeedback(context, "Deposited $materials materials into task $index.", true)
                                        1
                                    }))))
                .then(literal("world")
                    .then(literal("here")
                        .executes { context ->
                            val source = context.source
                            val x = Mth.floor(source.position.x)
                            val y = Mth.floor(source.position.y)
                            val z = Mth.floor(source.position.z)
                            val minY = source.level.minY
                            val maxY = source.level.maxY - 1
                            val inBounds = OniWorldFoundation.isWithinHorizontalBounds(
                                x,
                                z,
                                OniServices.simulationRuntime().config()
                            )
                            val inVoidBand = OniWorldFoundation.isVoidBand(
                                y,
                                maxY,
                                OniServices.simulationRuntime().config()
                            )
                            val inLavaBand = OniWorldFoundation.isLavaBand(
                                y,
                                minY,
                                OniServices.simulationRuntime().config()
                            )
                            Utils.SendFeedback(
                                context,
                                "WorldFoundation: pos=($x,$y,$z)"
                                    + " inBounds=$inBounds"
                                    + " voidBand=$inVoidBand"
                                    + " lavaBand=$inLavaBand"
                                    + " yRange=[$minY,$maxY]",
                                true
                            )
                            1
                        }))
                .then(literal("glasses")
                    .then(literal("inspect")
                        .then(argument("system", StringArgumentType.word())
                            .executes { context ->
                                val systemInput = StringArgumentType.getString(context, "system")
                                val systemLens = SystemLens.fromInput(systemInput)
                                if (systemLens == null) {
                                    Utils.SendError(
                                        context,
                                        "Unknown system lens. Use atmosphere/fluid/thermal/oxygen/power/stress/research/construction.",
                                        true
                                    )
                                    return@executes 0
                                }
                                val source = context.source
                                val x = Mth.floor(source.position.x)
                                val y = Mth.floor(source.position.y)
                                val z = Mth.floor(source.position.z)
                                val cell: OniCellState = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
                                    x,
                                    y,
                                    z,
                                    OniServices.simulationRuntime().config().cellSize()
                                )
                                Utils.SendFeedback(
                                    context,
                                    "System glasses [${systemLens.name}] at ($x,$y,$z):",
                                    true
                                )
                                for (property: LayerProperty in OniSystemInspector.inspect(
                                    OniServices.simulationRuntime(),
                                    systemLens,
                                    cell
                                )) {
                                    Utils.SendFeedback(context, "[${property.layer()}] ${property.key()}=${property.value()}", true)
                                }
                                1
                            }))))
            dispatcher.register(oniCommand)

            val exampleCommand = literal("server_example_command")
                .then(argument("example_string", StringArgumentType.word())
                    .then(argument("example_int", IntegerArgumentType.integer(0))
                        .executes { context ->
                            val exampleString = StringArgumentType.getString(context, "example_string")
                            val exampleInt = IntegerArgumentType.getInteger(context, "example_int")
                            Utils.SendFeedback(
                                context,
                                "Example Feedback:  example_string: $exampleString example_int: $exampleInt",
                                true
                            )
                            1
                        }))
            dispatcher.register(exampleCommand)
        }

        private fun literal(string: String): LiteralArgumentBuilder<CommandSourceStack> {
            return LiteralArgumentBuilder.literal(string)
        }

        private fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<CommandSourceStack, T> {
            return RequiredArgumentBuilder.argument(name, type)
        }
    }
}
