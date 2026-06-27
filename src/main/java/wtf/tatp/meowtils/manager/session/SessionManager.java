package wtf.tatp.meowtils.manager.session;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.util.ScoreboardUtil;

public class SessionManager {

    private static final long REFRESH_DELAY = 500L;
    private static long lastRefreshTime = 0L;

    public static boolean universalServer = false;
    public static boolean mineplex = false;
    public static boolean hypixel = false;
    public static boolean hypixelReplay = false;
    public static boolean skywars = false;
    public static boolean skywarsLobby = false;
    public static boolean skywarsGame = false;
    public static boolean skywarsGameMini = false;
    public static boolean bedwars = false;
    public static boolean bedwarsLobby = false;
    public static boolean bedwarsPractice = false;
    public static boolean bedwarsGamePre = false;
    public static boolean bedwarsGame = false;
    public static boolean bedwarsGameSolos = false;
    public static boolean bedwarsGameDoubles = false;
    public static boolean bedwarsGameThrees = false;
    public static boolean bedwarsGameFours = false;
    public static boolean bedwarsGameOneBlock = false;
    public static boolean bedwarsGameFourFour = false;
    public static boolean duels = false;
    public static boolean duelsLobby = false;
    public static boolean duelsBedwars = false;
    public static boolean megaWalls = false;
    public static boolean megaWallsLobby = false;
    public static boolean megaWallsGame = false;
    public static boolean murderMystery = false;

    @EventTarget
    public void onReceivePacket(ReceivePacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof S3BPacketScoreboardObjective ||
                packet instanceof S3CPacketUpdateScore ||
                packet instanceof S3DPacketDisplayScoreboard ||
                packet instanceof S3EPacketTeams) {

            long now = System.currentTimeMillis();
            if (now - lastRefreshTime < REFRESH_DELAY) return;
            lastRefreshTime = now;

            Minecraft.getMinecraft().addScheduledTask(SessionManager::refresh);
        }
    }

    private static void refresh() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);

        if (sidebar == null) {
            resetStates();
            return;
        }

        try {
            updateStates();
        } catch (Exception e) {
            Meowtils.error("Error while trying to update session states: " + e);
        }
    }

    private static void updateStates() {
        universalServer = (ScoreboardUtil.lineContains("fakepixel") || ScoreboardUtil.lineContains("blocksmc"));

        mineplex = ScoreboardUtil.lineContains("mineplex");

        hypixel = ScoreboardUtil.lineContains("hypixel");
        hypixelReplay = (hypixel && (ScoreboardUtil.titleContains("replay") || ScoreboardUtil.titleContains("atlas")));

        skywars = (ScoreboardUtil.titleContains("skywars") || ScoreboardUtil.titleContains("sky wars"));
        skywarsLobby = (skywars && ScoreboardUtil.lineContains("your level:"));
        skywarsGame = ((skywars && ScoreboardUtil.lineContains("mode: normal")) || ScoreboardUtil.lineContains("mode: insane"));
        skywarsGameMini = (skywars && ScoreboardUtil.lineContains("mode: mini"));

        bedwars = (ScoreboardUtil.titleContains("bed wars") || ScoreboardUtil.titleContains("bedwars"));
        bedwarsPractice = ScoreboardUtil.titleContains("bed wars practice");
        bedwarsLobby = (!bedwarsPractice && ScoreboardUtil.titleContains("bed wars") && ScoreboardUtil.lineContains("level:"));
        bedwarsGamePre = ((!bedwarsPractice && ScoreboardUtil.titleContains("bed wars") && ScoreboardUtil.lineContains("waiting...")) || ScoreboardUtil.lineContains("starting in"));
        bedwarsGame = (!bedwarsPractice && !bedwarsLobby && (ScoreboardUtil.titleContains("bed wars") || ScoreboardUtil.titleContains("bedwars")));
        bedwarsGameSolos = (bedwarsGame && ScoreboardUtil.lineContains("mode: solo"));
        bedwarsGameDoubles = (bedwarsGame && ScoreboardUtil.lineContains("mode: doubles"));
        bedwarsGameThrees = (bedwarsGame && ScoreboardUtil.lineContains("mode: 3v3v3v3"));
        bedwarsGameFours = (bedwarsGame && ScoreboardUtil.lineContains("mode: 4v4v4v4"));
        bedwarsGameOneBlock = (bedwarsGame && ScoreboardUtil.lineContains("mode: one block"));
        bedwarsGameFourFour = (bedwarsGame && ScoreboardUtil.lineContains("mode: 4v4"));

        duels = ScoreboardUtil.titleContains("duels");
        duelsLobby = (duels && ScoreboardUtil.lineContains("tokens:"));
        duelsBedwars = (ScoreboardUtil.titleContains("bed wars") && ScoreboardUtil.lineContains("mode: bed wars duel"));

        megaWalls = ScoreboardUtil.titleContains("mega walls");
        megaWallsLobby = ScoreboardUtil.titleContains("mega walls");
        megaWallsGame = ScoreboardUtil.titleContains("mega walls");

        murderMystery = ScoreboardUtil.titleContains("murder mystery");
    }

    public static void resetStates() {
        universalServer = hypixel = hypixelReplay = false;
        mineplex = false;
        skywars = skywarsLobby = skywarsGame = skywarsGameMini = false;
        bedwars = bedwarsLobby = bedwarsPractice = bedwarsGamePre = bedwarsGame = false;
        bedwarsGameSolos = bedwarsGameDoubles = bedwarsGameThrees = bedwarsGameFours = false;
        bedwarsGameOneBlock = bedwarsGameFourFour = false;
        duels = duelsLobby = duelsBedwars = false;
        megaWalls = megaWallsLobby = megaWallsGame = false;
        murderMystery = false;
    }
}