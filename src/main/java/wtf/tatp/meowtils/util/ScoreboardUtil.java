package wtf.tatp.meowtils.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardUtil {

    public static boolean titleContains(String text) {
        return getSidebarTitle().contains(text.toLowerCase());
    }

    public static boolean lineContains(String text) {
        return getSidebarLines().stream().anyMatch(l -> l.contains(text.toLowerCase()));
    }

    public static Scoreboard getScoreboard() {
        Minecraft mc = Minecraft.getMinecraft();
        return (mc.theWorld != null) ? mc.theWorld.getScoreboard() : null;
    }

    public static ScoreObjective getSidebar() {
        Scoreboard s = getScoreboard();
        return (s != null) ? s.getObjectiveInDisplaySlot(1) : null;
    }

    public static String getSidebarTitle() {
        ScoreObjective s = getSidebar();
        if (s == null) return "";
        return ColorUtil.unformattedText(s.getDisplayName().toLowerCase());
    }

    public static List<String> getSidebarLines() {
        Scoreboard scoreboard = getScoreboard();
        ScoreObjective objective = getSidebar();
        if (scoreboard == null || objective == null) return Collections.emptyList();

        List<Score> scores = new ArrayList<>(scoreboard.getSortedScores(objective));
        scores.sort(Comparator.comparingInt(Score::getScorePoints));
        List<String> lines = new ArrayList<>();

        for (Score score : scores) {
            if (score.getObjective() != objective) continue;

            String name = score.getPlayerName();
            ScorePlayerTeam team = scoreboard.getPlayersTeam(name);

            String line = ScorePlayerTeam.formatPlayerName(team, name);
            line = ColorUtil.unformattedText(line.toLowerCase());
            lines.add(line);
        }
        return lines;
    }
}