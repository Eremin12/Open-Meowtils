package wtf.tatp.meowtils.util;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.EnumChatFormatting;

public class ColorUtil {

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color DARK_BLUE = new Color(0, 0, 170);
    public static final Color DARK_GREEN = new Color(0, 170, 0);
    public static final Color DARK_AQUA = new Color(0, 170, 170);
    public static final Color DARK_RED = new Color(170, 0, 0);
    public static final Color DARK_PURPLE = new Color(170, 0, 170);
    public static final Color GOLD = new Color(255, 170, 0);
    public static final Color GRAY = new Color(170, 170, 170);
    public static final Color DARK_GRAY = new Color(85, 85, 85);
    public static final Color BLUE = new Color(85, 85, 255);
    public static final Color GREEN = new Color(85, 255, 85);
    public static final Color AQUA = new Color(85, 255, 255);
    public static final Color RED = new Color(255, 85, 85);
    public static final Color LIGHT_PURPLE = new Color(255, 85, 255);
    public static final Color YELLOW = new Color(255, 255, 85);
    public static final Color WHITE = new Color(255, 255, 255);

    private static final int MAX_CACHE = 256;

    private static final Map<Integer, Color> COLOR_CACHE = new LinkedHashMap<Integer, Color>(256, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Color> eldest) {
            return (size() > MAX_CACHE);
        }
    };

    public static int rgb(int red, int green, int blue) {
        red &= 0xFF;
        green &= 0xFF;
        blue &= 0xFF;
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    public static int rgba(int red, int green, int blue, int alpha) {
        red &= 0xFF;
        green &= 0xFF;
        blue &= 0xFF;
        alpha &= 0xFF;
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static Color getColor(int red, int green, int blue) {
        int key = 0xFF000000 | red << 16 | green << 8 | blue;
        return COLOR_CACHE.computeIfAbsent(key, k -> new Color(red, green, blue));
    }

    public static Color getColor(int red, int green, int blue, int alpha) {
        int key = alpha << 24 | red << 16 | green << 8 | blue;
        return COLOR_CACHE.computeIfAbsent(key, k -> new Color(red, green, blue, alpha));
    }

    public static EnumChatFormatting getColorFromString(String colorName) {
        String color = colorName.toLowerCase();
        switch (color) {
            case "black": return EnumChatFormatting.BLACK;
            case "dark_blue": return EnumChatFormatting.DARK_BLUE;
            case "dark_green": return EnumChatFormatting.DARK_GREEN;
            case "dark_aqua": return EnumChatFormatting.DARK_AQUA;
            case "dark_red": return EnumChatFormatting.DARK_RED;
            case "dark_purple": return EnumChatFormatting.DARK_PURPLE;
            case "gold": return EnumChatFormatting.GOLD;
            case "gray": return EnumChatFormatting.GRAY;
            case "dark_gray": return EnumChatFormatting.DARK_GRAY;
            case "blue": return EnumChatFormatting.BLUE;
            case "green": return EnumChatFormatting.GREEN;
            case "aqua": return EnumChatFormatting.AQUA;
            case "red": return EnumChatFormatting.RED;
            case "light_purple": return EnumChatFormatting.LIGHT_PURPLE;
            case "yellow": return EnumChatFormatting.YELLOW;
            case "white": return EnumChatFormatting.WHITE;
        }
        return EnumChatFormatting.WHITE;
    }

    public static String unformattedText(String text) {
        if (text == null) return null;
        StringBuilder sb = new StringBuilder(text.length());

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u00a7' && i + 1 < text.length()) {
                i++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String convertFormatting(String text) {
        if (text == null) return null;
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && isFormatCode(chars[i + 1])) {
                chars[i] = '\u00a7';
            }
        }
        return new String(chars);
    }

    public static int convertOpacity(float opacity) {
        return Math.max(0, Math.min(255, Math.round(opacity * 2.55F)));
    }

    public static EnumChatFormatting getColorFromCode(char code) {
        switch (code) {
            case '0': return EnumChatFormatting.BLACK;
            case '1': return EnumChatFormatting.DARK_BLUE;
            case '2': return EnumChatFormatting.DARK_GREEN;
            case '3': return EnumChatFormatting.DARK_AQUA;
            case '4': return EnumChatFormatting.DARK_RED;
            case '5': return EnumChatFormatting.DARK_PURPLE;
            case '6': return EnumChatFormatting.GOLD;
            case '7': return EnumChatFormatting.GRAY;
            case '8': return EnumChatFormatting.DARK_GRAY;
            case '9': return EnumChatFormatting.BLUE;
            case 'a': return EnumChatFormatting.GREEN;
            case 'b': return EnumChatFormatting.AQUA;
            case 'c': return EnumChatFormatting.RED;
            case 'd': return EnumChatFormatting.LIGHT_PURPLE;
            case 'e': return EnumChatFormatting.YELLOW;
            case 'f': return EnumChatFormatting.WHITE;
        }
        return EnumChatFormatting.WHITE;
    }

    public static int getRGBFromFormatting(EnumChatFormatting color) {
        switch (color) {
            case BLACK: return BLACK.getRGB();
            case DARK_BLUE: return DARK_BLUE.getRGB();
            case DARK_GREEN: return DARK_GREEN.getRGB();
            case DARK_AQUA: return DARK_AQUA.getRGB();
            case DARK_RED: return DARK_RED.getRGB();
            case DARK_PURPLE: return DARK_PURPLE.getRGB();
            case GOLD: return GOLD.getRGB();
            case GRAY: return GRAY.getRGB();
            case DARK_GRAY: return DARK_GRAY.getRGB();
            case BLUE: return BLUE.getRGB();
            case GREEN: return GREEN.getRGB();
            case AQUA: return AQUA.getRGB();
            case RED: return RED.getRGB();
            case LIGHT_PURPLE: return LIGHT_PURPLE.getRGB();
            case YELLOW: return YELLOW.getRGB();
            case WHITE: return WHITE.getRGB();
        }
        return WHITE.getRGB();
    }

    public static Color getColorFromFormatting(EnumChatFormatting color) {
        switch (color) {
            case BLACK: return BLACK;
            case DARK_BLUE: return DARK_BLUE;
            case DARK_GREEN: return DARK_GREEN;
            case DARK_AQUA: return DARK_AQUA;
            case DARK_RED: return DARK_RED;
            case DARK_PURPLE: return DARK_PURPLE;
            case GOLD: return GOLD;
            case GRAY: return GRAY;
            case DARK_GRAY: return DARK_GRAY;
            case BLUE: return BLUE;
            case GREEN: return GREEN;
            case AQUA: return AQUA;
            case RED: return RED;
            case LIGHT_PURPLE: return LIGHT_PURPLE;
            case YELLOW: return YELLOW;
            case WHITE: return WHITE;
        }
        return WHITE;
    }

    public static boolean isFormatCode(char c) {
        c = Character.toLowerCase(c);
        switch (c) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
            case 'k': case 'l': case 'm': case 'n': case 'o':
            case 'r':
                return true;
        }
        return false;
    }

    public static boolean isColorTooBright(int r, int g, int b) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        double brightness = 0.2126D * r + 0.7152D * g + 0.0722D * b;
        return (brightness > 180.0D);
    }
}