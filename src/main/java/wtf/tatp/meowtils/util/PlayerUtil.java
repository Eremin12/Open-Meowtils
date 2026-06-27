package wtf.tatp.meowtils.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PlayerUtil {

    public static boolean isNicked(GameProfile profile) {
        if (profile == null) return false;

        UUID uuid = profile.getId();
        if (uuid == null) return false;

        return (uuid.version() == 1);
    }

    public static GameProfile getProfile(String name) {
        for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equalsIgnoreCase(name)) {
                return info.getGameProfile();
            }
        }
        return null;
    }
}