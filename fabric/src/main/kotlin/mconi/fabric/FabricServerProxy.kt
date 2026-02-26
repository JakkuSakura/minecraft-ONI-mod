package mconi.fabric

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModInitializer
import mconi.common.world.OniSpawnHelper
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.level.storage.WritableLevelData
import net.minecraft.world.entity.Relative
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the server
 */
class FabricServerProxy(private val isDedicated: Boolean) : AbstractModInitializer.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Fabric Server Events")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, environment ->
            AbstractModInitializer.registerServerCommands(
                dispatcher as CommandDispatcher<CommandSourceStack>,
                environment == Commands.CommandSelection.ALL
                        || environment == Commands.CommandSelection.DEDICATED
            )
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            val level: ServerLevel = server.overworld()
            val respawn = LevelData.RespawnData.of(Level.OVERWORLD, OniSpawnHelper.spawnPos(), 0.0f, 0.0f)
            level.setRespawnData(respawn)
            val data = level.getLevelData() as? WritableLevelData
            data?.setSpawn(respawn)
        }

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val player: ServerPlayer = handler.player
            if (player.level().dimension() == Level.OVERWORLD) {
                val config = ServerPlayer.RespawnConfig(
                    LevelData.RespawnData.of(Level.OVERWORLD, OniSpawnHelper.spawnPos(), 0.0f, 0.0f),
                    true
                )
                player.setRespawnPosition(config, true)
                val spawn = OniSpawnHelper.spawnPos()
                player.teleportTo(
                    spawn.x + 0.5,
                    spawn.y.toDouble(),
                    spawn.z + 0.5
                )
            }
        }

        // register Fabric Server Events here
    }

    @Suppress("UNUSED")
    private fun isValidTime(): Boolean {
        if (isDedicated) {
            return true
        }
        return true
    }

    companion object {
        private val LOGGER: Logger = AbstractModInitializer.LOGGER
    }
}
