package wtf.tatp.meowtils.stats.util;

import net.minecraft.util.EnumChatFormatting;

public class SkywarsStatsUtil {

    public static EnumChatFormatting getKillsColor(int kills) {
        if (kills == 0) return EnumChatFormatting.BLUE;
        if (kills < 1000) return EnumChatFormatting.GRAY;
        if (kills < 2000) return EnumChatFormatting.WHITE;
        if (kills < 3000) return EnumChatFormatting.GREEN;
        if (kills < 5000) return EnumChatFormatting.DARK_GREEN;
        if (kills < 15000) return EnumChatFormatting.YELLOW;
        if (kills < 30000) return EnumChatFormatting.GOLD;
        if (kills < 50000) return EnumChatFormatting.RED;
        if (kills < 70000) return EnumChatFormatting.DARK_RED;
        if (kills < 100000) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }

    public static EnumChatFormatting getKdrColor(double kdr) {
        if (kdr == 0.0D) return EnumChatFormatting.BLUE;
        if (kdr < 0.3D) return EnumChatFormatting.GRAY;
        if (kdr < 0.9D) return EnumChatFormatting.WHITE;
        if (kdr < 1.5D) return EnumChatFormatting.GREEN;
        if (kdr < 2.0D) return EnumChatFormatting.DARK_GREEN;
        if (kdr < 3.0D) return EnumChatFormatting.YELLOW;
        if (kdr < 6.0D) return EnumChatFormatting.GOLD;
        if (kdr < 9.0D) return EnumChatFormatting.RED;
        if (kdr < 15.0D) return EnumChatFormatting.DARK_RED;
        if (kdr < 30.0D) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }

    public static EnumChatFormatting getWinsColor(int wins) {
        if (wins == 0) return EnumChatFormatting.BLUE;
        if (wins < 100) return EnumChatFormatting.GRAY;
        if (wins < 500) return EnumChatFormatting.WHITE;
        if (wins < 1000) return EnumChatFormatting.GREEN;
        if (wins < 1500) return EnumChatFormatting.DARK_GREEN;
        if (wins < 3000) return EnumChatFormatting.YELLOW;
        if (wins < 5000) return EnumChatFormatting.GOLD;
        if (wins < 8000) return EnumChatFormatting.RED;
        if (wins < 10000) return EnumChatFormatting.DARK_RED;
        return EnumChatFormatting.LIGHT_PURPLE;
    }

    public static EnumChatFormatting getWlrColor(double wlr) {
        if (wlr == 0.0D) return EnumChatFormatting.BLUE;
        if (wlr < 0.1D) return EnumChatFormatting.GRAY;
        if (wlr < 0.5D) return EnumChatFormatting.WHITE;
        if (wlr < 1.0D) return EnumChatFormatting.GREEN;
        if (wlr < 2.0D) return EnumChatFormatting.DARK_GREEN;
        if (wlr < 3.0D) return EnumChatFormatting.YELLOW;
        if (wlr < 4.0D) return EnumChatFormatting.GOLD;
        if (wlr < 7.0D) return EnumChatFormatting.RED;
        if (wlr < 10.0D) return EnumChatFormatting.DARK_RED;
        if (wlr < 15.0D) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }
}