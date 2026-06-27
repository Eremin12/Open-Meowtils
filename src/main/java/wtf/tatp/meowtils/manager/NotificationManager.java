package wtf.tatp.meowtils.manager;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.util.ColorUtil;

public class NotificationManager {

    private static final ResourceLocation NOTIFICATION = TextureManager.get("textures/notification/notification.png");
    private static final ResourceLocation WARN_ICON = TextureManager.get("textures/notification/icon/warning.png");
    private static final ResourceLocation ALERT_ICON = TextureManager.get("textures/notification/icon/alert.png");
    private static final ResourceLocation INFO_ICON = TextureManager.get("textures/notification/icon/info.png");
    private static final Color DARK_RED = ColorUtil.getColorFromFormatting(EnumChatFormatting.DARK_RED);
    private static final Color GOLD = ColorUtil.getColorFromFormatting(EnumChatFormatting.GOLD);
    private static final Color WHITE = ColorUtil.getColorFromFormatting(EnumChatFormatting.WHITE);
    private static final int WIDTH = 100;
    private static final int HEIGHT = 25;

    private static long displayTime = 1500L;
    private static Type selectedType = Type.INFO;
    private static String notificationTitle = "Title";
    private static float notificationTitleScale;
    private static String notificationMessage = "Message";
    private static float notificationMessageScale;
    private static long startTime = -1L;
    private static float xOffset = 0.0F;
    private static boolean displaying = false;

    public enum Type {
        INFO,
        ALERT,
        WARNING;
    }

    public static void show(String title, String message, Type type, long time) {
        startTime = System.currentTimeMillis();
        displaying = true;
        xOffset = WIDTH;
        selectedType = type;
        displayTime = time;
        notificationTitle = title;
        notificationMessage = message;
        notificationTitleScale = 7.0F;
        notificationMessageScale = 5.0F;
    }

    public static void show(String title, float titleScale, String message, float messageScale, Type type, long time) {
        show(title, message, type, time);
        notificationTitleScale = titleScale;
        notificationMessageScale = messageScale;
    }

    private static void render() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        if (!displaying) return;

        float scale = GuiUtil.getScale();
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed < 250L) {
            float progress = (float) elapsed / 250.0F;
            xOffset = WIDTH * (1.0F - progress);
        } else if (elapsed < displayTime) {
            xOffset = 0.0F;
        } else if (elapsed < displayTime + 250L) {
            float progress = (float) (elapsed - displayTime) / 250.0F;
            xOffset = WIDTH * progress;
        } else {
            displaying = false;
            return;
        }

        int x = (int) (sr.getScaledWidth() / scale - WIDTH + xOffset);
        int y = (int) (sr.getScaledHeight() / scale - HEIGHT - 10.0F);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.scale(scale, scale, scale);
        mc.getTextureManager().bindTexture(NOTIFICATION);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, WIDTH, HEIGHT, WIDTH, HEIGHT);

        if (selectedType == Type.INFO) {
            renderIcon(INFO_ICON, WHITE, x, y);
        } else if (selectedType == Type.ALERT) {
            renderIcon(ALERT_ICON, GOLD, x, y);
        } else if (selectedType == Type.WARNING) {
            renderIcon(WARN_ICON, DARK_RED, x, y);
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void renderIcon(ResourceLocation icon, Color color, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x + 5, y + 4, 0.0F, 0.0F, 9, 9, 9.0F, 9.0F);

        Meowtils.fontRenderer.drawScaledStringWithShadow(notificationTitle, (x + 18), (y + 6), color.getRGB(), notificationTitleScale);
        Meowtils.fontRenderer.drawScaledStringWithShadow(notificationMessage, (x + 19), (y + 16), WHITE.getRGB(), notificationMessageScale);
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        render();
    }
}