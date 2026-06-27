package wtf.tatp.meowtils.stats.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.module.hypixel.Stats;
import wtf.tatp.meowtils.stats.StatsContainer;
import wtf.tatp.meowtils.util.MojangNameToUUID;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.PlayerUtil;
import wtf.tatp.meowtils.util.Util;

public class ChatStats {

    private static final String LINE = EnumChatFormatting.DARK_GRAY + "---------------------------------------------";
    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " | ";
    private static final String HYPIXEL_API_ERROR = EnumChatFormatting.RED + "You do not have a " + EnumChatFormatting.BLUE + "Hypixel API" + EnumChatFormatting.RED + " key set." + EnumChatFormatting.WHITE + " Use: /meowapi <key>";

    public static void showInfo(String name) {
        if (PlayerUtil.isNicked(PlayerUtil.getProfile(name))) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Can't get info for nicked player: " + EnumChatFormatting.GOLD + name);
            return;
        }

        Stats.getStats(name, stats -> {
            Stats s = Module.get(Stats.class);
            if (s == null) return;

            if (stats == null || stats.general == null) {
                if (s.apiKey.isEmpty()) {
                    if (!s.api.equals("Hypixel")) {
                        Meowtils.addMessage("This command requires " + EnumChatFormatting.BLUE + "Hypixel API" + EnumChatFormatting.WHITE + ".");
                        return;
                    }
                    Meowtils.addMessage(HYPIXEL_API_ERROR);
                } else {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Failed to fetch info for: " + EnumChatFormatting.GRAY + name);
                }
                return;
            }

            String firstLogin = (stats.general.firstLogin == 0L) ? (EnumChatFormatting.RED + "Hidden") : (EnumChatFormatting.GRAY + Util.formatTimestamp(stats.general.firstLogin));
            String lastLogin = (stats.general.lastLogin == 0L) ? (EnumChatFormatting.RED + "Hidden") : (EnumChatFormatting.GRAY + Util.formatTimestamp(stats.general.lastLogin));
            String lastLogout = (stats.general.lastLogout == 0L) ? (EnumChatFormatting.RED + "Hidden") : (EnumChatFormatting.GRAY + Util.formatTimestamp(stats.general.lastLogout));
            String lastReward = (stats.general.lastClaimedReward == 0L) ? (EnumChatFormatting.RED + "Hidden") : (EnumChatFormatting.GRAY + Util.formatTimestamp(stats.general.lastClaimedReward));
            String lastExp = (stats.general.lastClaimedExp == 0L) ? (EnumChatFormatting.RED + "Hidden") : (EnumChatFormatting.GRAY + Util.formatTimestamp(stats.general.lastClaimedExp));
            double networkLevel = Math.sqrt((2 * stats.general.networkExp + 30625)) / 50.0D - 2.5D;

            Meowtils.addMessage(LINE);
            Meowtils.addMessage("Player: " + StatsUtil.getFormattedRankDisplay(stats.general.rank, stats.general.plusColor) + name);
            Meowtils.addMessage("First Login: " + firstLogin);
            Meowtils.addMessage("Last Login: " + lastLogin);
            Meowtils.addMessage("Last Logout: " + lastLogout);
            Meowtils.addMessage("Last Reward: " + lastReward);
            Meowtils.addMessage("Last EXP: " + lastExp);
            Meowtils.addMessage("Network Level: " + stats.general.plusColor + (int) networkLevel);
            Meowtils.addMessage("AP: " + EnumChatFormatting.GOLD + stats.general.achievementPoints);
            Meowtils.addMessage("Karma: " + EnumChatFormatting.LIGHT_PURPLE + stats.general.karma);
            Meowtils.addMessage("Language: " + (StatsUtil.isUnknown(stats.general.language) ? (EnumChatFormatting.RED + "?") : (EnumChatFormatting.BLUE + stats.general.language)));
            Meowtils.addMessage("Channel: " + (StatsUtil.isUnknown(stats.general.channel) ? (EnumChatFormatting.RED + "?") : StatsUtil.formattedChannel(stats.general.channel)));
            Meowtils.addMessage(LINE);
        });
    }

    public static void showBedwarsStats(String name, boolean warnNicked, boolean compact) {
        Stats.getStats(name, stats -> {
            Stats s = Module.get(Stats.class);
            if (s == null) return;

            if (PlayerUtil.isNicked(PlayerUtil.getProfile(name))) {
                if (warnNicked) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Can't get stats for nicked player: " + EnumChatFormatting.GOLD + name);
                }
                return;
            }
            if (stats == null || stats.general == null || stats.bedwars == null) {
                if (s.apiKey.isEmpty() && s.api.equals("Hypixel")) {
                    Meowtils.addMessage(HYPIXEL_API_ERROR);
                } else {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Failed to fetch bedwars stats for: " + EnumChatFormatting.GRAY + name);
                }
                return;
            }

            String rank = (s.useTeamColor && Bedwars.GAME.isActive()) ? NameUtil.getTabDisplayName(name) : (StatsUtil.getFormattedRankDisplay(stats.general.rank, stats.general.plusColor) + name);
            String ws = (stats.bedwars.ws > 1) ? (SEPARATOR + BedwarsStatsUtil.getWSColor(stats.bedwars.ws) + "WS: " + stats.bedwars.ws) : "";

            if (compact) {
                Meowtils.addMessage(BedwarsStatsUtil.getFormattedLevel(stats.bedwars.level) + " " + rank +
                        SEPARATOR + BedwarsStatsUtil.getFKDRColor(stats.bedwars.fkdr) + "FKDR: " + String.format("%.1f", stats.bedwars.fkdr).replace(",", ".") +
                        SEPARATOR + BedwarsStatsUtil.getWLRColor(stats.bedwars.wlr) + "WLR: " + String.format("%.1f", stats.bedwars.wlr).replace(",", ".") + ws);
            } else {
                Meowtils.addMessage(BedwarsStatsUtil.getFormattedLevel(stats.bedwars.level) + " " + rank +
                        SEPARATOR + BedwarsStatsUtil.getFinalsColor(stats.bedwars.finals) + "Finals: " + stats.bedwars.finals +
                        SEPARATOR + BedwarsStatsUtil.getFKDRColor(stats.bedwars.fkdr) + "FKDR: " + String.format("%.1f", stats.bedwars.fkdr).replace(",", ".") +
                        SEPARATOR + BedwarsStatsUtil.getWLRColor(stats.bedwars.wlr) + "WLR: " + String.format("%.1f", stats.bedwars.wlr).replace(",", ".") + ws +
                        SEPARATOR + BedwarsStatsUtil.getClutchRatioColor(stats.bedwars.clutchRatio) + "CR: " + BigDecimal.valueOf(stats.bedwars.clutchRatio).setScale(2, RoundingMode.DOWN).toPlainString().replace(",", "."));
            }
        });
    }

    public static void showSkywarsStats(String name, boolean warnNicked, boolean compact) {
        Stats.getStats(name, stats -> {
            Stats s = Module.get(Stats.class);
            if (s == null) return;

            if (PlayerUtil.isNicked(PlayerUtil.getProfile(name))) {
                if (warnNicked) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Can't get stats for nicked player: " + EnumChatFormatting.GOLD + name);
                }
                return;
            }
            if (stats == null || stats.skywars == null) {
                if (s.apiKey.isEmpty() && s.api.equals("Hypixel")) {
                    Meowtils.addMessage(HYPIXEL_API_ERROR);
                } else {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Failed to fetch skywars stats for: " + EnumChatFormatting.GRAY + name);
                }
                return;
            }

            if (compact) {
                Meowtils.addMessage(stats.skywars.level + "✯ " + StatsUtil.getFormattedRankDisplay(stats.general.rank, stats.general.plusColor) + name +
                        SEPARATOR + SkywarsStatsUtil.getKdrColor(stats.skywars.kdr) + "KDR: " + String.format("%.1f", stats.skywars.kdr).replace(",", ".") +
                        SEPARATOR + SkywarsStatsUtil.getWlrColor(stats.skywars.wlr) + "WLR: " + String.format("%.1f", stats.skywars.wlr).replace(",", "."));
            } else {
                Meowtils.addMessage(stats.skywars.level + "✯ " + StatsUtil.getFormattedRankDisplay(stats.general.rank, stats.general.plusColor) + name +
                        SEPARATOR + SkywarsStatsUtil.getKillsColor(stats.skywars.kills) + "Kills: " + stats.skywars.kills +
                        SEPARATOR + SkywarsStatsUtil.getWinsColor(stats.skywars.wins) + "Wins: " + stats.skywars.wins +
                        SEPARATOR + SkywarsStatsUtil.getKdrColor(stats.skywars.kdr) + "KDR: " + String.format("%.1f", stats.skywars.kdr).replace(",", ".") +
                        SEPARATOR + SkywarsStatsUtil.getWlrColor(stats.skywars.wlr) + "WLR: " + String.format("%.1f", stats.skywars.wlr).replace(",", "."));
            }
        });
    }

    public static void showRecentGames(String name) {
        if (PlayerUtil.isNicked(PlayerUtil.getProfile(name))) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Can't get recent games for nicked player: " + EnumChatFormatting.GOLD + name);
            return;
        }

        MojangNameToUUID.lookup(name, uuid -> {
            if (uuid == null || uuid.isEmpty()) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Failed to get player UUID.");
                return;
            }
            Stats.getRecent(uuid, stats -> {
                if (stats == null || stats.recentGames == null || stats.recentGames.isEmpty()) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Failed to fetch recent games for: " + EnumChatFormatting.GRAY + name);
                    return;
                }

                Meowtils.addMessage(LINE);
                Meowtils.addMessage(EnumChatFormatting.GOLD + name + "'s recent games:");
                for (StatsContainer.RecentGames game : stats.recentGames) {
                    Meowtils.addMessage(EnumChatFormatting.GRAY + Util.formatTimestamp(game.date) +
                            SEPARATOR + EnumChatFormatting.AQUA + game.gameType +
                            SEPARATOR + EnumChatFormatting.WHITE + game.mode +
                            SEPARATOR + EnumChatFormatting.GRAY + game.map);
                }
                Meowtils.addMessage(LINE);
            });
        });
    }

    public static void showStatus(String name) {
        if (PlayerUtil.isNicked(PlayerUtil.getProfile(name))) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Can't get status for nicked player: " + EnumChatFormatting.GOLD + name);
            return;
        }

        MojangNameToUUID.lookup(name, uuid -> {
            if (uuid == null || uuid.isEmpty()) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Failed to get player UUID.");
                return;
            }
            Stats.getStatus(uuid, stats -> {
                if (stats == null || stats.onlineStatus == null) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Failed to fetch status for: " + EnumChatFormatting.GRAY + name);
                    return;
                }

                StatsContainer.OnlineStatus status = stats.onlineStatus;
                String statusText = status.online ?
                        (EnumChatFormatting.GREEN + "Online" + SEPARATOR + EnumChatFormatting.AQUA + status.gameType + SEPARATOR + EnumChatFormatting.WHITE + status.mode + SEPARATOR + EnumChatFormatting.GRAY + status.map) :
                        (EnumChatFormatting.RED + "Offline");

                Meowtils.addMessage(EnumChatFormatting.GOLD + name + "'s status: " + statusText);
            });
        });
    }
}