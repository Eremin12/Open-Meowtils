package wtf.tatp.meowtils.module.skywars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class EquipAlerts extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean sound = false;
    @Config
    public boolean helmet = true;
    @Config
    public boolean chestplate = true;
    @Config
    public boolean leggings = true;
    @Config
    public boolean boots = true;
    @Config
    public String alertType = "Chat";

    private static final Map<UUID, ItemStack[]> LAST_ARMOR = new HashMap<>();

    public EquipAlerts() {
        super("EquipAlerts", Module.Category.Skywars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts when a player equips selected armor piece.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
        addCheck(new CheckValue("§bDiamond Helmet", "helmet", this));
        addCheck(new CheckValue("§bDiamond Chestplate", "chestplate", this));
        addCheck(new CheckValue("§bDiamond Leggings", "leggings", this));
        addCheck(new CheckValue("§bDiamond Boots", "boots", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Skywars.GAME.isNotActive()) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == this.mc.thePlayer ||
                    TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName()))
                continue;

            ItemStack[] currentArmor = player.inventory.armorInventory.clone();
            ItemStack[] previousArmor = LAST_ARMOR.get(player.getUniqueID());

            if (previousArmor != null) {
                for (int i = 0; i < currentArmor.length; i++) {
                    ItemStack before = previousArmor[i];
                    ItemStack now = currentArmor[i];

                    if (!ItemStack.areItemStacksEqual(before, now) && now != null) {
                        String armorName = allowedArmor(now.getItem());
                        if (armorName != null) {
                            String text = NameUtil.getTabDisplayName(player.getName()) + EnumChatFormatting.GRAY + " equipped " + EnumChatFormatting.DARK_AQUA + armorName;

                            if (!this.alertType.equals("Notification")) {
                                Meowtils.addMessage(text);
                            }

                            if (!this.alertType.equals("Chat")) {
                                NotificationManager.show("EquipAlerts", text, NotificationManager.Type.ALERT, 1500L);
                            }

                            if (this.sound) {
                                Util.playSound(Util.Sound.PING_MEDIUM, 100);
                            }
                        }
                    }
                }
            }
            LAST_ARMOR.put(player.getUniqueID(), currentArmor);
        }
    }

    private String allowedArmor(Item item) {
        if (item == Items.diamond_helmet && this.helmet) return "Diamond Helmet";
        if (item == Items.diamond_chestplate && this.chestplate) return "Diamond Chestplate";
        if (item == Items.diamond_leggings && this.leggings) return "Diamond Leggings";
        if (item == Items.diamond_boots && this.boots) return "Diamond Boots";
        return null;
    }

    @Override
    public void onReset() {
        LAST_ARMOR.clear();
    }
}