package wtf.tatp.meowtils.manager.icons.impl;

import com.mojang.authlib.GameProfile;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.icons.IconProvider;
import wtf.tatp.meowtils.module.hypixel.Denicker;
import wtf.tatp.meowtils.util.PlayerUtil;

public class NickIcon implements IconProvider {

    @Override
    public String getSuffix(GameProfile profile, boolean tablist, boolean nametag) {
        Denicker denicker = Module.get(Denicker.class);
        if (denicker == null || !denicker.enabled) return "";
        if (!denicker.icon) return "";

        return PlayerUtil.isNicked(profile) ? Denicker.getIcon() : "";
    }
}