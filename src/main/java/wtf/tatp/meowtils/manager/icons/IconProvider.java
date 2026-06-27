package wtf.tatp.meowtils.manager.icons;

import com.mojang.authlib.GameProfile;

public interface IconProvider {

    default String getPrefix(GameProfile profile, boolean tablist, boolean nametag) {
        return "";
    }

    default String getSuffix(GameProfile profile, boolean tablist, boolean nametag) {
        return "";
    }
}