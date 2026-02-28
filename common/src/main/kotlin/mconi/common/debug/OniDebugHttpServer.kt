package mconi.common.debug

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import mconi.common.AbstractModBootstrap
import mconi.common.item.OniBlueprintRegistry
import mconi.common.sim.OniServices
import mconi.common.world.OniMatterAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

object OniDebugHttpServer {
    private val started = AtomicBoolean(false)
    private val stopped = AtomicBoolean(false)
    @Volatile private var server: HttpServer? = null
    @Volatile private var executor: ExecutorService? = null
    @Volatile private var minecraftServer: MinecraftServer? = null

    @JvmStatic
    fun ensureStarted(serverRef: MinecraftServer) {
        minecraftServer = serverRef
        if (!isEnabled()) {
            return
        }
        if (!started.compareAndSet(false, true)) {
            return
        }

        try {
            val bind = System.getProperty("mconi.http.bind", "127.0.0.1").trim()
            val port = System.getProperty("mconi.http.port", "38080").trim().toInt()
            val address = InetSocketAddress(InetAddress.getByName(bind), port)
            val httpServer = HttpServer.create(address, 0)
            executor = Executors.newFixedThreadPool(2) { runnable ->
                Thread(runnable, "mconi-http").apply { isDaemon = true }
            }
            httpServer.executor = executor
            httpServer.createContext("/health", HealthHandler())
            httpServer.createContext("/snapshot", SnapshotHandler())
            httpServer.createContext("/power", PowerHandler())
            httpServer.createContext("/blueprints", BlueprintHandler())
            httpServer.createContext("/cell", CellHandler())
            httpServer.createContext("/world/sample", WorldSampleHandler())
            httpServer.start()
            server = httpServer
            AbstractModBootstrap.LOGGER.info("ONI debug HTTP server listening on {}:{}", bind, port)
        } catch (ex: Exception) {
            AbstractModBootstrap.LOGGER.warn("Failed to start ONI debug HTTP server: {}", ex.toString())
        }
    }

    @JvmStatic
    fun stop() {
        if (!stopped.compareAndSet(false, true)) {
            return
        }
        server?.stop(0)
        executor?.shutdownNow()
        server = null
        executor = null
        minecraftServer = null
        started.set(false)
    }

    private fun isEnabled(): Boolean {
        val value = System.getProperty("mconi.http.enabled", "true")
            .trim()
            .lowercase(Locale.ROOT)
        return value != "false" && value != "0" && value != "off"
    }

    private fun writeJson(exchange: HttpExchange, status: Int, body: String) {
        val payload = body.toByteArray(StandardCharsets.UTF_8)
        exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        exchange.sendResponseHeaders(status, payload.size.toLong())
        exchange.responseBody.use { output: OutputStream ->
            output.write(payload)
        }
    }

    private fun parseQuery(exchange: HttpExchange): Map<String, String> {
        val raw = exchange.requestURI.rawQuery ?: return emptyMap()
        if (raw.isEmpty()) {
            return emptyMap()
        }
        val map = HashMap<String, String>()
        raw.split("&").forEach { pair ->
            val idx = pair.indexOf('=')
            if (idx > 0) {
                map[pair.substring(0, idx)] = pair.substring(idx + 1)
            }
        }
        return map
    }

