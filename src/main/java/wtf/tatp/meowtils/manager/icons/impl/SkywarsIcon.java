package wtf.tatp.meowtils.manager.icons.impl;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.icons.IconProvider;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.skywars.SkywarsAlerts;

public class SkywarsIcon implements IconProvider {

    private static final String DIAMOND_SWORD = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "⚔" + EnumChatFormatting.RESET;
    private static final String FIRE_SWORD = EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "⚔" + EnumChatFormatting.RESET;
    private static final String ENDER_PEARL = EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.BOLD + "❃" + EnumChatFormatting.RESET;
    private static final String STRENGTH_POTION = EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "⚒" + EnumChatFormatting.RESET;
    private static final String KNOCKBACK_SWORD = EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD + "⚡" + EnumChatFormatting.RESET;
    private static final String KNOCKBACK_ROD = EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "⚡" + EnumChatFormatting.RESET;

    @Override
    public String getSuffix(GameProfile profile, boolean tablist, boolean nametag) {
        SkywarsAlerts s = Module.get(SkywarsAlerts.class);
        if (s == null || !s.enabled) return "";
        if (Skywars.GAME.isNotActive()) return "";
        if (!s.nametagIcon) return "";

        UUID uuid = profile.getId();

        String sword = s.swordIcon ? (SkywarsAlerts.heldItem(uuid, Items.diamond_sword) ? DIAMOND_SWORD : (SkywarsAlerts.heldItem(uuid, Items.iron_sword) ? FIRE_SWORD : "")) : "";
        String knockback = s.knockbackIcon ? (SkywarsAlerts.heldItem(uuid, Items.fishing_rod) ? KNOCKBACK_ROD : (SkywarsAlerts.heldItem(uuid, Items.stone_sword) ? KNOCKBACK_SWORD : "")) : "";
        String pearl = s.pearlIcon ? (SkywarsAlerts.heldItem(uuid, Items.ender_pearl) ? ENDER_PEARL : "") : "";
        String strength = s.strengthIcon ? (SkywarsAlerts.heldItem(uuid, Items.potionitem) ? STRENGTH_POTION : "") : "";

        return " " + sword + knockback + pearl + strength;
    }
}