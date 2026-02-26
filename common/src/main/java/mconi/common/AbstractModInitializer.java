/*
 *    This file is part of the minecraft-ONI-mod
 *    licensed under the GNU GPL v3 License.
 *    (some parts of this file are originally from the Distant Horizons mod by James Seibel)
 *
 *    Copyright (C) 2024  Leander Knüttel
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

package mconi.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mconi.common.sim.OniServices;
import mconi.common.sim.OniConstructionState;
import mconi.common.sim.OniBlueprintRegistry;
import mconi.common.sim.OniSimulationSnapshot;
import mconi.common.sim.OniSystemInspector;
import mconi.common.sim.OniWorldFoundation;
import mconi.common.world.OniWorldgenBootstrap;
import mconi.common.sim.model.FluidSpecies;
import mconi.common.sim.model.GasSpecies;
import mconi.common.sim.model.LayerProperty;
import mconi.common.sim.model.OniCellState;
import mconi.common.sim.model.SystemLens;
import mconi.common.wrappers.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base for all mod loader initializers 
 * and handles most setup.
 *
 * @author James Seibel
 * @author Leander Knüttel
 * @version 02.07.2024
 */
public abstract class AbstractModInitializer
{
	public static final String MOD_ID = "mconi";
	public static final String MOD_NAME = "Oxygen Not Included";
	public static final String VERSION = "1.0.0";
	public static final Logger LOGGER = LogManager.getLogger("MCONI");
	public static AbstractModInitializer INSTANCE;
	
	//==================//
	// abstract methods //
	//==================//
	
	protected abstract void createInitialBindings();
	protected abstract IEventProxy createClientProxy();
	protected abstract IEventProxy createServerProxy(boolean isDedicated);
	protected abstract void initializeModCompat();
	
	//protected abstract void subscribeClientStartedEvent(Runnable eventHandler);
	//protected abstract void subscribeServerStartingEvent(Consumer<MinecraftServer> eventHandler);
	//protected abstract void runDelayedSetup();

	public LoaderType loaderType;
	
	//===================//
	// initialize events //
	//===================//
	
	public void onInitializeClient()
	{
		LOGGER.info("Initializing " + MOD_NAME);

		this.startup();//<-- common mod init in here
		this.printModInfo();

		this.createClientProxy().registerEvents();
		this.createServerProxy(false).registerEvents();

		this.initializeModCompat();
		this.initConfig();

		//Client Init here

		LOGGER.info(MOD_NAME + " Initialized");

		//this.subscribeClientStartedEvent(this::postInit);
	}
	
	public void onInitializeServer()
	{
		LOGGER.info("Initializing " + MOD_NAME);
		
		this.startup();//<-- common mod init in here
		this.printModInfo();

		this.createServerProxy(true).registerEvents();

		this.initConfig();

		//Server Init here

		LOGGER.info(MOD_NAME + " Initialized");

		/*this.subscribeServerStartingEvent(server ->
		{
			this.postInit();
			
			LOGGER.info("Dedicated server initialized at " + server.getServerDirectory());
		});*/
	}
	
	//===========================//
	// inner initializer methods //
	//===========================//

	/**
	 * common mod init for client and server
	 */
	private void startup()
	{
		INSTANCE = this;
		this.createInitialBindings();
		OniServices.bootstrap();
		OniWorldgenBootstrap.register();
		//do common mod init here
	}
	
	private void printModInfo()
	{
		LOGGER.info(MOD_NAME + ", Version: " + VERSION);
	}
	
	private void initConfig()
	{

	}
	
	/*private void postInit()
	{
		LOGGER.info("Post-Initializing Mod");
		this.runDelayedSetup();
		LOGGER.info("Mod Post-Initialized");
	}*/

