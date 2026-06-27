package wtf.tatp.meowtils.util.anticheat.checks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.antisnipe.AntiCheat;

public class KillauraCheck {

    private final Map<UUID, Integer> useItemTicks = new HashMap<>();
    private final Map<UUID, Integer> lastEatTicks = new HashMap<>();
    private final Map<UUID, Integer> violationLevels = new HashMap<>();
    private static final int EAT_TIMEOUT = 33;
    private static final int MIN_USE_TIME = 6;
    private boolean failedKillauraB = false;

    public void anticheatCheck(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        AntiCheat antiCheat = Module.get(AntiCheat.class);
        if (antiCheat == null || !antiCheat.killaura) return;
        if (player == mc.thePlayer || player.ridingEntity != null) return;

        UUID uuid = player.getUniqueID();
        long tick = mc.theWorld.getTotalWorldTime();
        ItemStack heldItem = player.getHeldItem();
        boolean isUsingItem = player.isUsingItem();
        boolean isConsumable = (heldItem != null && isConsumable(heldItem.getItem()));
        boolean isAttacking = (player.attackedAtYaw > 0);
        int useTime = this.useItemTicks.getOrDefault(uuid, 0);

        if (isUsingItem && isConsumable) {
            useTime++;
            this.useItemTicks.put(uuid, useTime);
        } else {
            if (useTime > 0) {
                this.lastEatTicks.put(uuid, (int) tick);
            }
            this.useItemTicks.put(uuid, 0);
        }

        int lastEatTick = this.lastEatTicks.getOrDefault(uuid, 0);
        int sinceLastEat = (int) (tick - lastEatTick);

        if (isAttacking && useTime > MIN_USE_TIME && sinceLastEat < EAT_TIMEOUT && isConsumable) {
            int vl = this.violationLevels.getOrDefault(uuid, 0) + 1;
            this.violationLevels.put(uuid, vl);

            if (vl >= 8) {
                this.failedKillauraB = true;
            }
        } else {
            int vl = this.violationLevels.getOrDefault(uuid, 0);
            if (vl > 0) this.violationLevels.put(uuid, vl - 1);
        }
    }

    public boolean failedKillauraB() {
        return this.failedKillauraB;
    }

    public void reset() {
        this.failedKillauraB = false;
        this.useItemTicks.clear();
        this.lastEatTicks.clear();
        this.violationLevels.clear();
    }

    private boolean isConsumable(Item item) {
        return (item instanceof net.minecraft.item.ItemFood || item instanceof net.minecraft.item.ItemPotion || item instanceof net.minecraft.item.ItemBucketMilk);
    }
}