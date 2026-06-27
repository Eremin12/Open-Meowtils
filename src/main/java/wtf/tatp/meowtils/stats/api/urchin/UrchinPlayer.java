package wtf.tatp.meowtils.stats.api.urchin;

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
import wtf.tatp.meowtils.manager.lists.UrchinManager;
import wtf.tatp.meowtils.module.hypixel.Stats;
import wtf.tatp.meowtils.stats.StatsContainer;
import wtf.tatp.meowtils.stats.StatsSource;

public class UrchinPlayer implements StatsSource {

    private static final String PREFIX = "(Urchin) ";

    @Override
    public String getId() {
        return "urchin-player";
    }

    @Override
    public StatsContainer fetch(String name) {
        Stats s = Module.get(Stats.class);
        if (s == null) {
            Meowtils.error(PREFIX + "Stats module not found");
            return null;
        }

        if (s.urchinApiKey == null || s.urchinApiKey.isEmpty()) {
            Meowtils.error(PREFIX + "API key is missing");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            String url = "https://api.urchin.gg/v3/player/tags?player=" + name + "&key=" + s.urchinApiKey.replace(" ", "");

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return null;
            }

            String response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                response = reader.lines().collect(Collectors.joining());
            }

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            String uuid = (json.has("uuid") && !json.get("uuid").isJsonNull()) ? json.get("uuid").getAsString() : "";
            JsonArray tagsArray = (json.has("tags") && json.get("tags").isJsonArray()) ? json.getAsJsonArray("tags") : new JsonArray();
            List<StatsContainer.UrchinTag> tags = new ArrayList<>();

            for (JsonElement element : tagsArray) {
                if (!element.isJsonObject()) continue;
                JsonObject tag = element.getAsJsonObject();
                String type = (tag.has("type") && !tag.get("type").isJsonNull()) ? tag.get("type").getAsString() : "unknown";
                String reason = (tag.has("reason") && !tag.get("reason").isJsonNull()) ? tag.get("reason").getAsString() : "";
                String addedOn = (tag.has("added_on") && !tag.get("added_on").isJsonNull()) ? tag.get("added_on").getAsString() : "";
                tags.add(new StatsContainer.UrchinTag(type, reason, addedOn));
            }

            if (tags.isEmpty()) {
                UrchinManager.remove(name, uuid);
            } else {
                UrchinManager.put(name, uuid, tags);
            }

            StatsContainer container = new StatsContainer();
            container.updateUrchinTags(tags);
            Meowtils.info(PREFIX + "Fetched blacklist for: " + name);
            return container;
        } catch (Exception e) {
            Meowtils.error(PREFIX + "Fetching blacklist failed: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}