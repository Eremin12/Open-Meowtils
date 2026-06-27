package wtf.tatp.meowtils.manager.icons.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.icons.IconProvider;
import wtf.tatp.meowtils.manager.lists.BlacklistManager;
import wtf.tatp.meowtils.manager.lists.SafelistManager;
import wtf.tatp.meowtils.module.meowtils.Icons;

public class SafelistIcon implements IconProvider {

    private static final String CHECK_ICON = EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD + "✓ " + EnumChatFormatting.RESET;
    private static final String CHECK_ICON_BLUE = EnumChatFormatting.BLUE.toString() + EnumChatFormatting.BOLD + "✓ " + EnumChatFormatting.RESET;

    @Override
    public String getPrefix(GameProfile profile, boolean tablist, boolean nametag) {
        Icons icons = Module.get(Icons.class);
        if (icons == null || !icons.safelistIcon) return "";
        if (nametag && !Icons.displayInNametag()) return "";
        if (tablist && !Icons.displayInTab()) return "";

        Minecraft mc = Minecraft.getMinecraft();

        boolean blacklisted = (BlacklistManager.isBlacklisted(profile.getId().toString()) || BlacklistManager.isBlacklisted(profile.getName()));
        boolean safelisted = (SafelistManager.isSafelisted(profile.getId().toString()) || SafelistManager.isSafelisted(profile.getName()));
        boolean showIcon = (!blacklisted && safelisted);

        return (showIcon && profile.getId().equals(mc.thePlayer.getUniqueID())) ? CHECK_ICON_BLUE : (showIcon ? CHECK_ICON : "");
    }
}