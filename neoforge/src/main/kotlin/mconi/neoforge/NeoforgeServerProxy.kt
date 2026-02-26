package mconi.neoforge

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModInitializer
import mconi.common.world.OniSpawnHelper
import mconi.neoforge.world.OniWorldEnforcer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.level.storage.WritableLevelData
import net.minecraft.world.entity.Relative
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.level.ChunkEvent
import net.neoforged.neoforge.event.level.LevelEvent
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the server
 */
class NeoforgeServerProxy(private val isDedicated: Boolean) : AbstractModInitializer.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering NeoForge Server Events")
        NeoForge.EVENT_BUS.register(this)
        // NeoForge Server Events here
    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        @Suppress("UNCHECKED_CAST")
        AbstractModInitializer.registerServerCommands(
            event.dispatcher as CommandDispatcher<CommandSourceStack>,
            event.commandSelection == Commands.CommandSelection.ALL
                    || event.commandSelection == Commands.CommandSelection.DEDICATED
        )
    }

    @SubscribeEvent
    fun onLevelLoad(event: LevelEvent.Load) {
        val serverLevel = event.level as? ServerLevel ?: return
        OniWorldEnforcer.applyWorldBorder(serverLevel)
        if (serverLevel.dimension() == Level.OVERWORLD) {
            val respawn = LevelData.RespawnData.of(Level.OVERWORLD, OniSpawnHelper.spawnPos(), 0.0f, 0.0f)
            serverLevel.setRespawnData(respawn)
            val data = serverLevel.getLevelData() as? WritableLevelData
            data?.setSpawn(respawn)
        }
    }

    @SubscribeEvent
    fun onChunkLoad(event: ChunkEvent.Load) {
        val serverLevel = event.level as? ServerLevel ?: return
        val chunk = event.chunk as? net.minecraft.world.level.chunk.LevelChunk ?: return
        OniWorldEnforcer.enforceChunk(serverLevel, chunk)
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as? ServerPlayer ?: return
        if (player.level().dimension() != Level.OVERWORLD) {
            return
        }
        val config = ServerPlayer.RespawnConfig(
            LevelData.RespawnData.of(Level.OVERWORLD, OniSpawnHelper.spawnPos(), 0.0f, 0.0f),
            true
        )
        player.setRespawnPosition(config, true)
        val serverLevel = player.level() as? ServerLevel ?: return
        val spawn = OniSpawnHelper.spawnPos()
        player.teleportTo(
            serverLevel,
            spawn.x + 0.5,
            spawn.y.toDouble(),
            spawn.z + 0.5,
            java.util.EnumSet.noneOf(Relative::class.java),
            player.yRot,
            player.xRot,
            true
        )
    }

    companion object {
        private val LOGGER: Logger = AbstractModInitializer.LOGGER
    }
}
