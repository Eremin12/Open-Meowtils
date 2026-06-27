package wtf.tatp.meowtils.manager;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class TextureManager {

    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();

    public static ResourceLocation get(String path) {
        return CACHE.computeIfAbsent(path, TextureManager::load);
    }

    private static ResourceLocation load(String path) {
        try (InputStream inputStream = TextureManager.class.getClassLoader().getResourceAsStream("meowtils/" + path)) {
            if (inputStream == null) throw new IllegalStateException("Missing textures: " + path);

            BufferedImage image = ImageIO.read(inputStream);
            DynamicTexture texture = new DynamicTexture(image);

            String key = "meowtils_asset/" + path;
            ResourceLocation resourceLocation = new ResourceLocation(key);

            Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, texture);
            return resourceLocation;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }
}