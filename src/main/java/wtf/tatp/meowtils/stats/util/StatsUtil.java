package wtf.tatp.meowtils.stats.util;

import com.google.gson.JsonObject;
import net.minecraft.util.EnumChatFormatting;

public class StatsUtil {

    public static String getRank(JsonObject playerData) {
        if (playerData.has("rank") && !playerData.get("rank").isJsonNull()) {
            String rank = playerData.get("rank").getAsString();
            if (!rank.equals("NORMAL")) {
                if (rank.equals("STAFF")) {
                    return "ዞ";
                }
                if (rank.equals("YOUTUBER")) {
                    return "YOUTUBE";
                }
            }
        }

        if (playerData.has("monthlyPackageRank") && !playerData.get("monthlyPackageRank").isJsonNull() &&
                "SUPERSTAR".equals(playerData.get("monthlyPackageRank").getAsString())) return "MVP++";

        if (playerData.has("newPackageRank") && !playerData.get("newPackageRank").isJsonNull()) {
            String newRank = playerData.get("newPackageRank").getAsString();
            if ("VIP_PLUS".equals(newRank)) return "VIP+";
            if ("MVP_PLUS".equals(newRank)) return "MVP+";
            return newRank;
        }

        if (playerData.has("packageRank") && !playerData.get("packageRank").isJsonNull()) {
            return playerData.get("packageRank").getAsString();
        }
        return "NONE";
    }

    public static EnumChatFormatting getPlusColor(JsonObject playerData) {
        if (playerData.has("rankPlusColor") && !playerData.get("rankPlusColor").isJsonNull()) {
            try {
                String color = playerData.get("rankPlusColor").getAsString().toUpperCase();
                return EnumChatFormatting.valueOf(color);
            } catch (IllegalArgumentException e) {
                return EnumChatFormatting.RED;
            }
        }
        return EnumChatFormatting.RED;
    }

    public static EnumChatFormatting getRankColor(String rank) {
        switch (rank) {
            case "MVP":
            case "MVP+":
                return EnumChatFormatting.AQUA;
            case "MVP++":
                return EnumChatFormatting.GOLD;
            case "VIP":
            case "VIP+":
                return EnumChatFormatting.GREEN;
            case "YOUTUBE":
            case "ዞ":
                return EnumChatFormatting.RED;
        }
        return EnumChatFormatting.GRAY;
    }

    public static String getFormattedRankDisplay(String rank, EnumChatFormatting plusColor) {
        if ("ዞ".equals(rank)) return EnumChatFormatting.RED + "[" + EnumChatFormatting.GOLD + "ዞ" + EnumChatFormatting.RED + "] ";
        if ("YOUTUBE".equals(rank)) return EnumChatFormatting.RED + "[" + EnumChatFormatting.WHITE + "YOUTUBE" + EnumChatFormatting.RED + "] ";
        if ("MVP++".equals(rank)) return EnumChatFormatting.GOLD + "[MVP" + plusColor + "++" + EnumChatFormatting.GOLD + "] ";
        if ("MVP+".equals(rank)) return EnumChatFormatting.AQUA + "[MVP" + plusColor + "+" + EnumChatFormatting.AQUA + "] ";
        if ("VIP+".equals(rank)) return EnumChatFormatting.GREEN + "[VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GREEN + "] ";
        if (!"NONE".equals(rank)) return getRankColor(rank) + "[" + rank + "] ";
        return EnumChatFormatting.GRAY + " ";
    }

    public static String formattedChannel(String channel) {
        switch (channel) {
            case "PARTY":
                return EnumChatFormatting.BLUE + "Party";
            case "ALL":
                return EnumChatFormatting.GREEN + "All";
            case "GUILD":
                return EnumChatFormatting.DARK_GREEN + "Guild";
            case "OFFICER":
                return EnumChatFormatting.DARK_AQUA + "Officer";
            case "Unknown":
                return EnumChatFormatting.RED + "Unknown";
        }
        return channel;
    }

    public static boolean isUnknown(String stat) {
        return stat.equals("Unknown");
    }

    public static JsonObject getObj(JsonObject object, String key) {
        return (object.has(key) && object.get(key).isJsonObject()) ? object.getAsJsonObject(key) : new JsonObject();
    }

    public static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        return (object.has(key) && !object.get(key).isJsonNull()) ? object.get(key).getAsBoolean() : fallback;
    }

    public static String getString(JsonObject object, String key, String fallback) {
        return (object.has(key) && !object.get(key).isJsonNull()) ? object.get(key).getAsString() : fallback;
    }

    public static int getInt(JsonObject object, String key, int fallback) {
        return (object.has(key) && !object.get(key).isJsonNull()) ? object.get(key).getAsInt() : fallback;
    }

    public static long getLong(JsonObject object, String key, long fallback) {
        return (object.has(key) && !object.get(key).isJsonNull()) ? object.get(key).getAsLong() : fallback;
    }

    public static double getDouble(JsonObject object, String key, double fallback) {
        return (object.has(key) && !object.get(key).isJsonNull()) ? object.get(key).getAsDouble() : fallback;
    }

    public static String extractValue(String html, String start, String end) {
        int startIndex = html.indexOf(start);
        if (startIndex == -1) return "";
        startIndex += start.length();
        int endIndex = html.indexOf(end, startIndex);
        if (endIndex == -1) return "";
        return html.substring(startIndex, endIndex).trim();
    }
}