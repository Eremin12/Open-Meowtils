package wtf.tatp.meowtils.stats.util;

import net.minecraft.util.EnumChatFormatting;

public class BedwarsStatsUtil {

    public static EnumChatFormatting getFKDRColor(double fkdr) {
        if (fkdr == 0.0D) return EnumChatFormatting.BLUE;
        if (fkdr < 1.0D) return EnumChatFormatting.GRAY;
        if (fkdr < 3.0D) return EnumChatFormatting.WHITE;
        if (fkdr < 5.0D) return EnumChatFormatting.GREEN;
        if (fkdr < 7.0D) return EnumChatFormatting.DARK_GREEN;
        if (fkdr < 10.0D) return EnumChatFormatting.YELLOW;
        if (fkdr < 20.0D) return EnumChatFormatting.GOLD;
        if (fkdr < 30.0D) return EnumChatFormatting.RED;
        if (fkdr < 50.0D) return EnumChatFormatting.DARK_RED;
        if (fkdr < 100.0D) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }

    public static EnumChatFormatting getWLRColor(double wlr) {
        if (wlr == 0.0D) return EnumChatFormatting.BLUE;
        if (wlr < 0.5D) return EnumChatFormatting.GRAY;
        if (wlr < 0.9D) return EnumChatFormatting.WHITE;
        if (wlr < 1.5D) return EnumChatFormatting.GREEN;
        if (wlr < 2.0D) return EnumChatFormatting.DARK_GREEN;
        if (wlr < 3.0D) return EnumChatFormatting.YELLOW;
        if (wlr < 6.0D) return EnumChatFormatting.GOLD;
        if (wlr < 9.0D) return EnumChatFormatting.RED;
        if (wlr < 15.0D) return EnumChatFormatting.DARK_RED;
        if (wlr < 30.0D) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }

    public static EnumChatFormatting getWSColor(int ws) {
        if (ws == 0) return EnumChatFormatting.BLUE;
        if (ws < 3) return EnumChatFormatting.GRAY;
        if (ws < 5) return EnumChatFormatting.WHITE;
        if (ws < 15) return EnumChatFormatting.GREEN;
        if (ws < 20) return EnumChatFormatting.DARK_GREEN;
        if (ws < 30) return EnumChatFormatting.YELLOW;
        if (ws < 40) return EnumChatFormatting.GOLD;
        if (ws < 50) return EnumChatFormatting.RED;
        if (ws < 80) return EnumChatFormatting.DARK_RED;
        return EnumChatFormatting.LIGHT_PURPLE;
    }

    public static EnumChatFormatting getFinalsColor(int finals) {
        if (finals == 0) return EnumChatFormatting.BLUE;
        if (finals < 1000) return EnumChatFormatting.GRAY;
        if (finals < 2000) return EnumChatFormatting.WHITE;
        if (finals < 3000) return EnumChatFormatting.GREEN;
        if (finals < 5000) return EnumChatFormatting.DARK_GREEN;
        if (finals < 15000) return EnumChatFormatting.YELLOW;
        if (finals < 30000) return EnumChatFormatting.GOLD;
        if (finals < 50000) return EnumChatFormatting.RED;
        if (finals < 70000) return EnumChatFormatting.DARK_RED;
        if (finals < 100000) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }

    public static EnumChatFormatting getClutchRatioColor(double cr) {
        if (cr == 0.0D) return EnumChatFormatting.BLUE;
        if (cr < 0.01D) return EnumChatFormatting.GRAY;
        if (cr < 0.05D) return EnumChatFormatting.WHITE;
        if (cr < 0.1D) return EnumChatFormatting.GREEN;
        if (cr < 0.2D) return EnumChatFormatting.DARK_GREEN;
        if (cr < 0.3D) return EnumChatFormatting.YELLOW;
        if (cr < 0.4D) return EnumChatFormatting.GOLD;
        if (cr < 0.5D) return EnumChatFormatting.RED;
        if (cr < 0.6D) return EnumChatFormatting.DARK_RED;
        if (cr < 0.7D) return EnumChatFormatting.LIGHT_PURPLE;
        return EnumChatFormatting.DARK_PURPLE;
    }

