package wtf.tatp.meowtils.stats;

import java.util.List;
import net.minecraft.util.EnumChatFormatting;

public class StatsContainer {

    private long fetchedAt;
    public GeneralStats general;
    public List<RecentGames> recentGames;
    public OnlineStatus onlineStatus;
    public List<UrchinTag> urchinTags;
    public BedwarsStats bedwars;
    public SkywarsStats skywars;

    public void updateGeneral(GeneralStats stats) {
        this.general = stats;
        updateFetchTime();
    }

    public void updateRecent(List<RecentGames> recent) {
        this.recentGames = recent;
        updateFetchTime();
    }

    public void updateStatus(OnlineStatus status) {
        this.onlineStatus = status;
        updateFetchTime();
    }

    public void updateUrchinTags(List<UrchinTag> tags) {
        this.urchinTags = tags;
        updateFetchTime();
    }

    public void updateBedwars(BedwarsStats stats) {
        this.bedwars = stats;
        updateFetchTime();
    }

    public void updateSkywars(SkywarsStats stats) {
        this.skywars = stats;
        updateFetchTime();
    }

    private void updateFetchTime() {
        this.fetchedAt = System.currentTimeMillis();
    }

    public boolean isExpired(long ttl) {
        return (System.currentTimeMillis() - this.fetchedAt > ttl);
    }

    public static class GeneralStats {
        public final String rank;
        public final EnumChatFormatting plusColor;
        public final long firstLogin;
        public final long lastLogin;
        public final long lastLogout;
        public final int networkExp;
        public final int karma;
        public final int achievementPoints;
        public final long lastClaimedReward;
        public final long lastClaimedExp;
        public final String channel;
        public final String language;

        public GeneralStats(String rank, EnumChatFormatting plusColor, long firstLogin, long lastLogin, long lastLogout,
                            int networkExp, int karma, int achievementPoints, long lastClaimedReward, long lastClaimedExp,
                            String channel, String language) {
            this.rank = rank;
            this.plusColor = plusColor;
            this.firstLogin = firstLogin;
            this.lastLogin = lastLogin;
            this.lastLogout = lastLogout;
            this.networkExp = networkExp;
            this.karma = karma;
            this.achievementPoints = achievementPoints;
            this.lastClaimedReward = lastClaimedReward;
            this.lastClaimedExp = lastClaimedExp;
            this.channel = channel;
            this.language = language;
        }
    }

    public static class RecentGames {
        public final long date;
        public final String gameType;
        public final String mode;
        public final String map;
        public final long ended;

        public RecentGames(long date, String gameType, String mode, String map, long ended) {
            this.date = date;
            this.gameType = gameType;
            this.mode = mode;
            this.map = map;
            this.ended = ended;
        }
    }

    public static class OnlineStatus {
        public final boolean online;
        public final String gameType;
        public final String mode;
        public final String map;

        public OnlineStatus(boolean online, String gameType, String mode, String map) {
            this.online = online;
            this.gameType = gameType;
            this.mode = mode;
            this.map = map;
        }
    }

    public static class UrchinTag {
        public final String type;
        public final String reason;
        public final String addedOn;

        public UrchinTag(String type, String reason, String addedOn) {
            this.type = type;
            this.reason = reason;
            this.addedOn = addedOn;
        }
    }

    public static class BedwarsStats {
        public final int level;
        public final int finals;
        public final int beds;
        public final int bedsLost;
        public final double fkdr;
        public final double wlr;
        public final int ws;
        public final double clutchRatio;

        public BedwarsStats(int level, int finals, int beds, int bedsLost, double fkdr, double wlr, int ws, double clutchRatio) {
            this.level = level;
            this.finals = finals;
            this.beds = beds;
            this.bedsLost = bedsLost;
            this.fkdr = fkdr;
            this.wlr = wlr;
            this.ws = ws;
            this.clutchRatio = clutchRatio;
        }
    }

    public static class SkywarsStats {
        public final String level;
        public final int kills;
        public final double kdr;
        public final int wins;
        public final double wlr;

        public SkywarsStats(String level, int kills, double kdr, int wins, double wlr) {
            this.level = level;
            this.kills = kills;
            this.kdr = kdr;
            this.wins = wins;
            this.wlr = wlr;
        }
    }
}