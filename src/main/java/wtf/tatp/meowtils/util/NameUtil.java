package wtf.tatp.meowtils.util;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

public class NameUtil {

    public static String getTabDisplayName(String playerName) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || mc.theWorld.getScoreboard() == null) {
            return playerName;
        }

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScorePlayerTeam team = scoreboard.getPlayersTeam(playerName);

        if (team != null) {
            return team.getColorPrefix() + playerName + team.getColorSuffix();
        }

        return playerName;
    }

    public static EnumChatFormatting getNameColor(String formattedName) {
        if (formattedName == null || formattedName.isEmpty()) return EnumChatFormatting.GRAY;

        for (int i = 0; i < formattedName.length() - 1; i++) {
            if (formattedName.charAt(i) == '\u00a7') {
                char colorCode = formattedName.charAt(i + 1);
                return ColorUtil.getColorFromCode(colorCode);
            }
        }
        return EnumChatFormatting.WHITE;
    }
}