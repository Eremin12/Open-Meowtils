package wtf.tatp.meowtils.manager.icons.impl;

import com.mojang.authlib.GameProfile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.icons.IconProvider;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.hypixel.Stats;
import wtf.tatp.meowtils.stats.StatsContainer;
import wtf.tatp.meowtils.stats.util.BedwarsStatsUtil;
import wtf.tatp.meowtils.stats.util.SkywarsStatsUtil;
import wtf.tatp.meowtils.util.PlayerUtil;

public class StatsIcon implements IconProvider {

    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " ● ";
    private static final String STARTER = " ▶ ";

    private final Stats s = Module.get(Stats.class);

    @Override
    public String getPrefix(GameProfile profile, boolean tablist, boolean nametag) {
        if (this.s == null || !this.s.enabled) return "";
        if (Server.HYPIXEL.isNotActive()) return "";
        if (profile == null) return "";
        if (nametag && !this.s.nametag) return "";
        if (tablist && !this.s.tablist) return "";

        String name = profile.getName();
        if (name == null || PlayerUtil.isNicked(profile)) return "";
        if (profile.getId() == null) return "";
        if (profile.getId().version() == 2) return "";

        StatsContainer stats = Stats.getCached(name);
        if (stats == null && (Bedwars.GAME.isActive() || Skywars.GAME.isActive() || Skywars.MINI.isActive())) {
            if (!PlayerUtil.isNicked(profile)) {
                Stats.getStats(name, s -> {});
            }
            return "";
        }

        if (Bedwars.GAME.isActive()) {
            if (stats == null || stats.bedwars == null) return "";
            return nametag ? (BedwarsStatsUtil.getFormattedLevel(stats.bedwars.level).replace("[", "").replace("]", "") + " ") :
                    (BedwarsStatsUtil.getFormattedLevel(stats.bedwars.level) + " ");
        }

        if (Skywars.GAME.isActive() || Skywars.MINI.isActive()) {
            if (stats == null || stats.skywars == null) return "";
            return stats.skywars.level + "✯ ";
        }

        return "";
    }

    @Override
    public String getSuffix(GameProfile profile, boolean tablist, boolean nametag) {
        if (this.s == null || !this.s.enabled) return "";
        if (Server.HYPIXEL.isNotActive()) return "";
        if (profile == null) return "";
        if (nametag && !this.s.nametag) return "";
        if (tablist && !this.s.tablist) return "";
        if (nametag && this.s.displayMode.equals("Compact")) return "";

        boolean lowercase = this.s.displayMode.equals("Lowercase");
        boolean compact = this.s.displayMode.equals("Compact");

        String name = profile.getName();
        if (name == null || PlayerUtil.isNicked(profile)) return "";
        if (profile.getId() == null) return "";
        if (profile.getId().version() == 2) return "";

        StatsContainer stats = Stats.getCached(name);
        if (stats == null && (Bedwars.GAME.isActive() || Skywars.GAME.isActive() || Skywars.MINI.isActive())) {
            if (!PlayerUtil.isNicked(profile)) {
                Stats.getStats(name, s -> {});
            }
            return "";
        }

        if (Bedwars.GAME.isActive()) {
            if (stats == null || stats.bedwars == null) return "";
            List<String> parts = new ArrayList<>();

            if (this.s.bedwarsFinals) {
                String statType = compact ? "" : (lowercase ? "f: " : "F: ");
                parts.add(BedwarsStatsUtil.getFinalsColor(stats.bedwars.finals) + statType + stats.bedwars.finals);
            }

            if (this.s.bedwarsFkdr) {
                String statType = compact ? "" : (lowercase ? "fkd: " : "FKD: ");
                parts.add(BedwarsStatsUtil.getFKDRColor(stats.bedwars.fkdr) + statType + format(stats.bedwars.fkdr));
            }

            if (this.s.bedwarsWlr) {
                String statType = compact ? "" : (lowercase ? "wl: " : "WL: ");
                parts.add(BedwarsStatsUtil.getWLRColor(stats.bedwars.wlr) + statType + format(stats.bedwars.wlr));
            }

            if (this.s.bedwarsWs) {
                String statType = compact ? "" : (lowercase ? "ws: " : "WS: ");
                String winstreak = (stats.bedwars.ws > 1) ? (BedwarsStatsUtil.getWSColor(stats.bedwars.ws) + statType + stats.bedwars.ws) : "";
                if (!winstreak.isEmpty()) parts.add(winstreak);
            }

            if (this.s.bedwarsCr) {
                String statType = compact ? "" : (lowercase ? "cr: " : "CR: ");
                parts.add(BedwarsStatsUtil.getClutchRatioColor(stats.bedwars.clutchRatio) + statType +
                        BigDecimal.valueOf(stats.bedwars.clutchRatio).setScale(2, RoundingMode.DOWN).toPlainString().replace(",", "."));
            }

            return joinStats(parts);
        }

        if (Skywars.GAME.isActive() || Skywars.MINI.isActive()) {
            if (stats == null || stats.skywars == null) return "";
            List<String> parts = new ArrayList<>();

            if (this.s.skywarsKills) {
                String statType = compact ? "" : (lowercase ? "k: " : "K: ");
                parts.add(SkywarsStatsUtil.getKillsColor(stats.skywars.kills) + statType + stats.skywars.kills);
            }

            if (this.s.skywarsWins) {
                String statType = compact ? "" : (lowercase ? "w: " : "W: ");
                parts.add(SkywarsStatsUtil.getWinsColor(stats.skywars.wins) + statType + stats.skywars.wins);
            }

            if (this.s.skywarsKdr) {
                String statType = compact ? "" : (lowercase ? "kd: " : "KD: ");
                parts.add(SkywarsStatsUtil.getKdrColor(stats.skywars.kdr) + statType + format(stats.skywars.kdr));
            }

            if (this.s.skywarsWlr) {
                String statType = compact ? "" : (lowercase ? "wl: " : "WL: ");
                parts.add(SkywarsStatsUtil.getWlrColor(stats.skywars.wlr) + statType + format(stats.skywars.wlr));
            }

            return joinStats(parts);
        }

        return "";
    }

    private String format(double d) {
        return String.format("%.1f", d).replace(",", ".");
    }

    private String joinStats(List<String> parts) {
        return parts.isEmpty() ? "" : (STARTER + String.join(SEPARATOR, parts));
    }
}