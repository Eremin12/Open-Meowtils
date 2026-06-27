package wtf.tatp.meowtils.manager.icons;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;

public class IconManager {

    private static final List<IconProvider> PROVIDERS = new ArrayList<>();

    public static void register(IconProvider provider) {
        PROVIDERS.add(provider);
    }

    public static String buildPrefix(GameProfile profile, boolean tablist, boolean nametag) {
        StringBuilder result = new StringBuilder();
        for (IconProvider provider : PROVIDERS) {
            result.append(provider.getPrefix(profile, tablist, nametag));
        }
        return result.toString();
    }

    public static String buildSuffix(GameProfile profile, boolean tablist, boolean nametag) {
        StringBuilder result = new StringBuilder();
        for (IconProvider provider : PROVIDERS) {
            result.append(provider.getSuffix(profile, tablist, nametag));
        }
        return result.toString();
    }
}