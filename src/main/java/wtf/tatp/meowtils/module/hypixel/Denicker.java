package wtf.tatp.meowtils.module.hypixel;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.MegaWalls;
import wtf.tatp.meowtils.manager.session.MurderMystery;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.stats.util.ChatStats;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.PlayerUtil;
import wtf.tatp.meowtils.util.TeamUtil;

public class Denicker extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean message = true;
    @Config
    public boolean icon = true;
    @Config
    public String iconMode = "Normal";
    @Config
    public String iconColor = "§5Dark Purple";
    @Config
    public boolean ignoreTeam = false;

    private static final Set<String> PARSED = new HashSet<>();

    public Denicker() {
        super("Denicker", Module.Category.Hypixel);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Tells you the name of a nicked player if they are using their real skin.");
        addToggle(new ToggleValue("Ignore teammates", "ignoreTeam", this));
        addToggle(new ToggleValue("Nick message", "message", this));
        addToggle(new ToggleValue("Nick tag", "icon", this));
        addMode(new ModeValue("Tag", Arrays.asList("Normal", "Asterisk", "Text"), "iconMode", this));
        addMode(new ModeValue("Color", Arrays.asList("§5Dark Purple", "§4Dark Red", "§1Dark Blue", "§3Dark Aqua"), "iconColor", this));
    }

    public static String getIcon() {
        Denicker d = Module.get(Denicker.class);
        String mode = (d != null) ? d.iconMode : "Normal";

        switch (mode) {
            case "Normal":
                return getColor().toString() + EnumChatFormatting.BOLD + " ✧";
            case "Asterisk":
                return getColor().toString() + EnumChatFormatting.BOLD + " *";
            case "Text":
                return getColor() + " [NICKED]";
        }
        return "";
    }

    private static EnumChatFormatting getColor() {
        Denicker d = Module.get(Denicker.class);
        String mode = (d != null) ? d.iconColor : "";
        if (mode.contains("Dark Purple")) return EnumChatFormatting.DARK_PURPLE;
        if (mode.contains("Dark Red")) return EnumChatFormatting.DARK_RED;
        if (mode.contains("Dark Blue")) return EnumChatFormatting.DARK_BLUE;
        if (mode.contains("Dark Aqua")) return EnumChatFormatting.DARK_AQUA;
        return EnumChatFormatting.WHITE;
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (MegaWalls.GAME.isActive()) return;
        if (MurderMystery.ALL.isActive()) return;

        for (NetworkPlayerInfo info : this.mc.getNetHandler().getPlayerInfoMap()) {
            GameProfile profile = info.getGameProfile();
            String name = profile.getName();
            String displayName = (info.getDisplayName() != null) ? info.getDisplayName().getUnformattedText() : name;
            EntityPlayer player = this.mc.theWorld.getPlayerEntityByUUID(profile.getId());

            if (!PlayerUtil.isNicked(profile)) {
                continue;
            }
            if (this.ignoreTeam && player != null && TeamUtil.isTeam(player)) {
                continue;
            }
            if (PARSED.contains(name)) {
                continue;
            }

            Pair<String, String> skinData = getSkinData(profile);

            if (skinData != null) {
                String skinHash = skinData.getKey();
                String realName = skinData.getValue();

                PARSED.add(name);
                alert(realName, ColorUtil.unformattedText(displayName), skinHash);
            }
        }
    }

    private static void alert(String name, String displayName, String hash) {
        Denicker d = Module.get(Denicker.class);
        if (d == null) return;
        String localPlayer = Minecraft.getMinecraft().thePlayer.getName();

        if (nicks.contains(hash)) {
            if (d.message) {
                Meowtils.addMessage(EnumChatFormatting.RED + displayName + EnumChatFormatting.DARK_PURPLE + " is nicked.");
                PartyNotifier.denicker(displayName, "", false);
            }
        } else if (d.message) {
            if (name.equals(localPlayer)) return;
            Meowtils.addMessage(EnumChatFormatting.GOLD + name + EnumChatFormatting.DARK_PURPLE + " is nicked as " + EnumChatFormatting.RED + displayName + EnumChatFormatting.DARK_PURPLE + ".");

            Stats stats = Module.get(Stats.class);
            if (stats != null && stats.enabled && stats.autoCheck) {
                if (Bedwars.GAME.isActive()) {
                    ChatStats.showBedwarsStats(name, false, true);
                } else if (Skywars.GAME.isActive()) {
                    ChatStats.showSkywarsStats(name, false, true);
                }
            }
            PartyNotifier.denicker(displayName, name, true);
        }
    }

    private static Pair<String, String> getSkinData(GameProfile profile) {
        Property property = (Property) Iterables.getFirst(profile.getProperties().get("textures"), null);
        if (property == null) return null;

        try {
            String decoded = new String(Base64.getDecoder().decode(property.getValue()), StandardCharsets.UTF_8);
            JsonObject obj = new JsonParser().parse(decoded).getAsJsonObject();

            if (!obj.has("textures")) return null;
            JsonObject textures = obj.getAsJsonObject("textures");

            if (!textures.has("SKIN")) return null;
            JsonObject skin = textures.getAsJsonObject("SKIN");

            if (!skin.has("url")) return null;
            String url = skin.get("url").getAsString();
            String hash = url.substring(url.lastIndexOf('/') + 1);

            String realName = obj.has("profileName") ? obj.get("profileName").getAsString() : "";

            return new Pair<>(hash, realName);
        } catch (Exception e) {
            return null;
        }
    }

    private static class Pair<K, V> {
        private final K key;
        private final V value;

        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private K getKey() {
            return this.key;
        }

        private V getValue() {
            return this.value;
        }
    }

    @Override
    public void onReset() {
        PARSED.clear();
    }

    static HashSet<String> nicks = new HashSet<>(Arrays.asList(
            "4c7b0468044bfecacc43d00a3a69335a834b73937688292c20d3988cae58248d",
            "3b60a1f6d562f52aaebbf1434f1de147933a3affe0e764fa49ea057536623cd3"
            // ... 其余 hash 值省略，保持原数组不变
    ));
}