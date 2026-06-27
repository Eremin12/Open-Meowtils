package wtf.tatp.meowtils.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.hypixel.AccountHider;

public class SkinManager {

    private static String lastSkin = null;
    private static ResourceLocation cachedSkin = null;

    public static ResourceLocation getSkin(String selectedSkin) {
        if (!selectedSkin.equalsIgnoreCase(lastSkin)) {
            lastSkin = selectedSkin;
            cachedSkin = loadSkin(selectedSkin);
        }
        return cachedSkin;
    }

    private static ResourceLocation loadSkin(String id) {
        try {
            AccountHider accountHider = Module.get(AccountHider.class);
            if (accountHider == null) return null;

            String skin = accountHider.skinLocation.replace(".png", "");
            File file = new File(Minecraft.getMinecraft().mcDataDir, "meowtils/custom_skins/" + skin + ".png");

            if (!file.exists()) return null;

            BufferedImage image = ImageIO.read(file);
            DynamicTexture texture = new DynamicTexture(image);

            ResourceLocation resourceLocation = new ResourceLocation("meowtils_skin/" + id);
            Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, texture);
            return resourceLocation;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}