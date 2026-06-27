package wtf.tatp.meowtils.util.anticheat.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.antisnipe.AntiCheat;

public class LegitScaffoldCheck {

    private static final Map<UUID, Long> LAST_CROUCH_START = new HashMap<>();
    private static final Map<UUID, Long> LAST_CROUCH_END = new HashMap<>();
    private static final Map<UUID, Boolean> WAS_SNEAKING = new HashMap<>();
    private static final Map<UUID, Long> LAST_SWING_TICK = new HashMap<>();
    private static final Map<UUID, List<Integer>> CROUCH_DURATIONS = new HashMap<>();
    private static final Map<UUID, Long> LAST_FLAG_TICK = new HashMap<>();
    private static final Map<UUID, Boolean> FLAGGED = new HashMap<>();

    private static final long COOLDOWN_TICKS = 60L;
    private static final int THRESHOLD = 10;

    public void anticheatCheck(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        AntiCheat antiCheat = Module.get(AntiCheat.class);
        if (antiCheat == null || !antiCheat.legitScaffold || player == null || player == mc.thePlayer) return;

        UUID uuid = player.getUniqueID();
        long tick = mc.theWorld.getTotalWorldTime();

        trackCrouch(uuid, tick, player.isSneaking());
        trackSwing(uuid, tick, player.swingProgressInt);

        if (isScaffold(player)) {
            evaluate(uuid, tick);
        }
    }

    private void trackCrouch(UUID uuid, long tick, boolean currSneak) {
        boolean prevSneak = WAS_SNEAKING.getOrDefault(uuid, false);

        if (currSneak && !prevSneak) {
            LAST_CROUCH_START.put(uuid, tick);
        } else if (!currSneak && prevSneak) {
            long start = LAST_CROUCH_START.getOrDefault(uuid, tick - 1L);
            int duration = (int) (tick - start);
            LAST_CROUCH_END.put(uuid, tick);
            List<Integer> durations = CROUCH_DURATIONS.computeIfAbsent(uuid, k -> new ArrayList<>());
            durations.add(0, duration);
            if (durations.size() > 5) durations.remove(5);
        }
        WAS_SNEAKING.put(uuid, currSneak);
    }

    private void trackSwing(UUID uuid, long tick, int swingProgressInt) {
        if (swingProgressInt == 1) {
            LAST_SWING_TICK.put(uuid, tick);
        }
    }

    private boolean isScaffold(EntityPlayer player) {
        return (player.rotationPitch >= 60.0F && player.onGround && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof net.minecraft.item.ItemBlock);
    }

    private void evaluate(UUID uuid, long tick) {
        Long startObj = LAST_CROUCH_START.get(uuid);
        Long endObj = LAST_CROUCH_END.get(uuid);

        if (startObj == null || endObj == null) {
            FLAGGED.put(uuid, false);
            return;
        }

        int crouchDuration = (int) (endObj - startObj);
        boolean quickCrouch = (crouchDuration >= 1 && crouchDuration <= 2);

        long swing = LAST_SWING_TICK.getOrDefault(uuid, Long.MIN_VALUE);
        boolean swingTiming = (swing >= endObj && swing <= endObj + 3L && tick - swing <= 10L);

        List<Integer> durations = CROUCH_DURATIONS.getOrDefault(uuid, Collections.emptyList());
        boolean consistent = (durations.size() >= 3 && durations.get(0) <= 3 && durations.get(1) <= 3 && durations.get(2) <= 3);

        if (quickCrouch && swingTiming && consistent) {
            long lastFlag = LAST_FLAG_TICK.getOrDefault(uuid, 0L);
            if (tick - lastFlag >= COOLDOWN_TICKS) {
                FLAGGED.put(uuid, true);
                LAST_FLAG_TICK.put(uuid, tick);
                return;
            }
        }
        FLAGGED.put(uuid, false);
    }

    public boolean failedLegitScaffold(UUID uuid) {
        return FLAGGED.getOrDefault(uuid, false);
    }

    public void reset(UUID uuid) {
        FLAGGED.remove(uuid);
        LAST_CROUCH_START.remove(uuid);
        LAST_CROUCH_END.remove(uuid);
        WAS_SNEAKING.remove(uuid);
        LAST_SWING_TICK.remove(uuid);
        CROUCH_DURATIONS.remove(uuid);
        LAST_FLAG_TICK.remove(uuid);
    }
}