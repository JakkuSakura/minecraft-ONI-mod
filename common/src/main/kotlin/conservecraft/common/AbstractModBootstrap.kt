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

package conservecraft.common

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import conservecraft.common.element.OniElements
import conservecraft.common.sim.OniServices
import conservecraft.common.sim.OniSystemInspector
import conservecraft.common.sim.OniSystemSnapshot
import conservecraft.common.sim.OniWorldFoundation
import conservecraft.common.sim.model.LayerProperty
import conservecraft.common.sim.model.SystemLens
import conservecraft.common.world.OniMatterAccess
import conservecraft.common.wrappers.Utils
import net.minecraft.commands.CommandSourceStack
import net.minecraft.util.Mth
import net.minecraft.core.BlockPos
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Shared mod loader bootstrap
 * and handles most setup.
 *
 * @author James Seibel
 * @author Leander Knuttel
 * @version 02.07.2024
 */
abstract class AbstractModBootstrap {
    protected abstract fun createBindings()
    protected abstract fun createClientProxy(): IEventProxy
    protected abstract fun createServerProxy(isDedicated: Boolean): IEventProxy
    protected abstract fun setupModCompat()

    lateinit var loaderType: LoaderType

    fun onSetupClient() {
        LOGGER.info("Starting $MOD_NAME")

        startup()
        printModInfo()

        createClientProxy().registerEvents()
        createServerProxy(false).registerEvents()

        setupModCompat()
        initConfig()

        LOGGER.info("$MOD_NAME Ready")
    }

    fun onSetupServer() {
        LOGGER.info("Starting $MOD_NAME")

        startup()
        printModInfo()

        createServerProxy(true).registerEvents()

        initConfig()

        LOGGER.info("$MOD_NAME Ready")
    }

