package wtf.tatp.meowtils.module.bedwars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
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
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class ArmorAlerts extends Module {
    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean pingSound = true;
    @Config
    public boolean chain = true;
    @Config
    public boolean iron = true;
    @Config
    public boolean diamond = true;
    @Config
    public String alertType = "Chat";

    private static int tickCounter = 0;
    private static final Map<UUID, ItemArmor.ArmorMaterial> ALERTED_ARMOR = new HashMap<>();
    public final Minecraft mc = Minecraft.getMinecraft();

    public ArmorAlerts() {
        super("ArmorAlerts", Module.Category.Bedwars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts you when players buy an armor upgrade.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addToggle(new ToggleValue("Ping sound", "pingSound", this));
        addCheck(new CheckValue("§7Chainmail Armor", "chain", this));
        addCheck(new CheckValue("Iron Armor", "iron", this));
        addCheck(new CheckValue("§bDiamond Armor", "diamond", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST)
            return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive())
            return;
        tickCounter++;
        if (tickCounter < 20)
            return;
        tickCounter = 0;
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == null || player.ticksExisted < 60 || player == mc.thePlayer || TeamUtil.isBot(player) || TeamUtil.isTeam(player) || TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName()))
                continue;
            checkArmor(player);
        }
    }

    private void checkArmor(EntityPlayer player) {
        ItemStack leggings = player.inventoryContainer.getInventory().get(1);
        if (leggings == null || !(leggings.getItem() instanceof ItemArmor))
            return;
        ItemArmor armor = (ItemArmor) leggings.getItem();
        ItemArmor.ArmorMaterial material = armor.getArmorMaterial();
        if (material == ItemArmor.ArmorMaterial.LEATHER)
            return;
        if (material == ItemArmor.ArmorMaterial.CHAIN && !this.chain)
            return;
        if (material == ItemArmor.ArmorMaterial.IRON && !this.iron)
            return;
        if (material == ItemArmor.ArmorMaterial.DIAMOND && !this.diamond)
            return;
        ItemArmor.ArmorMaterial lastMaterial = ALERTED_ARMOR.get(player.getUniqueID());
        int current = getPriority(material);
        int last = getPriority(lastMaterial);
        if (current <= last)
            return;
        ALERTED_ARMOR.put(player.getUniqueID(), material);
        alert(player, material);
    }

    private int getPriority(ItemArmor.ArmorMaterial material) {
        if (material == null)
            return 0;
        switch (material) {
            case CHAIN:
                return 1;
            case IRON:
                return 2;
            case DIAMOND:
                return 3;
            default:
                return 0;
        }
    }

    private void alert(EntityPlayer player, ItemArmor.ArmorMaterial material) {
        String armorName;
        EnumChatFormatting color;
        String name = NameUtil.getTabDisplayName(player.getName());
        switch (material) {
            case CHAIN:
                armorName = "Chainmail Armor";
                color = EnumChatFormatting.DARK_GRAY;
                break;
            case IRON:
                armorName = "Iron Armor";
                color = EnumChatFormatting.WHITE;
                break;
            case DIAMOND:
                armorName = "Diamond Armor";
                color = EnumChatFormatting.AQUA;
                break;
            default:
                return;
        }
        String text = name + EnumChatFormatting.GRAY + " purchased " + color + armorName;
        if (!this.alertType.equals("Notification")) {
            Meowtils.addMessage(text);
        }
        if (!this.alertType.equals("Chat")) {
            NotificationManager.show("ArmorAlerts", text, NotificationManager.Type.ALERT, 1500L);
        }
        if (this.pingSound) {
            Util.playSound(Util.Sound.PING, 100);
        }
        PartyNotifier.armorAlerts(player.getName(), armorName);
    }

    public void onReset() {
        ALERTED_ARMOR.clear();
    }
}