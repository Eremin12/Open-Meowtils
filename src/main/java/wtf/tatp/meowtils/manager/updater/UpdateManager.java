package wtf.tatp.meowtils.manager.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.io.FileUtils;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.meowtils.Settings;
import wtf.tatp.meowtils.util.Prefix;

public class UpdateManager {

    private static final String MEOWTILS_LATEST = "https://api.github.com/repos/femboytatp/meowtils/releases/latest";
    private static final String MEOWTILS_UPDATER = "https://github.com/femboytatp/meowtils-auto-update/releases/latest/download/MeowtilsAutoUpdate.jar";
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean verified = false;
    private static final String VERSION = "2.0.0";

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (verified || mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        Meowtils.addMessage(EnumChatFormatting.GRAY + "Verifying " + EnumChatFormatting.DARK_PURPLE.toString() +
                EnumChatFormatting.BOLD + "Meowtils" + EnumChatFormatting.GRAY + " version...");
        Meowtils.warn("Verifying version.");

        verified = true;
        new Thread(UpdateManager::checkForUpdate, "Meowtils-Updater").start();
        EventManager.unregister(this);
    }

    private static void checkForUpdate() {
        try {
            JsonObject release = fetchLatest();
            if (release == null) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Unable to fetch update.");
                Meowtils.error("Unable to fetch update.");
                return;
            }

            String latestTag = release.get("tag_name").getAsString();
            if (compareVersions(VERSION, latestTag) >= 0) {
                Meowtils.addMessage(EnumChatFormatting.GREEN + "Already using latest " +
                        EnumChatFormatting.DARK_PURPLE.toString() + EnumChatFormatting.BOLD + "Meowtils" +
                        EnumChatFormatting.GREEN + " version: " + EnumChatFormatting.GRAY + VERSION);
                Meowtils.warn("Using latest version already: " + latestTag);
                return;
            }

            JsonArray assets = release.getAsJsonArray("assets");
            if (assets.size() == 0) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Failed to verify version.");
                Meowtils.error("Failed to verify version, assets size was 0.");
                return;
            }

            String url = null;
            String name = null;
            for (JsonElement e : assets) {
                JsonObject asset = e.getAsJsonObject();
                if (asset.get("name").getAsString().endsWith(".jar")) {
                    url = asset.get("browser_download_url").getAsString();
                    name = asset.get("name").getAsString();
                    Meowtils.warn("Set URL and name, URL: " + url + " Name: " + name);
                    break;
                }
            }

