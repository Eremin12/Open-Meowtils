package wtf.tatp.meowtils.module.antisnipe;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.EntityJoinWorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.hypixel.Stats;
import wtf.tatp.meowtils.stats.util.ChatStats;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.PlayerUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class SniperWarning extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean sound = true;
    @Config
    public boolean checkGear = true;
    @Config
    public boolean checkName = true;
    @Config
    public boolean checkStats = false;
    @Config
    public boolean fetchStats = false;

    private static final Map<String, Boolean> SNIPER_ALERTED = new HashMap<>();
    private static int tickCounter = 0;

    private static final String[] SNIPER_NAMES = new String[] {
            "mcalt_", "mcalts_", "hassalt_", "dogalt_", "mal_", "bym_", "jy6_", "lf_", "wg_",
            "ggnekito", "dahai_", "tzi", "nicegen", "opalalts", "msmc", "myau", "vape", "snipe",
            "nicealts", "rave", "alt", "client", "hack", "hax", "fernan", "watchdog", "anticheat"
    };

    public SniperWarning() {
        super("SniperWarning", Module.Category.Antisnipe);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Warns you of certain players that may be snipers.");
        addToggle(new ToggleValue("Fetch stats", "fetchStats", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
        addCheck(new CheckValue("Check gear", "checkGear", this));
        addCheck(new CheckValue("Check name", "checkName", this));
        addCheck(new CheckValue("Check stats", "checkStats", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) {
            return;
        }
        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        if (this.checkGear && Bedwars.GAME.isActive()) {
            for (EntityPlayer player : this.mc.theWorld.playerEntities) {
                checkGear(player);
            }
        }

        if (this.checkName) {
            for (EntityPlayer player : this.mc.theWorld.playerEntities) {
                checkName(player);
            }
        }
    }

    @EventTarget
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() == null) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity() == this.mc.thePlayer) return;

        Stats statsModule = Module.get(Stats.class);
        if (statsModule == null || !statsModule.enabled) return;
        if (Bedwars.GAME.isNotActive()) return;
        if (!this.checkStats) return;

        String name = event.getEntity().getName();
        EntityPlayer player = (EntityPlayer) event.getEntity();

        if (SNIPER_ALERTED.containsKey(player.getName())) {
            return;
        }

        Stats.getStats(name, stats -> {
            if (PlayerUtil.isNicked(PlayerUtil.getProfile(name))) return;
            if (TeamUtil.isTeam(player)) return;
            if (stats == null) return;

            if (stats.bedwars.clutchRatio >= 0.5D) {
                alert(name, "Clutch Ratio", this.sound);
            } else if (stats.bedwars.fkdr >= stats.bedwars.finals * 0.1D) {
                alert(name, "Stats", this.sound);
            }
        });
        SNIPER_ALERTED.put(player.getName(), true);
    }

    private void checkGear(EntityPlayer player) {
        if (player == null) return;
        if (player == this.mc.thePlayer) return;
        if (TeamUtil.isTeam(player)) return;
        if (TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName())) return;
        if (SNIPER_ALERTED.containsKey(player.getName())) return;

        boolean hasChain = false;
        boolean hasIronSword = false;

        for (ItemStack armor : player.inventory.armorInventory) {
            if (armor != null && armor.getItem() instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor) armor.getItem();
                if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.CHAIN) {
                    hasChain = true;
                    break;
                }
            }
        }

        if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSword) {
            ItemSword sword = (ItemSword) player.getHeldItem().getItem();
            if (sword.getToolMaterialName().equals("IRON")) {
                hasIronSword = true;
            }
        }

        if (hasChain && hasIronSword) {
            alert(player.getName(), "Iron Sword + Chainmail Armor", this.sound);
            SNIPER_ALERTED.put(player.getName(), true);
        }
    }

    private void checkName(EntityPlayer player) {
        if (player == null) return;
        if (TeamUtil.isTeam(player)) return;
        if (TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName())) return;
        if (SNIPER_ALERTED.containsKey(player.getName())) return;

        for (String sniper : SNIPER_NAMES) {
            if (player.getName().toLowerCase().contains(sniper.toLowerCase())) {
                alert(player.getName(), "Name", this.sound);
                SNIPER_ALERTED.put(player.getName(), true);
                return;
            }
        }
    }

    private void alert(String name, String reason, boolean sound) {
        Meowtils.addMessage(EnumChatFormatting.RED + "Warning: " + EnumChatFormatting.RESET +
                NameUtil.getTabDisplayName(name) + EnumChatFormatting.GRAY + " might be a sniper! " + EnumChatFormatting.DARK_GRAY + "(" + EnumChatFormatting.WHITE + reason + EnumChatFormatting.DARK_GRAY + ")");

        if (this.fetchStats) {
            if (Bedwars.GAME.isActive()) {
                ChatStats.showBedwarsStats(name, true, false);
            } else if (Skywars.GAME.isActive()) {
                ChatStats.showSkywarsStats(name, true, false);
            }
        }

        if (sound) {
            Util.playSound(Util.Sound.PING_DEEP, 100);
        }
    }

    @Override
    public void onReset() {
        SNIPER_ALERTED.clear();
    }
}