    public static String getFormattedLevel(int level) {
        LevelFormat format = getFormatForLevel(level);
        String lvl = String.valueOf(level);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(format.leftBracketColor).append("[");

        for (int i = 0; i < lvl.length(); i++) {
            EnumChatFormatting color = (i < format.digitColors.length) ? format.digitColors[i] : format.digitColors[format.digitColors.length - 1];
            stringBuilder.append(color).append(lvl.charAt(i));
        }

        if (format.starColor != null) {
            stringBuilder.append(format.starColor);
        }
        if (format.boldStar) {
            stringBuilder.append(EnumChatFormatting.BOLD);
        }
        stringBuilder.append(format.starIcon);
        stringBuilder.append(EnumChatFormatting.RESET);
        stringBuilder.append(format.rightBracketColor).append("]");

        return stringBuilder.toString() + EnumChatFormatting.RESET;
    }

    static class LevelFormat {
        EnumChatFormatting leftBracketColor;
        EnumChatFormatting[] digitColors;
        EnumChatFormatting starColor;
        String starIcon;
        EnumChatFormatting rightBracketColor;
        final boolean boldStar;

        LevelFormat(EnumChatFormatting leftBracketColor, EnumChatFormatting[] digitColors, EnumChatFormatting starColor, String starIcon, EnumChatFormatting rightBracketColor, boolean boldStar) {
            this.leftBracketColor = leftBracketColor;
            this.digitColors = digitColors;
            this.starColor = starColor;
            this.starIcon = starIcon;
            this.rightBracketColor = rightBracketColor;
            this.boldStar = boldStar;
        }
    }

