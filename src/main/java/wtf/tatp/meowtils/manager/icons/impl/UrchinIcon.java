package wtf.tatp.meowtils.manager.icons.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.icons.IconProvider;
import wtf.tatp.meowtils.module.hypixel.Stats;
import wtf.tatp.meowtils.module.meowtils.Icons;
import wtf.tatp.meowtils.stats.StatsContainer;
import wtf.tatp.meowtils.util.PlayerUtil;

public class UrchinIcon implements IconProvider {

    private static final String DARK_RED_SPIKE = EnumChatFormatting.DARK_RED + "✹ " + EnumChatFormatting.RESET;
    private static final String RED_SPIKE = EnumChatFormatting.RED + "✹ " + EnumChatFormatting.RESET;
    private static final String PURPLE_SPIKE = EnumChatFormatting.DARK_PURPLE + "✹ " + EnumChatFormatting.RESET;
    private static final String GRAY_SPIKE = EnumChatFormatting.GRAY + "✹ " + EnumChatFormatting.RESET;
    private static final String DARK_GRAY_SPIKE = EnumChatFormatting.DARK_GRAY + "✹ " + EnumChatFormatting.RESET;
    private static final String YELLOW_STAR = EnumChatFormatting.YELLOW + "✴ " + EnumChatFormatting.RESET;
    private static final String YELLOW_INFO = EnumChatFormatting.YELLOW + "ⓘ " + EnumChatFormatting.RESET;

    @Override
    public String getPrefix(GameProfile profile, boolean tablist, boolean nametag) {
        Stats s = Module.get(Stats.class);
        if (s == null || !s.enabled) return "";
        if (!s.urchinIcon) return "";
        if (!s.urchinApi) return "";
        if (nametag && !Icons.displayInNametag()) return "";
        if (tablist && !Icons.displayInTab()) return "";
        if (profile == null || profile.getName() == null || PlayerUtil.isNicked(profile)) return "";
        if (profile.getName().equals(Minecraft.getMinecraft().thePlayer.getName()) && s.urchinIgnoreSelf) return "";
        if (profile.getId().version() == 2) return "";

        StatsContainer stats = Stats.getCachedUrchin(profile.getName());
        if (stats == null) {
            Stats.requestUrchin(profile.getName(), statsContainer -> {});
            return "";
        }

        if (stats.urchinTags == null || stats.urchinTags.isEmpty()) return "";

        StringBuilder urchinTag = new StringBuilder();
        for (StatsContainer.UrchinTag tag : stats.urchinTags) {
            if (tag == null || tag.type == null) continue;
            urchinTag.append(getTag(tag));
        }

        return urchinTag.toString();
    }

    private String getTag(StatsContainer.UrchinTag tag) {
        String blatantTag = tag.type.equals("blatant_cheater") ? DARK_RED_SPIKE : "";
        String confirmedTag = tag.type.equals("confirmed_cheater") ? PURPLE_SPIKE : "";
        String closetTag = tag.type.equals("closet_cheater") ? YELLOW_STAR : "";
        String sniperTag = tag.type.equals("sniper") ? RED_SPIKE : "";
        String infoTag = tag.type.equals("info") ? GRAY_SPIKE : "";
        String accountTag = tag.type.equals("account") ? DARK_GRAY_SPIKE : "";
        String cautionTag = tag.type.equals("caution") ? YELLOW_INFO : "";

        return blatantTag + confirmedTag + closetTag + sniperTag + infoTag + accountTag + cautionTag;
    }
}