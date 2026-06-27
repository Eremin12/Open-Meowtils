package wtf.tatp.meowtils.stats.api.hypixel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.hypixel.Stats;
import wtf.tatp.meowtils.stats.StatsContainer;
import wtf.tatp.meowtils.stats.StatsSource;
import wtf.tatp.meowtils.stats.util.StatsUtil;

public class HypixelRecent implements StatsSource {

    private static final String PREFIX = "(Hypixel API) ";

    @Override
    public String getId() {
        return "hypixel-recent";
    }

    @Override
    public StatsContainer fetch(String uuid) {
        Stats s = Module.get(Stats.class);
        if (s == null) {
            Meowtils.error(PREFIX + "Stats module not found");
            return null;
        }

        if (s.apiKey == null || s.apiKey.isEmpty()) {
            Meowtils.error(PREFIX + "API key is missing");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            String url = "https://api.hypixel.net/v2/recentgames?key=" + s.apiKey.replace(" ", "") + "&uuid=" + uuid;

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int code = conn.getResponseCode();
            if (code != 200) {
                if (code == 400) {
                    Meowtils.error(PREFIX + "Some data is missing");
                } else if (code == 403) {
                    Meowtils.error(PREFIX + "Access forbidden");
                } else if (code == 422) {
                    Meowtils.error(PREFIX + "Some data is invalid");
                } else if (code == 429) {
                    Meowtils.error(PREFIX + "Request limit has been reached");
                } else {
                    Meowtils.error(PREFIX + "Unsigned error: " + code);
                }
                return null;
            }

            String response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                response = reader.lines().collect(Collectors.joining());
            }

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();

            if (!json.has("success") || !json.get("success").getAsBoolean()) {
                String cause = json.has("cause") ? json.get("cause").getAsString() : "Unknown reason";
                Meowtils.error(PREFIX + "error: " + cause);
                return null;
            }

            List<StatsContainer.RecentGames> recent = new ArrayList<>();

            if (json.has("games") && json.get("games").isJsonArray()) {
                JsonArray gamesArray = json.getAsJsonArray("games");
                List<JsonObject> gameObjects = new ArrayList<>();

                for (JsonElement element : gamesArray) {
                    if (element.isJsonObject()) {
                        gameObjects.add(element.getAsJsonObject());
                    }
                }

                gameObjects.sort((a, b) -> Long.compare(StatsUtil.getLong(b, "date", 0L), StatsUtil.getLong(a, "date", 0L)));
                int limit = Math.min(3, gameObjects.size());

                for (int i = 0; i < limit; i++) {
                    JsonObject game = gameObjects.get(i);

                    long date = StatsUtil.getLong(game, "date", 0L);
                    long ended = StatsUtil.getLong(game, "ended", 0L);
                    String gameType = StatsUtil.getString(game, "gameType", "Unknown");
                    String mode = StatsUtil.getString(game, "mode", "Unknown");
                    String map = StatsUtil.getString(game, "map", "Unknown");

                    recent.add(new StatsContainer.RecentGames(date, gameType, mode, map, ended));
                }
            }

            StatsContainer container = new StatsContainer();
            container.updateRecent(recent);
            Meowtils.info(PREFIX + "Fetched stats for " + uuid);
            return container;
        } catch (Exception e) {
            Meowtils.error("Fetching recent from Hypixel API failed: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}