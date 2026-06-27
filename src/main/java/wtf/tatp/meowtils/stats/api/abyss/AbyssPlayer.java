package wtf.tatp.meowtils.stats.api.abyss;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.stats.StatsContainer;
import wtf.tatp.meowtils.stats.StatsSource;
import wtf.tatp.meowtils.stats.util.StatsUtil;
import wtf.tatp.meowtils.util.MojangNameToUUID;

public class AbyssPlayer implements StatsSource {

    @Override
    public String getId() {
        return "abyss-player";
    }

    @Override
    public StatsContainer fetch(String name) {
        String uuid;
        try {
            uuid = getUuid(name);
        } catch (Exception e) {
            return null;
        }

        if (uuid == null) return null;
        HttpURLConnection conn = null;
        try {
            String url = "http://api.abyssoverlay.com/player?uuid=" + uuid;
            String prefix = "(Abyss API) ";

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "node-ao/2.0.3");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int code = conn.getResponseCode();
            if (code != 200) {
                Meowtils.error(prefix + "Error: " + code);
                return null;
            }

            String response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                response = reader.lines().collect(Collectors.joining());
            }

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();

            if (!json.has("success") || !json.get("success").getAsBoolean()) {
                String cause = json.has("cause") ? json.get("cause").getAsString() : "Unknown reason";
                Meowtils.error(prefix + "error: " + cause);
                return null;
            }

            if (!json.has("player") || json.get("player").isJsonNull()) {
                Meowtils.error(prefix + "missing player field");
                return null;
            }

            JsonObject playerData = json.getAsJsonObject("player");
            JsonObject lastExp = StatsUtil.getObj(playerData, "eugene");

            long firstLogin = StatsUtil.getLong(playerData, "firstLogin", 0L);
            long lastLogin = StatsUtil.getLong(playerData, "lastLogin", 0L);
            long lastLogout = StatsUtil.getLong(playerData, "lastLogout", 0L);
            int networkExp = StatsUtil.getInt(playerData, "networkExp", 0);
            int karma = StatsUtil.getInt(playerData, "karma", 0);
            int achievementPoints = StatsUtil.getInt(playerData, "achievementPoints", 0);
            long lastClaimedReward = StatsUtil.getLong(playerData, "lastClaimedReward", 0L);
            long lastClaimedExp = StatsUtil.getLong(lastExp, "dailyTwoKExp", 0L);
            String channel = StatsUtil.getString(playerData, "channel", "Unknown");
            String language = StatsUtil.getString(playerData, "userLanguage", "Unknown");

            StatsContainer.GeneralStats generalStats = new StatsContainer.GeneralStats(
                    StatsUtil.getRank(playerData), StatsUtil.getPlusColor(playerData),
                    firstLogin, lastLogin, lastLogout, networkExp, karma,
                    achievementPoints, lastClaimedReward, lastClaimedExp, channel, language
            );

            JsonObject bw = StatsUtil.getObj(StatsUtil.getObj(playerData, "stats"), "Bedwars");

            int finalKills = StatsUtil.getInt(bw, "final_kills_bedwars", 0);
            int finalDeaths = StatsUtil.getInt(bw, "final_deaths_bedwars", 1);
            int beds = StatsUtil.getInt(bw, "beds_broken_bedwars", 0);
            int bedsLost = StatsUtil.getInt(bw, "beds_lost_bedwars", 0);
            double fkdr = (double) finalKills / finalDeaths;
            int wins = StatsUtil.getInt(bw, "wins_bedwars", 0);
            int losses = StatsUtil.getInt(bw, "losses_bedwars", 1);
            double wlr = (double) wins / losses;
            int ws = StatsUtil.getInt(bw, "winstreak", 0);
            int level = StatsUtil.getInt(StatsUtil.getObj(playerData, "achievements"), "bedwars_level", 0);
            double clutchRatio = (bedsLost == 0) ? 0.0D : (1.0D - (double) finalDeaths / bedsLost);

            StatsContainer.BedwarsStats bedwarsStats = new StatsContainer.BedwarsStats(
                    level, finalKills, beds, bedsLost, fkdr, wlr, ws, clutchRatio
            );

            JsonObject sw = StatsUtil.getObj(StatsUtil.getObj(playerData, "stats"), "SkyWars");

            String skywarsLevel = StatsUtil.getString(sw, "levelFormatted", "0");
            int skywarsKills = StatsUtil.getInt(sw, "kills", 0);
            int skywarsDeaths = StatsUtil.getInt(sw, "deaths", 1);
            double skywarsKdr = (double) skywarsKills / skywarsDeaths;
            int skywarsWins = StatsUtil.getInt(sw, "wins", 0);
            int skywarsLosses = StatsUtil.getInt(sw, "losses", 1);
            double skywarsWlr = (double) skywarsWins / skywarsLosses;

            StatsContainer.SkywarsStats skywarsStats = new StatsContainer.SkywarsStats(
                    skywarsLevel, skywarsKills, skywarsKdr, skywarsWins, skywarsWlr
            );

            StatsContainer container = new StatsContainer();
            container.updateGeneral(generalStats);
            container.updateBedwars(bedwarsStats);
            container.updateSkywars(skywarsStats);
            Meowtils.info(prefix + "Fetched stats for " + name);
            return container;
        } catch (Exception e) {
            Meowtils.error("Fetching stats from Abyss API failed: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static String getUuid(String name) throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        MojangNameToUUID.lookup(name, future::complete);
        return future.get(5000L, TimeUnit.MILLISECONDS);
    }
}