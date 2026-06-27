package wtf.tatp.meowtils.util;

import java.util.function.Supplier;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.meowtils.Settings;

public class Prefix {

    private static Supplier<String> prefixSupplier = Prefix::meowtilsPrefix;

    public static String getPrefix() {
        return prefixSupplier.get();
    }

    public static void setPrefix(String prefix) {
        if (prefix == null) return;
        setSupplier(() -> prefix);
    }

    public static void resetPrefix() {
        prefixSupplier = Prefix::meowtilsPrefix;
    }

    private static void setSupplier(Supplier<String> supplier) {
        prefixSupplier = supplier;
    }

    private static String meowtilsPrefix() {
        Settings s = Module.get(Settings.class);
        if (s == null) {
            return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.BLUE + "M" + EnumChatFormatting.DARK_AQUA + "e" + EnumChatFormatting.AQUA + "o" + EnumChatFormatting.WHITE + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;
        }

        String prefixCase = s.lowerCase ? "m" : "M";
        String boldPrefixCase = s.lowerCase ? (EnumChatFormatting.BOLD + "m") : (EnumChatFormatting.BOLD + "M");

        switch (s.prefix) {
            case "Default":
                return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.BLUE + prefixCase + EnumChatFormatting.DARK_AQUA + "e" + EnumChatFormatting.AQUA + "o" + EnumChatFormatting.WHITE + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;

            case "Myau":
                return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.RED + prefixCase + EnumChatFormatting.GOLD + "e" + EnumChatFormatting.YELLOW + "o" + EnumChatFormatting.GREEN + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;

            case "Fire":
                return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.YELLOW + prefixCase + EnumChatFormatting.GOLD + "e" + EnumChatFormatting.RED + "o" + EnumChatFormatting.DARK_RED + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;

            case "Nebula":
                return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.DARK_RED + prefixCase + EnumChatFormatting.RED + "e" + EnumChatFormatting.LIGHT_PURPLE + "o" + EnumChatFormatting.DARK_PURPLE + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;

            case "Air":
                return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.AQUA + prefixCase + EnumChatFormatting.WHITE + "e" + EnumChatFormatting.GRAY + "o" + EnumChatFormatting.DARK_GRAY + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;

            case "Custom":
                return ColorUtil.getColorFromString(s.themeFirstBracket) + "[" +
                        ColorUtil.getColorFromString(s.themeM) + prefixCase +
                        ColorUtil.getColorFromString(s.themeE) + "e" +
                        ColorUtil.getColorFromString(s.themeO) + "o" +
                        ColorUtil.getColorFromString(s.themeW) + "w" +
                        ColorUtil.getColorFromString(s.themeSecondBracket) + "] " + EnumChatFormatting.WHITE;

            case "Short":
                return ColorUtil.getColorFromString(s.themeFirstBracket) + "[" +
                        ColorUtil.getColorFromString(s.themeM) + boldPrefixCase +
                        ColorUtil.getColorFromString(s.themeSecondBracket) + "] " + EnumChatFormatting.WHITE;
        }

        return EnumChatFormatting.GRAY + "[" + EnumChatFormatting.BLUE + prefixCase + EnumChatFormatting.DARK_AQUA + "e" + EnumChatFormatting.AQUA + "o" + EnumChatFormatting.WHITE + "w" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE;
    }
}