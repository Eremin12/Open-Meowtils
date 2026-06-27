package wtf.tatp.meowtils.manager.log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import wtf.tatp.meowtils.Meowtils;

public class LogManager {

    private static final String LOG_UPLOAD_URL = "https://api.mclo.gs/1/log";
    private static boolean attached = false;
    private static boolean initialized = false;

    public static void init() {
        attachAppender();
    }

    private static void attachAppender() {
        if (attached) return;
        attached = true;

        LoggerContext context = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        Configuration cfg = context.getConfiguration();
        LogAppender appender = new LogAppender("MeowtilsLog");

        appender.start();

        LoggerConfig root = cfg.getLoggers().get("");
        if (root == null) {
            for (LoggerConfig l : cfg.getLoggers().values()) {
                if (l.getName().isEmpty()) {
                    root = l;
                    break;
                }
            }
        }

        if (root != null) {
            root.addAppender(appender, null, null);
            context.updateLoggers();
        }
    }

    public static void write(String level, String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String line = "[" + timestamp + "] [" + level + "]: " + message + "\n";

        try (FileOutputStream fileOutput = new FileOutputStream(Meowtils.MEOWTILS_LOG, initialized);
             OutputStreamWriter writer = new OutputStreamWriter(fileOutput, StandardCharsets.UTF_8)) {
            writer.write(line);
            initialized = true;
        } catch (IOException e) {
            Meowtils.error("Unable to write to meowtils.log: " + e);
        }
    }

    public static File findLog() {
        File f = Meowtils.MEOWTILS_LOG;
        return f.exists() ? f : null;
    }

    public static void postLog() {
        File logFile = findLog();
        if (logFile == null) {
            Meowtils.warn("Unable to upload log: No log file found");
            Meowtils.addMessage(EnumChatFormatting.RED + "Unable to upload log: No log file found");
            return;
        }

        Meowtils.info("Uploading meowtils.log to mclo.gs..");
        Meowtils.addMessage("Uploading meowtils.log to mclo.gs..");

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String content = new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("content", content);
                requestBody.addProperty("source", "Meowtils");

                byte[] body = requestBody.toString().getBytes(StandardCharsets.UTF_8);

                conn = (HttpURLConnection) new URL(LOG_UPLOAD_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Content-Length", String.valueOf(body.length));
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                try (OutputStream outputStream = conn.getOutputStream()) {
                    outputStream.write(body);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JsonObject json = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
                    if (json.get("success").getAsBoolean()) {
                        String url = json.get("url").getAsString();
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
                        Meowtils.info("Uploaded meowtils.log. URL copied: " + url);
                        Meowtils.addMessage("Uploaded log. URL: " + url);
                    } else {
                        Meowtils.error("Error with mclo.gs when uploading meowtils.log: " + json.get("error").getAsString());
                        Meowtils.addMessage(EnumChatFormatting.RED + "Error when uploading log to mclo.gs");
                    }
                }
            } catch (Exception e) {
                Meowtils.error("Upload to mclo.gs failed: " + e);
                Meowtils.addMessage(EnumChatFormatting.RED + "Error when uploading log to mclo.gs");
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }, "Meowtils-Log-Upload").start();
    }
}