    private fun startup() {
        INSTANCE = this
        createBindings()
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
        const val MOD_ID: String = "conservecraft"
        const val MOD_NAME: String = "ConserveCraft"
        const val VERSION: String = "1.0.0"

        @JvmField
        val LOGGER: Logger = LogManager.getLogger("ConserveCraft")

        @JvmField
        var INSTANCE: AbstractModBootstrap? = null

        @JvmStatic
        fun registerClientCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
            val oniConfig = literal("oni_config")
                .executes {
                    conservecraft.common.client.OniClientScreens.openWorldgenConfigScreen()
                    1
                }
            dispatcher.register(oniConfig)

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
                .then(literal("system")
                    .then(literal("status")
                        .executes { context ->
                            val snapshot: OniSystemSnapshot = OniServices.systemRuntime().snapshot()
                            Utils.SendFeedback(
                                context,
                                "ONI System: running=${snapshot.running()}"
                                    + " serverTicks=${snapshot.serverTicks()}"
                                    + " systemTicks=${snapshot.systemTicks()}"
                                    + " lastSystemTick=${snapshot.lastSystemTick()}"
                                    + " interval=${snapshot.tickInterval()}"
                                    + " cellSize=${snapshot.cellSize()}"
                                    + " activeCells=${snapshot.activeCells()}"
                                    + " power=${String.format("%.1f", snapshot.powerGenerationW())}/${String.format("%.1f", snapshot.powerDemandW())}W"
                                    + " storedJ=${String.format("%.1f", snapshot.storedEnergyJ())}"
                                    + " powerTripped=${snapshot.powerTripped()}"
                                    + " stress=${String.format("%.2f", snapshot.colonyStress())}"
                                    + " researchUnlocked=${snapshot.unlockedResearchCount()}",
                                true
                            )
                            1
                        })
                    .then(literal("pipeline")
                        .executes { context ->
                            Utils.SendFeedback(
                                context,
                                "ONI System pipeline: " + OniServices.systemRuntime().pipelineOrder().joinToString(" -> "),
                                true
                            )
                            1
                        })
                    .then(literal("pause")
                        .executes { context ->
                            OniServices.systemRuntime().setRunning(false)
                            Utils.SendFeedback(context, "ONI System paused.", true)
                            1
                        })
                    .then(literal("resume")
                        .executes { context ->
                            OniServices.systemRuntime().setRunning(true)
                            Utils.SendFeedback(context, "ONI System resumed.", true)
                            1
                        })
                    .then(literal("step")
                        .executes { context ->
                            val server = context.source.server
                            val level = server.overworld()
                            if (level != null) {
                                OniServices.systemRuntime().runOneSystemStep(
                                    OniServices.systemRuntime().snapshot().serverTicks(),
                                    level
                                )
                            }
                            Utils.SendFeedback(context, "ONI System executed one manual step.", true)
                            1
                        })
                    .then(literal("set_interval")
                        .then(argument("ticks", IntegerArgumentType.integer(1, 1200))
                            .executes { context ->
                                val ticks = IntegerArgumentType.getInteger(context, "ticks")
                                OniServices.systemRuntime().config().setTickInterval(ticks)
                                Utils.SendFeedback(context, "ONI System interval set to $ticks ticks.", true)
                                1
                            }))
                    .then(literal("set_cell_size")
                        .then(argument("blocks", IntegerArgumentType.integer(1, 16))
                            .executes { context ->
                                val size = IntegerArgumentType.getInteger(context, "blocks")
                                OniServices.systemRuntime().config().setCellSize(size)
                                Utils.SendFeedback(context, "ONI System cell size set to $size blocks.", true)
                                1
                            }))
                    .then(literal("inspect_here")
                        .executes { context ->
                            val source = context.source
                            val x = Mth.floor(source.position.x)
                            val y = Mth.floor(source.position.y)
                            val z = Mth.floor(source.position.z)
                            val level = source.level
                            val pos = BlockPos(x, y, z)
                            val state = level.getBlockState(pos)
                            val gas = OniMatterAccess.gasSpec(state)
                            val liquidId = OniMatterAccess.liquidId(state)
                            val entity = OniMatterAccess.matterEntity(level, pos)
                            val mass = entity?.mass() ?: 0.0
                            val tempK = entity?.temperatureK() ?: 293.15
                            Utils.SendFeedback(
                                context,
                                "Block: occupancy=${
                                    when {
                                        gas != null -> "GAS"
                                        liquidId != null -> "LIQUID"
                                        state.isAir -> "VACUUM"
                                        else -> "SOLID"
                                    }
                                }"
                                    + " tempK=${String.format("%.2f", tempK)}"
                                    + " mass=${String.format("%.3f", mass)}"
                                    + " gas=${gas?.id ?: "none"}"
                                    + " liquid=${liquidId ?: "none"}",
                                true
                            )
                            1
                        })
                    .then(literal("inject_gas")
                        .then(argument("species", StringArgumentType.word())
                            .then(argument("mass", DoubleArgumentType.doubleArg(0.0, 1000.0))
                                .executes { context ->
                                    val source = context.source
                                    val x = Mth.floor(source.position.x)
                                    val y = Mth.floor(source.position.y)
                                    val z = Mth.floor(source.position.z)
                                    val speciesInput = StringArgumentType.getString(context, "species")
                                    val mass = DoubleArgumentType.getDouble(context, "mass")
                                    val species = OniElements.parseGas(speciesInput)
                                    if (species == null) {
                                        Utils.SendError(context, "Invalid gas species: $speciesInput. Use O2/CO2/H2/CH4/Steam/Cl2.", true)
                                        return@executes 0
                                    }
                                    val level = source.level
                                    val pos = BlockPos(x, y, z)
                                    val targetState = when (species) {
                                        OniElements.GAS_OXYGEN -> OniBlockLookup.state(OniBlockFactory.OXYGEN_GAS)
                                        OniElements.GAS_CARBON_DIOXIDE -> OniBlockLookup.state(OniBlockFactory.CARBON_DIOXIDE_GAS)
                                        OniElements.GAS_HYDROGEN -> OniBlockLookup.state(OniBlockFactory.HYDROGEN_GAS)
                                        OniElements.GAS_METHANE -> OniBlockLookup.state(OniBlockFactory.METHANE_GAS)
                                        OniElements.GAS_STEAM -> OniBlockLookup.state(OniBlockFactory.STEAM_GAS)
                                        OniElements.GAS_CHLORINE -> OniBlockLookup.state(OniBlockFactory.CHLORINE_GAS)
                                        else -> null
                                    }
                                    if (targetState == null) {
                                        Utils.SendError(context, "Unsupported gas species: $speciesInput.", true)
                                        return@executes 0
                                    }
                                    level.setBlock(pos, targetState, 2)
                                    val entity = OniMatterAccess.matterEntity(level, pos)
                                    if (entity != null) {
                                        entity.setMass(entity.mass() + mass)
                                        entity.setTemperatureK(species.defaultTemperature)
                                    }
                                    Utils.SendFeedback(context, "Injected $mass of ${species.symbol} into current block.", true)
                                    1
                                })))
                    .then(literal("inject_liquid")
                        .then(argument("species", StringArgumentType.word())
                            .then(argument("mass", DoubleArgumentType.doubleArg(0.0, 10000.0))
                                .executes { context ->
                                    val source = context.source
                                    val x = Mth.floor(source.position.x)
                                    val y = Mth.floor(source.position.y)
                                    val z = Mth.floor(source.position.z)
                                    val speciesInput = StringArgumentType.getString(context, "species")
                                    val mass = DoubleArgumentType.getDouble(context, "mass")
                                    val liquidId = OniElements.parseLiquidId(speciesInput)
                                    if (liquidId == null) {
                                        Utils.SendError(context, "Invalid liquid id. Use water/polluted_water/crude_oil/lava/salt_water/brine/ethanol/petroleum/milk.", true)
                                        return@executes 0
                                    }
                                    val level = source.level
                                    val pos = BlockPos(x, y, z)
                                    val targetState = when (liquidId) {
                                        OniElements.LIQUID_WATER -> OniBlockLookup.state(OniBlockFactory.WATER)
                                        OniElements.LIQUID_POLLUTED_WATER -> OniBlockLookup.state(OniBlockFactory.POLLUTED_WATER)
                                        OniElements.LIQUID_CRUDE_OIL -> OniBlockLookup.state(OniBlockFactory.CRUDE_OIL)
                                        OniElements.LIQUID_LAVA -> OniBlockLookup.state(OniBlockFactory.LAVA)
                                        OniElements.LIQUID_SALT_WATER -> OniBlockLookup.state(OniBlockFactory.SALT_WATER)
                                        OniElements.LIQUID_BRINE -> OniBlockLookup.state(OniBlockFactory.BRINE)
                                        OniElements.LIQUID_ETHANOL -> OniBlockLookup.state(OniBlockFactory.ETHANOL)
                                        OniElements.LIQUID_PETROLEUM -> OniBlockLookup.state(OniBlockFactory.PETROLEUM)
                                        OniElements.LIQUID_MILK -> OniBlockLookup.state(OniBlockFactory.MILK)
                                        OniElements.LIQUID_NATURAL_RESIN -> OniBlockLookup.state(OniBlockFactory.NATURAL_RESIN)
                                        OniElements.LIQUID_PHYTO_OIL -> OniBlockLookup.state(OniBlockFactory.PHYTO_OIL)
                                        OniElements.LIQUID_MOLTEN_GLASS -> OniBlockLookup.state(OniBlockFactory.MOLTEN_GLASS)
                                        OniElements.LIQUID_SUPER_COOLANT -> OniBlockLookup.state(OniBlockFactory.SUPER_COOLANT)
                                        OniElements.LIQUID_VISCO_GEL -> OniBlockLookup.state(OniBlockFactory.VISCO_GEL)
                                        else -> null
                                    }
                                    if (targetState == null) {
                                        Utils.SendError(context, "Unsupported liquid id: $liquidId.", true)
                                        return@executes 0
                                    }
                                    level.setBlock(pos, targetState, 2)
                                    val entity = OniMatterAccess.matterEntity(level, pos)
                                    if (entity != null) {
                                        entity.setMass(mass)
                                        val spec = OniElements.liquidSpec(liquidId)
                                        if (spec != null) {
                                            entity.setTemperatureK(spec.defaultTemperatureK)
                                        }
                                    }
                                    Utils.SendFeedback(context, "Set liquid $liquidId mass to $mass in current block.", true)
                                    1
                                })))
                    .then(literal("set_power")
                        .then(argument("generation_w", DoubleArgumentType.doubleArg(0.0, 10000000.0))
                            .then(argument("demand_w", DoubleArgumentType.doubleArg(0.0, 10000000.0))
                                .executes { context ->
                                    val generationW = DoubleArgumentType.getDouble(context, "generation_w")
                                    val demandW = DoubleArgumentType.getDouble(context, "demand_w")
                                    OniServices.systemRuntime().powerState().setGenerationW(generationW)
                                    OniServices.systemRuntime().powerState().setDemandW(demandW)
                                    Utils.SendFeedback(context, "Set power generation/demand to $generationW W/$demandW W.", true)
                                    1
                                })))
                    .then(literal("set_stress")
                        .then(argument("score", DoubleArgumentType.doubleArg(0.0, 100.0))
                            .executes { context ->
                                val score = DoubleArgumentType.getDouble(context, "score")
                                val player = context.source.player
                                if (player != null) {
                                    OniServices.systemRuntime().stressState().setScore(player, score)
                                    Utils.SendFeedback(context, "Set player stress to $score.", true)
                                } else {
                                    OniServices.systemRuntime().stressState().setScore(score)
                                    Utils.SendFeedback(context, "Set colony stress to $score.", true)
                                }
                                1
                            }))
                    .then(literal("research")
                        .then(literal("status")
                            .executes { context ->
                                Utils.SendFeedback(
                                    context,
                                    "Research unlocked nodes: ${OniServices.systemRuntime().researchState().unlockedCount()} "
                                        + "${OniServices.systemRuntime().researchState().unlockedNodes()}",
                                    true
                                )
                                1
                            })
                        .then(literal("unlock")
                            .then(argument("node", StringArgumentType.word())
                                .executes { context ->
                                    val node = StringArgumentType.getString(context, "node")
                                    OniServices.systemRuntime().researchState().unlock(node)
                                    Utils.SendFeedback(context, "Unlocked research node: $node", true)
                                    1
                                })))
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
                                OniServices.systemRuntime().config()
                            )
                            val inVoidBand = OniWorldFoundation.isVoidBand(
                                y,
                                maxY,
                                OniServices.systemRuntime().config()
                            )
                            val inLavaBand = OniWorldFoundation.isLavaBand(
                                y,
                                minY,
                                OniServices.systemRuntime().config()
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
                                        "Unknown system lens. Use atmosphere/liquid/thermal/gas/power/stress/research/construction.",
                                        true
                                    )
                                    return@executes 0
                                }
                                val source = context.source
                                val x = Mth.floor(source.position.x)
                                val y = Mth.floor(source.position.y)
                                val z = Mth.floor(source.position.z)
                                val level = source.level
                                val pos = BlockPos(x, y, z)
                                Utils.SendFeedback(
                                    context,
                                    "System glasses [${systemLens.name}] at ($x,$y,$z):",
                                    true
                                )
                                val commandPlayer = context.source.entity as? net.minecraft.world.entity.player.Player
                                for (property: LayerProperty in OniSystemInspector.inspect(
                                    OniServices.systemRuntime(),
                                    systemLens,
                                    level,
                                    pos,
                                    commandPlayer
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
