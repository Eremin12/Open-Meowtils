package wtf.tatp.meowtils.util;

import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.event.api.EventTarget;

public class MojangNameToUUID {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    public static void lookup(String name, Consumer<String> callback) {
        EXECUTOR.submit(() -> {
            String local = lookupLocal(name);
            String result = (local != null) ? local : lookupFromAPI(name);
            new MainThreadCallback(result, callback);
        });
    }

    private static String lookupLocal(String name) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                GameProfile gameProfile = player.getGameProfile();
                if (gameProfile.getName().equalsIgnoreCase(name)) {
                    return gameProfile.getId().toString();
                }
            }
        }

        if (mc.getNetHandler() != null) {
            for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
                GameProfile gameProfile = info.getGameProfile();
                if (gameProfile.getName().equalsIgnoreCase(name)) {
                    return gameProfile.getId().toString();
                }
            }
        }
        return null;
    }

    private static String lookupFromAPI(String name) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() != 200) return null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                String json = response.toString();
                if (json.contains("\"id\":\"")) {
                    int start = json.indexOf("\"id\":\"") + 6;
                    int end = json.indexOf("\"", start);
                    return json.substring(start, end);
                }
            }
            return null;
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static class MainThreadCallback {
        private final String uuid;
        private final Consumer<String> callback;

        public MainThreadCallback(String uuid, Consumer<String> callback) {
            this.uuid = uuid;
            this.callback = callback;
            EventManager.register(this);
        }

        @EventTarget
        public void onClientTick(ClientTickEvent event) {
            if (event.getPhase() != ClientTickEvent.Phase.PRE) return;
            try {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.theWorld != null && mc.thePlayer != null) {
                    this.callback.accept(this.uuid);
                }
            } finally {
                EventManager.unregister(this);
            }
        }
    }
}