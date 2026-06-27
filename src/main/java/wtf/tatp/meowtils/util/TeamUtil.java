package wtf.tatp.meowtils.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.lists.FriendlistManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.module.meowtils.Teams;

public class TeamUtil {

    private static final Map<UUID, Long> TAB_JOIN_TIMES = new HashMap<>();
    private static final Set<UUID> CURRENT_TAB_LIST = new HashSet<>();
    private static final List<EntityPlayer> CACHED_TEAMMATES = new ArrayList<>();

    public static boolean ignoreFriends(String uuidOrName) {
        Teams teams = Module.get(Teams.class);
        return (FriendlistManager.isFriendlisted(uuidOrName) && teams != null && teams.ignoreFriends);
    }

    public static boolean isTeam(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || player == null) return false;

        Teams teams = Module.get(Teams.class);
        if (teams == null || !teams.ignoreTeam) return false;
        if (player == mc.thePlayer) return false;
        if (player.capabilities.disableDamage) return true;

        try {
            if (CACHED_TEAMMATES.contains(player)) return true;
            if (mc.thePlayer.isOnSameTeam(player)) return true;

            if (Bedwars.ALL.isActive() && sameArmorColor(mc.thePlayer, player)) {
                CACHED_TEAMMATES.add(player);
                return true;
            }

            String selfFormatted = (mc.thePlayer.getTeam() != null) ? mc.thePlayer.getTeam().formatString(mc.thePlayer.getName()) : mc.thePlayer.getName();
            String targetFormatted = (player.getTeam() != null) ? player.getTeam().formatString(player.getName()) : player.getName();

            String selfColor = getMostFrequentColor(selfFormatted);
            String targetColor = getMostFrequentColor(targetFormatted);

            if (selfColor != null && selfColor.equals(targetColor)) {
                CACHED_TEAMMATES.add(player);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static String getMostFrequentColor(String text) {
        if (text == null) return null;

        Map<String, Integer> colorCounts = new HashMap<>();

        for (int i = 0; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            if (c == '\u00a7') {
                char code = text.charAt(i + 1);
                if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f')) {
                    String currentColor = "\u00a7" + code;
                    colorCounts.put(currentColor, colorCounts.getOrDefault(currentColor, 0) + 1);
                }
            }
        }
        return colorCounts.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }

    private static Integer getArmorColor(EntityPlayer player) {
        if (player == null) return null;

        ItemStack stack = player.getEquipmentInSlot(2);
        if (stack == null || !(stack.getItem() instanceof ItemArmor)) return null;

        ItemArmor armor = (ItemArmor) stack.getItem();
        if (armor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER) return null;
        return armor.getColor(stack);
    }

    private static boolean sameArmorColor(EntityPlayer a, EntityPlayer b) {
        Integer colorA = getArmorColor(a);
        Integer colorB = getArmorColor(b);

        return (colorA != null && colorA.equals(colorB));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) {
            return;
        }

        Teams teams = Module.get(Teams.class);
        if (teams != null && teams.ignoreBotMode.equals("Universal")) {
            updateTabList();
        }
    }

    private static void updateTabList() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() == null) return;

        Set<UUID> newTabList = new HashSet<>();
        for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
            newTabList.add(info.getGameProfile().getId());
        }

        long now = System.currentTimeMillis();

        for (UUID id : newTabList) {
            TAB_JOIN_TIMES.putIfAbsent(id, now);
        }

        TAB_JOIN_TIMES.keySet().removeIf(id -> !newTabList.contains(id));

        CURRENT_TAB_LIST.clear();
        CURRENT_TAB_LIST.addAll(newTabList);
    }

    public static boolean isBot(EntityPlayer player) {
        Teams t = Module.get(Teams.class);
        if (t == null) return true;
        if (player == null) return true;
        if (t.ignoreBotMode.equals("None")) return false;
        if (player == Minecraft.getMinecraft().thePlayer) return false;

        UUID id = player.getUniqueID();
        boolean useTabCheck = (t.ignoreBotMode.equals("Universal") || (t.ignoreBotMode.equals("Dynamic") && Server.HYPIXEL.isNotActive()));
        boolean useUuidCheck = (t.ignoreBotMode.equals("Hypixel") || (t.ignoreBotMode.equals("Dynamic") && Server.HYPIXEL.isActive()));

        if (useTabCheck) {
            if (!CURRENT_TAB_LIST.contains(id)) return true;

            Long joinTime = TAB_JOIN_TIMES.get(id);
            if (joinTime == null) return true;

            long timeInTab = System.currentTimeMillis() - joinTime;
            return (timeInTab < 10000L);
        }

        if (useUuidCheck) {
            return (id.version() != 1 && id.version() != 4);
        }

        return true;
    }

    public static void reset() {
        CURRENT_TAB_LIST.clear();
        TAB_JOIN_TIMES.clear();
        CACHED_TEAMMATES.clear();
    }
}