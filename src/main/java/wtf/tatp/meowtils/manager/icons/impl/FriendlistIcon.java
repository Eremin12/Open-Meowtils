package wtf.tatp.meowtils.manager.icons.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.icons.IconProvider;
import wtf.tatp.meowtils.manager.lists.FriendlistManager;
import wtf.tatp.meowtils.module.meowtils.Icons;

public class FriendlistIcon implements IconProvider {

    private static final String ICON = EnumChatFormatting.GOLD + "✮ " + EnumChatFormatting.RESET;

    @Override
    public String getPrefix(GameProfile profile, boolean tablist, boolean nametag) {
        Icons icons = Module.get(Icons.class);
        if (icons == null || !icons.friendIcon) return "";
        if (nametag && !Icons.displayInNametag()) return "";
        if (tablist && !Icons.displayInTab()) return "";

        boolean friendlisted = (FriendlistManager.isFriendlisted(profile.getId().toString()) || FriendlistManager.isFriendlisted(profile.getName()));

        return friendlisted ? ICON : "";
    }
}