    private class HealthHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (!exchange.requestMethod.equals("GET", ignoreCase = true)) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}")
                return
            }
            val body = "{\"ok\":true,\"mod\":\"${AbstractModBootstrap.MOD_ID}\",\"version\":\"${AbstractModBootstrap.VERSION}\"}"
            writeJson(exchange, 200, body)
        }
    }

    private class SnapshotHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (!exchange.requestMethod.equals("GET", ignoreCase = true)) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}")
                return
            }
            val snapshot = OniServices.systemRuntime().snapshot()
            val body = buildString {
                append("{")
                append("\"running\":").append(snapshot.running())
                append(",\"serverTicks\":").append(snapshot.serverTicks())
                append(",\"systemTicks\":").append(snapshot.systemTicks())
                append(",\"lastSystemTick\":").append(snapshot.lastSystemTick())
                append(",\"tickInterval\":").append(snapshot.tickInterval())
                append(",\"cellSize\":").append(snapshot.cellSize())
                append(",\"activeCells\":").append(snapshot.activeCells())
                append(",\"powerGenerationW\":").append(snapshot.powerGenerationW())
                append(",\"powerDemandW\":").append(snapshot.powerDemandW())
                append(",\"storedEnergyJ\":").append(snapshot.storedEnergyJ())
                append(",\"powerTripped\":").append(snapshot.powerTripped())
                append(",\"colonyStress\":").append(snapshot.colonyStress())
                append(",\"unlockedResearchCount\":").append(snapshot.unlockedResearchCount())
                append(",\"activeConstructionCount\":").append(snapshot.activeConstructionCount())
                append("}")
            }
            writeJson(exchange, 200, body)
        }
    }

    private class PowerHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (!exchange.requestMethod.equals("GET", ignoreCase = true)) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}")
                return
            }
            val power = OniServices.systemRuntime().powerState()
            val body = buildString {
                append("{")
                append("\"generationW\":").append(power.generationW())
                append(",\"demandW\":").append(power.demandW())
                append(",\"storedEnergyJ\":").append(power.storedEnergyJ())
                append(",\"tripped\":").append(power.tripped())
                append("}")
            }
            writeJson(exchange, 200, body)
        }
    }

    private class BlueprintHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (!exchange.requestMethod.equals("GET", ignoreCase = true)) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}")
                return
            }
            val body = "{\"blueprints\":\"${OniBlueprintRegistry.allIds()}\"}"
            writeJson(exchange, 200, body)
        }
    }

    private class CellHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (!exchange.requestMethod.equals("GET", ignoreCase = true)) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}")
                return
            }
            val query = parseQuery(exchange)
            if (!query.containsKey("x") || !query.containsKey("y") || !query.containsKey("z")) {
                writeJson(exchange, 400, "{\"error\":\"missing_coordinates\"}")
                return
            }
            val x = query.getValue("x").toInt()
            val y = query.getValue("y").toInt()
            val z = query.getValue("z").toInt()
            val serverRef = minecraftServer
            val level: ServerLevel = serverRef?.overworld() ?: run {
                writeJson(exchange, 503, "{\"error\":\"overworld_unavailable\"}")
                return
            }
            val pos = net.minecraft.core.BlockPos(x, y, z)
            val state = level.getBlockState(pos)
            val gas = OniMatterAccess.gasSpec(state)
            val liquidId = OniMatterAccess.liquidId(state)
            val entity = OniMatterAccess.matterEntity(level, pos)
            val weight = entity?.massKg() ?: 0.0
            val tempK = entity?.temperatureK() ?: 293.15
            val body = buildString {
                append("{")
                append("\"x\":").append(x)
                append(",\"y\":").append(y)
                append(",\"z\":").append(z)
                append(",\"occupancy\":\"").append(
                    when {
                        gas != null -> "GAS"
                        liquidId != null -> "LIQUID"
                        state.isAir -> "VACUUM"
                        else -> "SOLID"
                    }
                ).append("\"")
                append(",\"temperatureK\":").append(tempK)
                append(",\"weightKg\":").append(weight)
                append(",\"gasId\":\"").append(gas?.id ?: "").append("\"")
                append(",\"liquidId\":\"").append(liquidId ?: "").append("\"")
                append("}")
            }
            writeJson(exchange, 200, body)
        }
    }

    private class WorldSampleHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (!exchange.requestMethod.equals("POST", ignoreCase = true) &&
                !exchange.requestMethod.equals("GET", ignoreCase = true)) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}")
                return
            }
            val serverRef = minecraftServer
            if (serverRef == null) {
                writeJson(exchange, 503, "{\"error\":\"server_unavailable\"}")
                return
            }
            val level: ServerLevel = serverRef.overworld() ?: run {
                writeJson(exchange, 503, "{\"error\":\"overworld_unavailable\"}")
                return
            }
            val query = parseQuery(exchange)
            val x = query["x"]?.toInt() ?: 0
            val y = query["y"]?.toInt() ?: level.minY
            val z = query["z"]?.toInt() ?: 0
            val radius = query["radius"]?.toInt() ?: 16
            writeJson(exchange, 501, "{\"error\":\"world_sampler_removed\"}")
        }
    }
}
