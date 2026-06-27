package wtf.tatp.meowtils.font;

import java.awt.Font;
import java.io.InputStream;

public class FontManager {

    public static Font load(String path, float size) {
        try (InputStream inputStream = FontManager.class.getClassLoader().getResourceAsStream("meowtils/fonts/" + path)) {
            if (inputStream == null) throw new IllegalStateException("Missing font: " + path);

            return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font: " + path, e);
        }
    }
}