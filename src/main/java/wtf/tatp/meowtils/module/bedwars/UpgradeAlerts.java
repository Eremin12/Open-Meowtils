package wtf.tatp.meowtils.module.bedwars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class UpgradeAlerts extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean sound = true;
    @Config
    public String alertType = "Chat";

    private static final Map<String, Set<String>> PLAYER_UPGRADES = new HashMap<>();
    private static final Map<String, Set<String>> TEAM_UPGRADES = new HashMap<>();
    private static int tickCounter = 0;

    public UpgradeAlerts() {
        super("UpgradeAlerts", Module.Category.Bedwars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts when a team buys a team upgrade.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;

        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            checkForEnchantments(player);
        }
    }

    private void checkForEnchantments(EntityPlayer player) {
        if (player == null || player.inventory == null) return;

        String playerName = player.getName();
        ItemStack heldItem = player.getHeldItem();
        ItemStack chestplate = player.inventory.armorInventory[2];
        Set<String> upgrades = PLAYER_UPGRADES.getOrDefault(playerName, new HashSet<>());

        if (player == this.mc.thePlayer) return;
        if (TeamUtil.isTeam(player)) return;
        if (TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName())) {
            return;
        }

        if (heldItem != null && heldItem.getItem() instanceof net.minecraft.item.ItemSword && heldItem.hasDisplayName()) {
            notifyUpgrade(playerName, "Sharpened Swords");
        }

        if (chestplate != null && chestplate.getItem() instanceof net.minecraft.item.ItemArmor && chestplate.hasDisplayName()) {
            notifyUpgrade(playerName, "Reinforced Armor");
        }

        PLAYER_UPGRADES.put(playerName, upgrades);
    }

    private void notifyUpgrade(String playerName, String upgrade) {
        if (!"NONE".equals(NameUtil.getTabDisplayName(playerName))) {
            ScorePlayerTeam playerTeam = this.mc.theWorld.getScoreboard().getPlayersTeam(playerName);
            if (playerTeam == null) return;

            String formattedName = ScorePlayerTeam.formatPlayerName(playerTeam, playerName);
            EnumChatFormatting colorCode = NameUtil.getNameColor(formattedName);
            String teamName = getFormattedTeamName(colorCode);
            Set<String> teamUpgrades = TEAM_UPGRADES.getOrDefault(teamName, new HashSet<>());

            if (teamName == null) return;
            if (teamUpgrades.contains(upgrade)) return;

            String text = teamName + EnumChatFormatting.GRAY + " purchased " + EnumChatFormatting.DARK_AQUA + upgrade;

            if (!this.alertType.equals("Notification")) {
                Meowtils.addMessage(text);
            }

            if (!this.alertType.equals("Chat")) {
                NotificationManager.show("UpgradeAlerts", text, NotificationManager.Type.ALERT, 1500L);
            }

            teamUpgrades.add(upgrade);
            TEAM_UPGRADES.put(teamName, teamUpgrades);

            PartyNotifier.upgradeAlerts(ColorUtil.unformattedText(teamName), upgrade);

            if (this.sound) {
                Util.playSound(Util.Sound.PING, 100);
            }
        }
    }

    private String getFormattedTeamName(EnumChatFormatting color) {
        switch (color) {
            case RED:
                return EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "Red Team";
            case BLUE:
                return EnumChatFormatting.BLUE.toString() + EnumChatFormatting.BOLD + "Blue Team";
            case GREEN:
                return EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD + "Green Team";
            case YELLOW:
                return EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + "Yellow Team";
            case AQUA:
                return EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "Aqua Team";
            case WHITE:
                return EnumChatFormatting.WHITE.toString() + EnumChatFormatting.BOLD + "White Team";
            case LIGHT_PURPLE:
                return EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.BOLD + "Pink Team";
            case DARK_GRAY:
                return EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.BOLD + "Gray Team";
        }
        return null;
    }

    @Override
    public void onReset() {
        TEAM_UPGRADES.clear();
        PLAYER_UPGRADES.clear();
    }
}