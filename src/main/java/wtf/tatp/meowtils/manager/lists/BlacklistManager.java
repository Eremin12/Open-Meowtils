package wtf.tatp.meowtils.manager.lists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.util.Util;

public class BlacklistManager {

    private static final File FILE = Meowtils.MEOWTILS_BLACKLIST;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private static Map<String, String> blacklist = new HashMap<>();

    public static final Set<String> NON_BLATANT = new HashSet<>(Arrays.asList(
            "cheating", "aimassist", "autoclicker", "reach", "velocity", "esp",
            "fakelag", "legit scaffold", "fastplace", "closet", "scaffold"
    ));

    public static final Set<String> BLATANT = new HashSet<>(Arrays.asList(
            "antivoid", "bednuker", "fly", "keepsprint", "killaura", "nofall",
            "noslow", "bhop", "antifireball", "safewalk", "blink", "autoblock",
            "blatant", "strafe", "snipe", "sniper", "sniping"
    ));

    static {
        loadBlacklist();
    }

    public static void add(String uuidOrName, String reason) {
        blacklist.put(uuidOrName, System.currentTimeMillis() + " " + reason);
        saveBlacklist();
    }

    public static void remove(String key) {
        blacklist.remove(key);
        saveBlacklist();
    }

    public static void appendReason(String key, String reason) {
        String existingEntry = getEntry(key);
        if (existingEntry == null) {
            add(key, reason);
            return;
        }

        String[] parts = existingEntry.split(" ", 2);
        String prevReasons = (parts.length > 1) ? parts[1] : "";

        Set<String> reasons = new LinkedHashSet<>(Arrays.asList(prevReasons.split(" \\| ")));
        reasons.add(reason);

        remove(key);
        add(key, String.join(" | ", reasons));
    }

    public static boolean isBlacklisted(String uuidOrName) {
        return blacklist.containsKey(uuidOrName);
    }

    public static String getEntry(String uuidOrName) {
        return blacklist.get(uuidOrName);
    }

    public static String getFormattedEntry(String uuidOrName) {
        String raw = blacklist.get(uuidOrName);
        if (raw == null) return null;

        String[] parts = raw.split(" ", 2);
        if (parts.length < 2) return null;

        long timeMs = Long.parseLong(parts[0]);
        String reasonsRaw = parts[1];

        return EnumChatFormatting.DARK_GRAY + Util.formatTimestamp(timeMs) +
                EnumChatFormatting.GRAY + " for: " + colorReasons(reasonsRaw);
    }

    public static EnumChatFormatting getReasonColor(String entryOrReasons) {
        if (entryOrReasons == null) {
            return EnumChatFormatting.DARK_GREEN;
        }

        String[] split = entryOrReasons.split(" ", 2);
        String reasons = (split.length == 2) ? split[1] : entryOrReasons;

        String[] parts = reasons.toLowerCase().split("\\|");

        for (String part : parts) {
            String reason = part.trim();

            if (BLATANT.contains(reason)) {
                return EnumChatFormatting.DARK_RED;
            }

            if (NON_BLATANT.contains(reason)) {
                return EnumChatFormatting.GOLD;
            }
        }

        return EnumChatFormatting.DARK_GREEN;
    }

    public static String colorReasons(String raw) {
        String[] parts = raw.split(" \\| ");
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String reason = parts[i].trim();

            EnumChatFormatting color = getReasonColor(reason);
            out.append(color).append(reason.toLowerCase());

            if (i < parts.length - 1) {
                out.append(EnumChatFormatting.DARK_GRAY).append(" | ");
            }
        }

        return out.toString();
    }

    public static String formatReasons(String[] reasonParts) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < reasonParts.length; i++) {
            String current = reasonParts[i].toLowerCase();
            if (i < reasonParts.length - 1) {
                String combined = current + " " + reasonParts[i + 1].toLowerCase();
                if (NON_BLATANT.contains(combined) || BLATANT.contains(combined)) {
                    result.add(combined);
                    i++;
                    continue;
                }
            }
            result.add(current);
        }
        return String.join(" | ", result);
    }

    private static void loadBlacklist() {
        if (!FILE.exists()) return;
        try (Reader reader = new FileReader(FILE)) {
            Map<String, String> loaded = gson.fromJson(reader, TYPE);
            if (loaded != null) {
                blacklist = loaded;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveBlacklist() {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(blacklist, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}