            if (url == null) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Failed to find update.");
                Meowtils.error("Failed to find update, browser_download_url was null.");
                return;
            }

            Meowtils.addMessage(EnumChatFormatting.DARK_PURPLE.toString() + EnumChatFormatting.BOLD + "Meowtils" +
                    EnumChatFormatting.GREEN + " update " + EnumChatFormatting.GRAY + latestTag +
                    EnumChatFormatting.GREEN + " is available!");

            Settings settings = Module.get(Settings.class);
            if (settings != null && settings.autoUpdate) {
                Meowtils.addMessage(EnumChatFormatting.GREEN + "Downloading update...");
                download(url, name);
                Meowtils.warn("Download URL: " + url + " Name: " + name);
            } else {
                ChatComponentText msg = new ChatComponentText(Prefix.getPrefix() + EnumChatFormatting.GREEN.toString() +
                        EnumChatFormatting.BOLD + "Click to download update!");
                msg.setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + url))));
                mc.thePlayer.addChatMessage(msg);
                Meowtils.warn("Auto updates are disabled, manual download is required.");
            }
        } catch (Exception e) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Failed to verify update.");
            Meowtils.error("Failed to verify update.");
            e.printStackTrace();
        }
    }

    private static void download(String url, String name) {
        try {
            String newName;
            if (!Meowtils.AUTO_UPDATE_DIR.exists()) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Update directory does not exist or was not found.");
                Meowtils.error("Couldn't find cache directory for update: " + Meowtils.AUTO_UPDATE_DIR);
                return;
            }

            File oldJar = getCurrentJar();

            if (oldJar != null) {
                String oldName = oldJar.getName();
                boolean isVersioned = (oldName.contains("-") && oldName.substring(0, oldName.lastIndexOf('.')).contains("."));

                if (isVersioned) {
                    newName = name;
                    Meowtils.warn("Updated jar was not renamed as it has version tags.");
                } else {
                    newName = oldName;
                    Meowtils.addMessage(EnumChatFormatting.GREEN + "Renamed jar to: " + EnumChatFormatting.WHITE + oldName.replace(".jar", ""));
                    Meowtils.warn("Updated jar was renamed to: " + oldName);
                }
            } else {
                newName = name;
                Meowtils.error("Updated jar was null and did not get renamed.");
            }

            File target = new File(Meowtils.AUTO_UPDATE_DIR, newName);
            FileUtils.copyURLToFile(new URL(url), target);
            if (!updater(target)) {
                return;
            }

            Meowtils.addMessage(EnumChatFormatting.GREEN + "Done!" + EnumChatFormatting.GRAY.toString() +
                    EnumChatFormatting.ITALIC + " Update will be applied next launch.");
            Meowtils.warn("Update is completed and will be applied next launch.");
        } catch (Exception e) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Failed to download update.");
            Meowtils.error("Failed to download update.");
            e.printStackTrace();
        }
    }

    private static boolean updater(File newJar) {
        File autoUpdateJar = new File(mc.mcDataDir, "meowtils/auto_update/MeowtilsAutoUpdate.jar");

        if (!autoUpdateJar.exists()) {
            try {
                FileUtils.copyURLToFile(new URL(MEOWTILS_UPDATER), autoUpdateJar);
            } catch (Exception e) {
                ChatComponentText msg = new ChatComponentText(Prefix.getPrefix() + EnumChatFormatting.RED +
                        "Failed to download update jar, you will have to download this manually " +
                        EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "HERE" + EnumChatFormatting.RED + ".");
                msg.setChatStyle(msg.getChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/femboytatp/meowtils-auto-update"))
                        .setUnderlined(true));
                mc.thePlayer.addChatMessage(msg);
                Meowtils.error("Failed to download updater jar, manual download is required.");
                e.printStackTrace();
                return false;
            }
        }

        File oldJar = getCurrentJar();
        if (oldJar == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Unable to find path, you will have to update manually.");
            Meowtils.error("Old jar was null and you will have to update manually.");
            return false;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                new ProcessBuilder(javaBin, "-jar", autoUpdateJar.getAbsolutePath(), oldJar.getAbsolutePath(), newJar.getAbsolutePath()).start();
                Meowtils.warn("Successfully registered shutdown hook for update.");
            } catch (Exception e) {
                Meowtils.error("Unable to register shutdown hook for update jar.");
                e.printStackTrace();
            }
        }));
        return true;
    }

    private static File getCurrentJar() {
        try {
            URL url = UpdateManager.class.getProtectionDomain().getCodeSource().getLocation();
            URI uri = url.toURI();

            if ("jar".equals(uri.getScheme())) {
                String path = uri.getSchemeSpecificPart();
                uri = new URI(path.substring(0, path.lastIndexOf("!")));
            }

            File jar = new File(uri);
            if (!jar.exists() || !jar.getName().endsWith(".jar")) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Unexpected path, you will have to update manually.");
                Meowtils.error("Unexpected jar path, you will have to update manually. " + jar);
                return null;
            }

            return jar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JsonObject fetchLatest() throws Exception {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(MEOWTILS_LATEST).openConnection();
            conn.setRequestProperty("User-Agent", "Meowtils-Updater");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                return new JsonParser().parse(reader).getAsJsonObject();
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static int compareVersions(String a, String b) {
        String baseA = a.contains("-") ? a.substring(0, a.indexOf('-')) : a;
        String baseB = b.contains("-") ? b.substring(0, b.indexOf('-')) : b;
        String[] splitA = baseA.split("\\.");
        String[] splitB = baseB.split("\\.");

        int length = Math.max(splitA.length, splitB.length);
        for (int i = 0; i < length; i++) {
            int vA = (i < splitA.length) ? parseInt(splitA[i]) : 0;
            int vB = (i < splitB.length) ? parseInt(splitB[i]) : 0;
            if (vA != vB) return Integer.compare(vA, vB);
        }

        boolean preA = a.contains("-");
        boolean preB = b.contains("-");
        if (preA && !preB) return -1;
        if (!preA && preB) return 1;

        String[] subA = a.substring(a.indexOf('-') + 1).split("[^\\d]+");
        String[] subB = b.substring(b.indexOf('-') + 1).split("[^\\d]+");
        for (int j = 0; j < Math.max(subA.length, subB.length); j++) {
            int vA = (j < subA.length) ? parseInt(subA[j]) : 0;
            int vB = (j < subB.length) ? parseInt(subB[j]) : 0;
            if (vA != vB) return Integer.compare(vA, vB);
        }
        return 0;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}