	public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher){
		//Example Command
		LiteralArgumentBuilder<CommandSourceStack> exampleCommand = literal("client_example_command")
				.then(argument("example_string", StringArgumentType.word())
						.then(argument("example_int", IntegerArgumentType.integer(0))
								.executes(context -> {
									String example_string = StringArgumentType.getString(context, "example_string");
									int example_int = IntegerArgumentType.getInteger(context, "example_int");
									Utils.sendToClientChat("Example Feedback:  example_string: " + example_string + " example_int: " + example_int);
									return 1;
								})));
		//remember to register it...
		dispatcher.register(exampleCommand);

		//register client commands here
	}

	public static void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean allOrDedicated) {
		LiteralArgumentBuilder<CommandSourceStack> oniCommand = literal("oni")
				.then(literal("sim")
						.then(literal("status")
								.executes(context -> {
									OniSimulationSnapshot snapshot = OniServices.simulationRuntime().snapshot();
									Utils.SendFeedback(context,
											"ONI Simulation: running=" + snapshot.running()
													+ " serverTicks=" + snapshot.serverTicks()
													+ " simTicks=" + snapshot.simulationTicks()
													+ " lastSimTick=" + snapshot.lastSimulationTick()
													+ " interval=" + snapshot.tickInterval()
													+ " cellSize=" + snapshot.cellSize()
													+ " activeCells=" + snapshot.activeCells()
													+ " power=" + String.format("%.1f", snapshot.powerGenerationW()) + "/" + String.format("%.1f", snapshot.powerDemandW()) + "W"
													+ " storedJ=" + String.format("%.1f", snapshot.storedEnergyJ())
													+ " powerTripped=" + snapshot.powerTripped()
													+ " stress=" + String.format("%.2f", snapshot.colonyStress())
													+ " researchUnlocked=" + snapshot.unlockedResearchCount()
													+ " buildQueue=" + snapshot.activeConstructionCount(),
											true);
									return 1;
								}))
						.then(literal("pipeline")
								.executes(context -> {
									Utils.SendFeedback(
											context,
											"ONI Simulation pipeline: " + String.join(" -> ", OniServices.simulationRuntime().pipelineOrder()),
											true);
									return 1;
								}))
						.then(literal("pause")
								.executes(context -> {
									OniServices.simulationRuntime().setRunning(false);
									Utils.SendFeedback(context, "ONI Simulation paused.", true);
									return 1;
								}))
						.then(literal("resume")
								.executes(context -> {
									OniServices.simulationRuntime().setRunning(true);
									Utils.SendFeedback(context, "ONI Simulation resumed.", true);
									return 1;
								}))
						.then(literal("step")
								.executes(context -> {
									OniServices.simulationRuntime().runOneSimulationStep(
											OniServices.simulationRuntime().snapshot().serverTicks());
									Utils.SendFeedback(context, "ONI Simulation executed one manual step.", true);
									return 1;
								}))
						.then(literal("set_interval")
								.then(argument("ticks", IntegerArgumentType.integer(1, 1200))
										.executes(context -> {
											int ticks = IntegerArgumentType.getInteger(context, "ticks");
											OniServices.simulationRuntime().config().setTickInterval(ticks);
											Utils.SendFeedback(context, "ONI Simulation interval set to " + ticks + " ticks.", true);
											return 1;
										})))
						.then(literal("set_cell_size")
								.then(argument("blocks", IntegerArgumentType.integer(1, 16))
										.executes(context -> {
											int size = IntegerArgumentType.getInteger(context, "blocks");
											OniServices.simulationRuntime().config().setCellSize(size);
											Utils.SendFeedback(context, "ONI Simulation cell size set to " + size + " blocks.", true);
											return 1;
										})))
						.then(literal("touch_here")
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									int x = Mth.floor(source.getPosition().x);
									int y = Mth.floor(source.getPosition().y);
									int z = Mth.floor(source.getPosition().z);
									OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
											x,
											y,
											z,
											OniServices.simulationRuntime().config().cellSize());
									Utils.SendFeedback(context, "Created/loaded simulation cell at current position.", true);
									return 1;
								}))
						.then(literal("inspect_here")
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									int x = Mth.floor(source.getPosition().x);
									int y = Mth.floor(source.getPosition().y);
									int z = Mth.floor(source.getPosition().z);
									OniCellState cell = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
											x,
											y,
											z,
											OniServices.simulationRuntime().config().cellSize());
									Utils.SendFeedback(context,
											"Cell: occupancy=" + cell.occupancyState()
													+ " pressure=" + String.format("%.2f", cell.pressureKpa()) + "kPa"
													+ " tempK=" + String.format("%.2f", cell.temperatureK())
													+ " o2Frac=" + String.format("%.4f", cell.o2Fraction())
													+ " co2Frac=" + String.format("%.4f", cell.co2Fraction())
													+ " breathBand=" + cell.breathingBand()
													+ " fluid=" + cell.fluidSpecies()
													+ " fluidMassKg=" + String.format("%.3f", cell.fluidMassKg())
													+ " O2kg=" + String.format("%.3f", cell.gasMassKg(GasSpecies.O2))
													+ " CO2kg=" + String.format("%.3f", cell.gasMassKg(GasSpecies.CO2))
													+ " H2kg=" + String.format("%.3f", cell.gasMassKg(GasSpecies.H2)),
											true);
									return 1;
								}))
						.then(literal("inject_gas")
								.then(argument("species", StringArgumentType.word())
										.then(argument("mass_kg", DoubleArgumentType.doubleArg(0.0D, 1000.0D))
												.executes(context -> {
													CommandSourceStack source = context.getSource();
													int x = Mth.floor(source.getPosition().x);
													int y = Mth.floor(source.getPosition().y);
													int z = Mth.floor(source.getPosition().z);
													String speciesInput = StringArgumentType.getString(context, "species");
													double massKg = DoubleArgumentType.getDouble(context, "mass_kg");
													GasSpecies species;
													try
													{
														species = GasSpecies.valueOf(speciesInput.toUpperCase());
													}
													catch (IllegalArgumentException exception)
													{
														Utils.SendError(context, "Invalid gas species: " + speciesInput + ". Use O2/CO2/H2.", true);
														return 0;
													}
													OniCellState cell = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
															x,
															y,
															z,
															OniServices.simulationRuntime().config().cellSize());
													double updatedMass = cell.gasMassKg(species) + massKg;
													cell.setGasMassKg(species, updatedMass);
													Utils.SendFeedback(context,
															"Injected " + massKg + "kg of " + species + " into current cell.",
															true);
													return 1;
												}))))
						.then(literal("inject_fluid")
								.then(argument("species", StringArgumentType.word())
										.then(argument("mass_kg", DoubleArgumentType.doubleArg(0.0D, 10000.0D))
												.executes(context -> {
													CommandSourceStack source = context.getSource();
													int x = Mth.floor(source.getPosition().x);
													int y = Mth.floor(source.getPosition().y);
													int z = Mth.floor(source.getPosition().z);
													String speciesInput = StringArgumentType.getString(context, "species");
													double massKg = DoubleArgumentType.getDouble(context, "mass_kg");
													FluidSpecies species;
													try
													{
														species = FluidSpecies.valueOf(speciesInput.toUpperCase());
													}
													catch (IllegalArgumentException exception)
													{
														Utils.SendError(context, "Invalid fluid species. Use WATER/POLLUTED_WATER/CRUDE_OIL/LAVA.", true);
														return 0;
													}
													OniCellState cell = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
															x,
															y,
															z,
															OniServices.simulationRuntime().config().cellSize());
													cell.setFluidState(species, massKg);
													Utils.SendFeedback(context, "Set fluid " + species + " mass to " + massKg + "kg in current cell.", true);
													return 1;
												}))))
						.then(literal("set_power")
								.then(argument("generation_w", DoubleArgumentType.doubleArg(0.0D, 10000000.0D))
										.then(argument("demand_w", DoubleArgumentType.doubleArg(0.0D, 10000000.0D))
												.executes(context -> {
													double generationW = DoubleArgumentType.getDouble(context, "generation_w");
													double demandW = DoubleArgumentType.getDouble(context, "demand_w");
													OniServices.simulationRuntime().powerState().setGenerationW(generationW);
													OniServices.simulationRuntime().powerState().setDemandW(demandW);
													Utils.SendFeedback(context, "Set power generation/demand to " + generationW + "W/" + demandW + "W.", true);
													return 1;
												}))))
						.then(literal("set_stress")
								.then(argument("score", DoubleArgumentType.doubleArg(0.0D, 100.0D))
										.executes(context -> {
											double score = DoubleArgumentType.getDouble(context, "score");
											OniServices.simulationRuntime().stressState().setScore(score);
											Utils.SendFeedback(context, "Set colony stress to " + score + ".", true);
											return 1;
										})))
						.then(literal("research")
								.then(literal("status")
										.executes(context -> {
											Utils.SendFeedback(
													context,
													"Research unlocked nodes: " + OniServices.simulationRuntime().researchState().unlockedCount()
															+ " " + OniServices.simulationRuntime().researchState().unlockedNodes(),
													true);
											return 1;
										}))
								.then(literal("unlock")
										.then(argument("node", StringArgumentType.word())
												.executes(context -> {
													String node = StringArgumentType.getString(context, "node");
													OniServices.simulationRuntime().researchState().unlock(node);
													Utils.SendFeedback(context, "Unlocked research node: " + node, true);
													return 1;
												}))))
						.then(literal("build")
								.then(literal("status")
										.executes(context -> {
											Utils.SendFeedback(
													context,
													"Build queue size: " + OniServices.simulationRuntime().constructionState().activeCount(),
													true);
											for (OniConstructionState.BuildTask task : OniServices.simulationRuntime().constructionState().tasks())
											{
												Utils.SendFeedback(
														context,
														"- " + task.getBlueprintId()
																+ " progress=" + String.format("%.2f", task.getProgressSeconds()) + "/" + task.getBuildTimeSeconds() + "s"
																+ " materials=" + task.getDepositedMaterials() + "/" + task.getRequiredMaterialUnits()
																+ " pausedReason=" + task.getPausedReason(),
														true);
											}
											return 1;
										}))
								.then(literal("blueprints")
										.executes(context -> {
											Utils.SendFeedback(context, "Blueprints: " + OniBlueprintRegistry.allIds(), true);
											return 1;
										}))
								.then(literal("queue_blueprint")
										.then(argument("blueprint", StringArgumentType.word())
												.executes(context -> {
													String blueprint = StringArgumentType.getString(context, "blueprint");
													OniConstructionState.BuildTask task = OniServices.simulationRuntime().constructionState().queueBlueprint(blueprint);
													if (task == null)
													{
														Utils.SendError(context, "Unknown blueprint: " + blueprint, true);
														return 0;
													}
													Utils.SendFeedback(context, "Queued build task for blueprint: " + blueprint, true);
													return 1;
												})))
								.then(literal("queue")
										.then(argument("blueprint", StringArgumentType.word())
												.then(argument("required_research", StringArgumentType.word())
														.then(argument("materials", IntegerArgumentType.integer(1, 100000))
																.then(argument("build_seconds", IntegerArgumentType.integer(1, 100000))
																		.executes(context -> {
																			String blueprint = StringArgumentType.getString(context, "blueprint");
																			String requiredResearch = StringArgumentType.getString(context, "required_research");
																			int materials = IntegerArgumentType.getInteger(context, "materials");
																			int buildSeconds = IntegerArgumentType.getInteger(context, "build_seconds");
																			OniServices.simulationRuntime().constructionState().queueTask(
																					new OniConstructionState.BuildTask(blueprint, requiredResearch, materials, buildSeconds, 0, 0.0D, "Missing materials"));
																			Utils.SendFeedback(context, "Queued build task for blueprint: " + blueprint, true);
																			return 1;
																		}))))))
								.then(literal("deposit")
										.then(argument("index", IntegerArgumentType.integer(0, 10000))
												.then(argument("materials", IntegerArgumentType.integer(1, 100000))
														.executes(context -> {
															int index = IntegerArgumentType.getInteger(context, "index");
															int materials = IntegerArgumentType.getInteger(context, "materials");
															if (index >= OniServices.simulationRuntime().constructionState().tasks().size())
															{
																Utils.SendError(context, "Invalid task index.", true);
																return 0;
															}
															OniConstructionState.BuildTask task = OniServices.simulationRuntime().constructionState().tasks().get(index);
															task.setDepositedMaterials(task.getDepositedMaterials() + materials);
															Utils.SendFeedback(context, "Deposited " + materials + " materials into task " + index + ".", true);
															return 1;
														})))))
				.then(literal("world")
						.then(literal("here")
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									int x = Mth.floor(source.getPosition().x);
									int y = Mth.floor(source.getPosition().y);
									int z = Mth.floor(source.getPosition().z);
									int minY = source.getLevel().getMinY();
									int maxY = source.getLevel().getMaxY() - 1;
									boolean inBounds = OniWorldFoundation.isWithinHorizontalBounds(
											x,
											z,
											OniServices.simulationRuntime().config());
									boolean inVoidBand = OniWorldFoundation.isVoidBand(
											y,
											maxY,
											OniServices.simulationRuntime().config());
									boolean inLavaBand = OniWorldFoundation.isLavaBand(
											y,
											minY,
											OniServices.simulationRuntime().config());
									Utils.SendFeedback(context,
											"WorldFoundation: pos=(" + x + "," + y + "," + z + ")"
													+ " inBounds=" + inBounds
													+ " voidBand=" + inVoidBand
													+ " lavaBand=" + inLavaBand
													+ " yRange=[" + minY + "," + maxY + "]",
											true);
									return 1;
								})))
				.then(literal("glasses")
						.then(literal("inspect")
								.then(argument("system", StringArgumentType.word())
										.executes(context -> {
											String systemInput = StringArgumentType.getString(context, "system");
											SystemLens systemLens = SystemLens.fromInput(systemInput);
											if (systemLens == null)
											{
												Utils.SendError(context, "Unknown system lens. Use atmosphere/fluid/thermal/oxygen/power/stress/research/construction.", true);
												return 0;
											}
											CommandSourceStack source = context.getSource();
											int x = Mth.floor(source.getPosition().x);
											int y = Mth.floor(source.getPosition().y);
											int z = Mth.floor(source.getPosition().z);
											OniCellState cell = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
													x,
													y,
													z,
													OniServices.simulationRuntime().config().cellSize());
											Utils.SendFeedback(context, "System glasses [" + systemLens.name() + "] at (" + x + "," + y + "," + z + "):", true);
											for (LayerProperty property : OniSystemInspector.inspect(OniServices.simulationRuntime(), systemLens, cell))
											{
												Utils.SendFeedback(context, "[" + property.layer() + "] " + property.key() + "=" + property.value(), true);
											}
											return 1;
											})))));
		dispatcher.register(oniCommand);

		//Example Command
		LiteralArgumentBuilder<CommandSourceStack> exampleCommand = literal("server_example_command")
				.then(argument("example_string", StringArgumentType.word())
						.then(argument("example_int", IntegerArgumentType.integer(0))
								.executes(context -> {
									String example_string = StringArgumentType.getString(context, "example_string");
									int example_int = IntegerArgumentType.getInteger(context, "example_int");
									Utils.SendFeedback(context, "Example Feedback:  example_string: " + example_string + " example_int: " + example_int, true);
									return 1;
								})));
		//remember to register it...
		dispatcher.register(exampleCommand);

		//register server commands here
	}

	private static LiteralArgumentBuilder<CommandSourceStack> literal(String string) {
		return LiteralArgumentBuilder.literal(string);
	}
	private static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	//================//
	// helper classes //
	//================//
	
	public interface IEventProxy
	{
		void registerEvents();
	}
}