    private static LevelFormat getFormatForLevel(int level) {
        if (level < 100) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.GRAY }, EnumChatFormatting.GRAY, "✫", EnumChatFormatting.GRAY, false);
        if (level < 200) return new LevelFormat(EnumChatFormatting.WHITE, new EnumChatFormatting[] { EnumChatFormatting.WHITE }, EnumChatFormatting.WHITE, "✫", EnumChatFormatting.WHITE, false);
        if (level < 300) return new LevelFormat(EnumChatFormatting.GOLD, new EnumChatFormatting[] { EnumChatFormatting.GOLD }, EnumChatFormatting.GOLD, "✫", EnumChatFormatting.GOLD, false);
        if (level < 400) return new LevelFormat(EnumChatFormatting.AQUA, new EnumChatFormatting[] { EnumChatFormatting.AQUA }, EnumChatFormatting.AQUA, "✫", EnumChatFormatting.AQUA, false);
        if (level < 500) return new LevelFormat(EnumChatFormatting.DARK_GREEN, new EnumChatFormatting[] { EnumChatFormatting.DARK_GREEN }, EnumChatFormatting.DARK_GREEN, "✫", EnumChatFormatting.DARK_GREEN, false);
        if (level < 600) return new LevelFormat(EnumChatFormatting.DARK_AQUA, new EnumChatFormatting[] { EnumChatFormatting.DARK_AQUA }, EnumChatFormatting.DARK_AQUA, "✫", EnumChatFormatting.DARK_AQUA, false);
        if (level < 700) return new LevelFormat(EnumChatFormatting.DARK_RED, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED }, EnumChatFormatting.DARK_RED, "✫", EnumChatFormatting.DARK_RED, false);
        if (level < 800) return new LevelFormat(EnumChatFormatting.LIGHT_PURPLE, new EnumChatFormatting[] { EnumChatFormatting.LIGHT_PURPLE }, EnumChatFormatting.LIGHT_PURPLE, "✫", EnumChatFormatting.LIGHT_PURPLE, false);
        if (level < 900) return new LevelFormat(EnumChatFormatting.BLUE, new EnumChatFormatting[] { EnumChatFormatting.BLUE }, EnumChatFormatting.BLUE, "✫", EnumChatFormatting.BLUE, false);
        if (level < 1000) return new LevelFormat(EnumChatFormatting.DARK_PURPLE, new EnumChatFormatting[] { EnumChatFormatting.DARK_PURPLE }, EnumChatFormatting.DARK_PURPLE, "✫", EnumChatFormatting.DARK_PURPLE, false);
        if (level < 1100) return new LevelFormat(EnumChatFormatting.RED, new EnumChatFormatting[] { EnumChatFormatting.GOLD, EnumChatFormatting.YELLOW, EnumChatFormatting.GREEN, EnumChatFormatting.AQUA }, EnumChatFormatting.LIGHT_PURPLE, "✫", EnumChatFormatting.DARK_PURPLE, false);
        if (level < 1200) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.WHITE }, EnumChatFormatting.GRAY, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1300) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.YELLOW }, EnumChatFormatting.GOLD, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1400) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.AQUA }, EnumChatFormatting.DARK_AQUA, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1500) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.GREEN }, EnumChatFormatting.DARK_GREEN, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1600) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.DARK_AQUA }, EnumChatFormatting.BLUE, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1700) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.RED }, EnumChatFormatting.DARK_RED, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1800) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.LIGHT_PURPLE }, EnumChatFormatting.DARK_PURPLE, "✪", EnumChatFormatting.GRAY, false);
        if (level < 1900) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.BLUE }, EnumChatFormatting.DARK_BLUE, "✪", EnumChatFormatting.GRAY, false);
        if (level < 2000) return new LevelFormat(EnumChatFormatting.GRAY, new EnumChatFormatting[] { EnumChatFormatting.DARK_PURPLE }, EnumChatFormatting.DARK_GRAY, "✪", EnumChatFormatting.GRAY, false);
        if (level < 2100) return new LevelFormat(EnumChatFormatting.DARK_GRAY, new EnumChatFormatting[] { EnumChatFormatting.GRAY, EnumChatFormatting.WHITE, EnumChatFormatting.WHITE, EnumChatFormatting.GRAY }, EnumChatFormatting.GRAY, "✪", EnumChatFormatting.DARK_GRAY, false);
        if (level < 2200) return new LevelFormat(EnumChatFormatting.WHITE, new EnumChatFormatting[] { EnumChatFormatting.WHITE, EnumChatFormatting.YELLOW, EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD }, EnumChatFormatting.GOLD, "⚝", EnumChatFormatting.GOLD, true);
        if (level < 2300) return new LevelFormat(EnumChatFormatting.GOLD, new EnumChatFormatting[] { EnumChatFormatting.GOLD, EnumChatFormatting.WHITE, EnumChatFormatting.WHITE, EnumChatFormatting.AQUA }, EnumChatFormatting.DARK_AQUA, "⚝", EnumChatFormatting.DARK_AQUA, true);
        if (level < 2400) return new LevelFormat(EnumChatFormatting.DARK_PURPLE, new EnumChatFormatting[] { EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.LIGHT_PURPLE, EnumChatFormatting.LIGHT_PURPLE, EnumChatFormatting.GOLD }, EnumChatFormatting.YELLOW, "⚝", EnumChatFormatting.YELLOW, true);
        if (level < 2500) return new LevelFormat(EnumChatFormatting.AQUA, new EnumChatFormatting[] { EnumChatFormatting.AQUA, EnumChatFormatting.WHITE, EnumChatFormatting.WHITE, EnumChatFormatting.GRAY }, EnumChatFormatting.GRAY, "⚝", EnumChatFormatting.DARK_GRAY, true);
        if (level < 2600) return new LevelFormat(EnumChatFormatting.WHITE, new EnumChatFormatting[] { EnumChatFormatting.WHITE, EnumChatFormatting.GREEN, EnumChatFormatting.GREEN, EnumChatFormatting.DARK_GREEN }, EnumChatFormatting.DARK_GREEN, "⚝", EnumChatFormatting.DARK_GREEN, true);
        if (level < 2700) return new LevelFormat(EnumChatFormatting.DARK_RED, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED, EnumChatFormatting.RED, EnumChatFormatting.RED, EnumChatFormatting.LIGHT_PURPLE }, EnumChatFormatting.LIGHT_PURPLE, "⚝", EnumChatFormatting.DARK_PURPLE, true);
        if (level < 2800) return new LevelFormat(EnumChatFormatting.YELLOW, new EnumChatFormatting[] { EnumChatFormatting.YELLOW, EnumChatFormatting.WHITE, EnumChatFormatting.WHITE, EnumChatFormatting.DARK_GRAY }, EnumChatFormatting.DARK_GRAY, "⚝", EnumChatFormatting.DARK_GRAY, true);
        if (level < 2900) return new LevelFormat(EnumChatFormatting.GREEN, new EnumChatFormatting[] { EnumChatFormatting.GREEN, EnumChatFormatting.DARK_GREEN, EnumChatFormatting.DARK_GREEN, EnumChatFormatting.GOLD }, EnumChatFormatting.GOLD, "⚝", EnumChatFormatting.YELLOW, true);
        if (level < 3000) return new LevelFormat(EnumChatFormatting.AQUA, new EnumChatFormatting[] { EnumChatFormatting.AQUA, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.BLUE }, EnumChatFormatting.BLUE, "⚝", EnumChatFormatting.DARK_BLUE, true);
        if (level < 3100) return new LevelFormat(EnumChatFormatting.YELLOW, new EnumChatFormatting[] { EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD, EnumChatFormatting.GOLD, EnumChatFormatting.RED }, EnumChatFormatting.RED, "⚝", EnumChatFormatting.DARK_RED, true);
        if (level < 3200) return new LevelFormat(EnumChatFormatting.BLUE, new EnumChatFormatting[] { EnumChatFormatting.BLUE, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.GOLD }, EnumChatFormatting.GOLD, "✥", EnumChatFormatting.YELLOW, false);
        if (level < 3300) return new LevelFormat(EnumChatFormatting.RED, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED, EnumChatFormatting.GRAY, EnumChatFormatting.GRAY, EnumChatFormatting.DARK_RED }, EnumChatFormatting.RED, "✥", EnumChatFormatting.RED, false);
        if (level < 3400) return new LevelFormat(EnumChatFormatting.BLUE, new EnumChatFormatting[] { EnumChatFormatting.BLUE, EnumChatFormatting.BLUE, EnumChatFormatting.LIGHT_PURPLE, EnumChatFormatting.RED }, EnumChatFormatting.RED, "✥", EnumChatFormatting.DARK_RED, false);
        if (level < 3500) return new LevelFormat(EnumChatFormatting.DARK_GREEN, new EnumChatFormatting[] { EnumChatFormatting.GREEN, EnumChatFormatting.LIGHT_PURPLE, EnumChatFormatting.LIGHT_PURPLE, EnumChatFormatting.DARK_PURPLE }, EnumChatFormatting.DARK_PURPLE, "✥", EnumChatFormatting.DARK_GREEN, false);
        if (level < 3600) return new LevelFormat(EnumChatFormatting.RED, new EnumChatFormatting[] { EnumChatFormatting.RED, EnumChatFormatting.DARK_RED, EnumChatFormatting.DARK_RED, EnumChatFormatting.DARK_GREEN }, EnumChatFormatting.GREEN, "✥", EnumChatFormatting.GREEN, false);
        if (level < 3700) return new LevelFormat(EnumChatFormatting.GREEN, new EnumChatFormatting[] { EnumChatFormatting.GREEN, EnumChatFormatting.GREEN, EnumChatFormatting.AQUA, EnumChatFormatting.BLUE }, EnumChatFormatting.BLUE, "✥", EnumChatFormatting.DARK_BLUE, false);
        if (level < 3800) return new LevelFormat(EnumChatFormatting.DARK_RED, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED, EnumChatFormatting.RED, EnumChatFormatting.RED, EnumChatFormatting.AQUA }, EnumChatFormatting.DARK_AQUA, "✥", EnumChatFormatting.DARK_AQUA, false);
        if (level < 3900) return new LevelFormat(EnumChatFormatting.DARK_BLUE, new EnumChatFormatting[] { EnumChatFormatting.DARK_BLUE, EnumChatFormatting.BLUE, EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.DARK_PURPLE }, EnumChatFormatting.LIGHT_PURPLE, "✥", EnumChatFormatting.DARK_BLUE, false);
        if (level < 4000) return new LevelFormat(EnumChatFormatting.RED, new EnumChatFormatting[] { EnumChatFormatting.RED, EnumChatFormatting.GREEN, EnumChatFormatting.GREEN, EnumChatFormatting.DARK_AQUA }, EnumChatFormatting.BLUE, "✥", EnumChatFormatting.BLUE, false);
        if (level < 4100) return new LevelFormat(EnumChatFormatting.DARK_PURPLE, new EnumChatFormatting[] { EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.RED, EnumChatFormatting.RED, EnumChatFormatting.GOLD }, EnumChatFormatting.GOLD, "✥", EnumChatFormatting.YELLOW, false);
        if (level < 4200) return new LevelFormat(EnumChatFormatting.YELLOW, new EnumChatFormatting[] { EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD, EnumChatFormatting.RED, EnumChatFormatting.LIGHT_PURPLE }, EnumChatFormatting.LIGHT_PURPLE, "✥", EnumChatFormatting.DARK_PURPLE, false);
        if (level < 4300) return new LevelFormat(EnumChatFormatting.DARK_BLUE, new EnumChatFormatting[] { EnumChatFormatting.BLUE, EnumChatFormatting.DARK_AQUA, EnumChatFormatting.AQUA, EnumChatFormatting.WHITE }, EnumChatFormatting.GRAY, "✥", EnumChatFormatting.GRAY, false);
        if (level < 4400) return new LevelFormat(EnumChatFormatting.BLACK, new EnumChatFormatting[] { EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.DARK_GRAY, EnumChatFormatting.DARK_GRAY, EnumChatFormatting.DARK_PURPLE }, EnumChatFormatting.DARK_PURPLE, "✥", EnumChatFormatting.BLACK, false);
        if (level < 4500) return new LevelFormat(EnumChatFormatting.DARK_GREEN, new EnumChatFormatting[] { EnumChatFormatting.DARK_GREEN, EnumChatFormatting.GREEN, EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD }, EnumChatFormatting.DARK_PURPLE, "✥", EnumChatFormatting.LIGHT_PURPLE, false);
        if (level < 4600) return new LevelFormat(EnumChatFormatting.WHITE, new EnumChatFormatting[] { EnumChatFormatting.WHITE, EnumChatFormatting.AQUA, EnumChatFormatting.AQUA, EnumChatFormatting.DARK_AQUA }, EnumChatFormatting.DARK_AQUA, "✥", EnumChatFormatting.DARK_AQUA, false);
        if (level < 4700) return new LevelFormat(EnumChatFormatting.DARK_AQUA, new EnumChatFormatting[] { EnumChatFormatting.AQUA, EnumChatFormatting.YELLOW, EnumChatFormatting.YELLOW, EnumChatFormatting.GOLD }, EnumChatFormatting.LIGHT_PURPLE, "✥", EnumChatFormatting.DARK_PURPLE, false);
        if (level < 4800) return new LevelFormat(EnumChatFormatting.WHITE, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED, EnumChatFormatting.RED, EnumChatFormatting.RED, EnumChatFormatting.BLUE }, EnumChatFormatting.DARK_BLUE, "✥", EnumChatFormatting.BLUE, false);
        if (level < 4900) return new LevelFormat(EnumChatFormatting.DARK_PURPLE, new EnumChatFormatting[] { EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.RED, EnumChatFormatting.GOLD, EnumChatFormatting.YELLOW }, EnumChatFormatting.AQUA, "✥", EnumChatFormatting.DARK_AQUA, false);
        if (level < 5000) return new LevelFormat(EnumChatFormatting.DARK_GREEN, new EnumChatFormatting[] { EnumChatFormatting.GREEN, EnumChatFormatting.WHITE, EnumChatFormatting.WHITE, EnumChatFormatting.GREEN }, EnumChatFormatting.GREEN, "✥", EnumChatFormatting.DARK_GREEN, false);
        if (level < 5100) return new LevelFormat(EnumChatFormatting.DARK_RED, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED, EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.BLUE, EnumChatFormatting.BLUE }, EnumChatFormatting.DARK_BLUE, "✥", EnumChatFormatting.BLACK, false);
        return new LevelFormat(EnumChatFormatting.DARK_RED, new EnumChatFormatting[] { EnumChatFormatting.DARK_RED, EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.BLUE, EnumChatFormatting.BLUE }, EnumChatFormatting.DARK_BLUE, "✥", EnumChatFormatting.BLACK, false);
    }
}