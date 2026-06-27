package wtf.tatp.meowtils.stats;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import wtf.tatp.meowtils.Meowtils;

public class StatsManager {

    private static final ArrayDeque<FetchTask> QUEUE = new ArrayDeque<>();
    private static final HashSet<String> QUEUED_KEYS = new HashSet<>();
    private static final ConcurrentHashMap<String, List<Callback>> PENDING_FETCHES = new ConcurrentHashMap<>();
    private static final int FETCH_TIMEOUT = 10000;
    private static boolean schedulerActive = false;
    private static final ScheduledExecutorService SCHEDULER;
    private static final ExecutorService FETCH_EXECUTOR;

    static {
        SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Meowtils-Stats-Scheduler");
            t.setDaemon(true);
            return t;
        });
        FETCH_EXECUTOR = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "Meowtils-Stats-Fetch");
            t.setDaemon(true);
            return t;
        });
    }

    public static void request(String name, StatsSource source, Callback callback) {
        if (name == null || source == null) {
            safeCallback(callback, null);
            return;
        }

        String lowerName = name.toLowerCase();
        String key = source.getId() + ":" + lowerName;

        StatsContainer cached = StatsCache.getValid(key);
        if (cached != null) {
            safeCallback(callback, cached);
            return;
        }

        if (!StatsCache.canRetry(key)) {
            safeCallback(callback, null);
            return;
        }

        synchronized (QUEUE) {
            if (PENDING_FETCHES.containsKey(key)) {
                if (callback != null) PENDING_FETCHES.get(key).add(callback);
                return;
            }

            if (callback != null) {
                PENDING_FETCHES.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(callback);
            }

            if (QUEUED_KEYS.add(key)) {
                QUEUE.add(new FetchTask(lowerName, key, source));
                if (!schedulerActive) {
                    schedulerActive = true;
                    SCHEDULER.execute(StatsManager::runNext);
                }
            }
        }
    }

    private static void runNext() {
        FetchTask t;
        synchronized (QUEUE) {
            t = QUEUE.poll();
            if (t == null) {
                schedulerActive = false;
                return;
            }
        }

        runFetch(t);
        SCHEDULER.schedule(StatsManager::runNext, t.source.getCooldown(), TimeUnit.MILLISECONDS);
    }

    private static void runFetch(FetchTask t) {
        CompletableFuture<StatsContainer> future = CompletableFuture.supplyAsync(() -> t.source.fetch(t.name), FETCH_EXECUTOR);

        ScheduledFuture<?> fallback = SCHEDULER.schedule(() -> future.cancel(true), FETCH_TIMEOUT, TimeUnit.MILLISECONDS);

        future.whenComplete((result, e) -> {
            fallback.cancel(false);
            synchronized (QUEUE) {
                QUEUED_KEYS.remove(t.key);
            }
            completedFetch(t.key, (e != null) ? null : result);
        });
    }

    private static void completedFetch(String key, StatsContainer stats) {
        if (stats != null) {
            StatsCache.put(key, stats);
        } else {
            StatsCache.markFailed(key);
        }
        notifyCallbacks(key, stats);
    }

    private static void notifyCallbacks(String key, StatsContainer stats) {
        List<Callback> callbacks;
        synchronized (QUEUE) {
            callbacks = PENDING_FETCHES.remove(key);
        }

        if (callbacks == null) return;
        for (Callback c : callbacks) {
            safeCallback(c, stats);
        }
    }

    private static void safeCallback(Callback c, StatsContainer stats) {
        if (c == null) return;
        Minecraft.getMinecraft().addScheduledTask(() -> {
            try {
                c.call(stats);
            } catch (Exception e) {
                Meowtils.error("Failed stats callback: " + e);
            }
        });
    }

    private static final class FetchTask {
        final String name;
        final String key;
        final StatsSource source;

        FetchTask(String name, String key, StatsSource source) {
            this.name = name;
            this.key = key;
            this.source = source;
        }
    }

    public interface Callback {
        void call(StatsContainer statsContainer);
    }
}