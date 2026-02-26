package mconi.common.debug;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import mconi.common.AbstractModInitializer;
import mconi.common.sim.OniServices;
import mconi.common.sim.OniSimulationSnapshot;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class OniDebugHttpServer {
    private static final AtomicBoolean STARTED = new AtomicBoolean(false);
    private static final AtomicBoolean STOPPED = new AtomicBoolean(false);
    private static volatile HttpServer server;
    private static volatile ExecutorService executor;

    private OniDebugHttpServer() {
    }

    public static void ensureStarted(MinecraftServer minecraftServer) {
        if (!isEnabled()) {
            return;
        }
        if (!STARTED.compareAndSet(false, true)) {
            return;
        }

        try {
            String bind = System.getProperty("mconi.http.bind", "127.0.0.1").trim();
            int port = Integer.parseInt(System.getProperty("mconi.http.port", "38080").trim());
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(bind), port);
            HttpServer httpServer = HttpServer.create(address, 0);
            executor = Executors.newFixedThreadPool(2, r -> {
                Thread thread = new Thread(r, "mconi-http");
                thread.setDaemon(true);
                return thread;
            });
            httpServer.setExecutor(executor);
            httpServer.createContext("/health", new HealthHandler());
            httpServer.createContext("/snapshot", new SnapshotHandler());
            httpServer.start();
            server = httpServer;
            AbstractModInitializer.LOGGER.info("ONI debug HTTP server listening on {}:{}", bind, port);
        } catch (Exception ex) {
            AbstractModInitializer.LOGGER.warn("Failed to start ONI debug HTTP server: {}", ex.toString());
        }
    }

    public static void stop() {
        if (!STOPPED.compareAndSet(false, true)) {
            return;
        }
        HttpServer httpServer = server;
        if (httpServer != null) {
            httpServer.stop(0);
        }
        ExecutorService exec = executor;
        if (exec != null) {
            exec.shutdownNow();
        }
        server = null;
        executor = null;
        STARTED.set(false);
    }

    private static boolean isEnabled() {
        String value = System.getProperty("mconi.http.enabled", "true").trim().toLowerCase(Locale.ROOT);
        return !("false".equals(value) || "0".equals(value) || "off".equals(value));
    }

    private static void writeJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] payload = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, payload.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(payload);
        }
    }

    private static final class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}");
                return;
            }
            String body = "{\"ok\":true,\"mod\":\"" + AbstractModInitializer.MOD_ID +
                "\",\"version\":\"" + AbstractModInitializer.VERSION + "\"}";
            writeJson(exchange, 200, body);
        }
    }

    private static final class SnapshotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeJson(exchange, 405, "{\"error\":\"method_not_allowed\"}");
                return;
            }
            OniSimulationSnapshot snapshot = OniServices.simulationRuntime().snapshot();
            String body = "{"
                + "\"running\":" + snapshot.running()
                + ",\"serverTicks\":" + snapshot.serverTicks()
                + ",\"simulationTicks\":" + snapshot.simulationTicks()
                + ",\"lastSimulationTick\":" + snapshot.lastSimulationTick()
                + ",\"tickInterval\":" + snapshot.tickInterval()
                + ",\"cellSize\":" + snapshot.cellSize()
                + ",\"activeCells\":" + snapshot.activeCells()
                + ",\"powerGenerationW\":" + snapshot.powerGenerationW()
                + ",\"powerDemandW\":" + snapshot.powerDemandW()
                + ",\"storedEnergyJ\":" + snapshot.storedEnergyJ()
                + ",\"powerTripped\":" + snapshot.powerTripped()
                + ",\"colonyStress\":" + snapshot.colonyStress()
                + ",\"unlockedResearchCount\":" + snapshot.unlockedResearchCount()
                + ",\"activeConstructionCount\":" + snapshot.activeConstructionCount()
                + "}";
            writeJson(exchange, 200, body);
        }
    }
}
