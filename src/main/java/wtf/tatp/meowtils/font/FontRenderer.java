package wtf.tatp.meowtils.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FontRenderer {

    private final Font font;
    private final int textureSize = 1024;
    private final Map<Character, CharacterInfo> charMap = new HashMap<>();
    private final ResourceLocation textureLocation;
    private final float originalFontSize;
    private final net.minecraft.client.gui.FontRenderer mcFontRenderer = Minecraft.getMinecraft().fontRendererObj;
    private static final int PADDING = 2;

    public FontRenderer(Font font) {
        this.font = font;
        this.originalFontSize = font.getSize2D();
        DynamicTexture texture = bakeFontAtlas();
        ResourceLocation location = new ResourceLocation("font_renderer");
        Minecraft.getMinecraft().getTextureManager().loadTexture(location, texture);
        this.textureLocation = location;
    }

    private DynamicTexture bakeFontAtlas() {
        BufferedImage atlas = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlas.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setFont(this.font);
        g.setColor(Color.WHITE);

        FontRenderContext fontRenderContext = g.getFontRenderContext();

        int x = PADDING;
        int y = PADDING;
        int lineHeight = 0;

        for (int i = 32; i < 256; i++) {
            char c = (char) i;

            GlyphVector glyphVector = this.font.createGlyphVector(fontRenderContext, String.valueOf(c));
            Rectangle bounds = glyphVector.getPixelBounds(null, 0.0F, 0.0F);
            float advance = glyphVector.getGlyphMetrics(0).getAdvanceX();

            if (c == ' ') {
                this.charMap.put(c, new CharacterInfo(0, 0, 0, 0, 0.0F, 0.0F, advance));
            } else if (bounds.width > 0 && bounds.height > 0) {
                int margin = 3;
                int extraWidth = bounds.width + margin * 2;
                int extraHeight = bounds.height + margin * 2;

                if (x + extraWidth + PADDING >= textureSize) {
                    x = PADDING;
                    y += lineHeight + PADDING;
                    lineHeight = 0;
                }

                g.drawGlyphVector(glyphVector, (x + margin - bounds.x), (y + margin - bounds.y));
                if (extraHeight > lineHeight) lineHeight = extraHeight;
                this.charMap.put(c, new CharacterInfo(x, y, extraWidth, extraHeight, (bounds.x - margin), (bounds.y - margin), advance));
                x += extraWidth + PADDING;
            }
        }
        g.dispose();
        DynamicTexture dynamicTexture = new DynamicTexture(atlas);

        GlStateManager.bindTexture(dynamicTexture.getGlTextureId());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        return dynamicTexture;
    }

    public void drawString(String text, float x, float y, int color, float size) {
        drawColoredText(text, x, y, color, size, false);
    }

    public void drawStringWithShadow(String text, float x, float y, int color, float size) {
        if (text == null || text.isEmpty()) return;
        float shadowOffset = 0.4F;
        drawColoredText(text, x + shadowOffset, y + shadowOffset, color, size, true);
        drawColoredText(text, x, y, color, size, false);
    }

    public void drawStringWithLightShadow(String text, float x, float y, int color, float size) {
        if (text == null || text.isEmpty()) return;
        float shadowOffset = 0.1F;
        drawColoredText(text, x + shadowOffset, y + shadowOffset, color, size, true);
        drawColoredText(text, x, y, color, size, false);
    }

    public void drawScaledString(String text, float x, float y, int color, float size) {
        drawColoredText(text, x, y + 7.5F * size / 10.0F, color, size, false);
    }

    public void drawScaledStringWithShadow(String text, float x, float y, int color, float size) {
        if (text == null || text.isEmpty()) return;
        float shadowOffset = 0.4F;
        drawColoredText(text, x + shadowOffset, y + shadowOffset + 7.5F * size / 10.0F, color, size, true);
        drawColoredText(text, x, y + 7.5F * size / 10.0F, color, size, false);
    }

    public void drawScaledStringWithLightShadow(String text, float x, float y, int color, float size) {
        if (text == null || text.isEmpty()) return;
        float shadowOffset = 0.1F;
        drawColoredText(text, x + shadowOffset, y + shadowOffset + 7.5F * size / 10.0F, color, size, true);
        drawColoredText(text, x, y + 7.5F * size / 10.0F, color, size, false);
    }

    public float getStringWidth(String text, float size) {
        if (text == null || text.isEmpty()) return 0.0F;

        float totalWidth = 0.0F;
        float customScale = size / this.originalFontSize;
        float mcScale = size / 10.0F;

        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c == '\u00a7' && i + 1 < len) {
                i++;
            } else {
                CharacterInfo info = this.charMap.get(c);
                if (info != null) {
                    totalWidth += info.advance;
                } else {
                    totalWidth += this.mcFontRenderer.getCharWidth(c) * mcScale / customScale;
                }
            }
        }
        return totalWidth * customScale;
    }

    private void drawColoredText(String text, float x, float y, int baseColor, float size, boolean shadow) {
        if (text == null || text.isEmpty()) return;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();

        GlStateManager.translate(x, y, 0.0F);
        float scale = size / this.originalFontSize;
        GlStateManager.scale(scale, scale, 1.0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(this.textureLocation);

        float r = (baseColor >> 16 & 0xFF) / 255.0F;
        float g = (baseColor >> 8 & 0xFF) / 255.0F;
        float b = (baseColor & 0xFF) / 255.0F;
        float a = (baseColor >> 24 & 0xFF) / 255.0F;

        if (shadow) {
            r *= 0.25F;
            g *= 0.25F;
            b *= 0.25F;
        }

        int currentColorRed = (int) (r * 255.0F);
        int currentColorGreen = (int) (g * 255.0F);
        int currentColorBlue = (int) (b * 255.0F);
        int currentColorAlpha = (int) (a * 255.0F);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        float cursorX = 0.0F;
        int len = text.length();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);

            if (c == '\u00a7' && i + 1 < len) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                i++;

                if (code == 'r') {
                    currentColorRed = (int) (r * 255.0F);
                    currentColorGreen = (int) (g * 255.0F);
                    currentColorBlue = (int) (b * 255.0F);
                } else {
                    int rgb = getRGBFromCode(code);
                    if (rgb != -1) {
                        float cr = (rgb >> 16 & 0xFF) / 255.0F;
                        float cg = (rgb >> 8 & 0xFF) / 255.0F;
                        float cb = (rgb & 0xFF) / 255.0F;

                        if (shadow) { cr *= 0.25F; cg *= 0.25F; cb *= 0.25F; }

                        currentColorRed = (int) (cr * 255.0F);
                        currentColorGreen = (int) (cg * 255.0F);
                        currentColorBlue = (int) (cb * 255.0F);
                    }
                }
            } else {
                CharacterInfo info = this.charMap.get(c);

                if (info == null) {
                    tessellator.draw();

                    GlStateManager.pushMatrix();

                    float mcScale = size / 10.0F;
                    float relativeScale = mcScale / scale;

                    GlStateManager.scale(relativeScale, relativeScale, 1.0F);

                    int finalColor = currentColorAlpha << 24 | currentColorRed << 16 | currentColorGreen << 8 | currentColorBlue;
                    this.mcFontRenderer.drawString(String.valueOf(c), cursorX / relativeScale, -8.0F, finalColor, false);
                    GlStateManager.popMatrix();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                    Minecraft.getMinecraft().getTextureManager().bindTexture(this.textureLocation);
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

                    cursorX += this.mcFontRenderer.getCharWidth(c) * mcScale / scale;
                } else {
                    if (info.width > 0 && info.height > 0) {
                        float u1 = info.textureX / (float) textureSize;
                        float v1 = info.textureY / (float) textureSize;
                        float u2 = (info.textureX + info.width) / (float) textureSize;
                        float v2 = (info.textureY + info.height) / (float) textureSize;

                        float quadX = cursorX + info.offsetX;
                        float quadY = info.offsetY;

                        worldRenderer.pos(quadX, (quadY + info.height), 0.0).tex(u1, v2).color(currentColorRed, currentColorGreen, currentColorBlue, currentColorAlpha).endVertex();
                        worldRenderer.pos((quadX + info.width), (quadY + info.height), 0.0).tex(u2, v2).color(currentColorRed, currentColorGreen, currentColorBlue, currentColorAlpha).endVertex();
                        worldRenderer.pos((quadX + info.width), quadY, 0.0).tex(u2, v1).color(currentColorRed, currentColorGreen, currentColorBlue, currentColorAlpha).endVertex();
                        worldRenderer.pos(quadX, quadY, 0.0).tex(u1, v1).color(currentColorRed, currentColorGreen, currentColorBlue, currentColorAlpha).endVertex();
                    }
                    cursorX += info.advance;
                }
            }
        }
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static int getRGBFromCode(char code) {
        if (code >= '0' && code <= '9') return Minecraft.getMinecraft().fontRendererObj.getColorCode(code);
        if (code >= 'a' && code <= 'f') return Minecraft.getMinecraft().fontRendererObj.getColorCode(code);
        return -1;
    }

    private static class CharacterInfo {
        final int textureX;
        final int textureY;
        final int width;
        final int height;
        final float offsetX;
        final float offsetY;
        final float advance;

        CharacterInfo(int textureX, int textureY, int width, int height, float offsetX, float offsetY, float advance) {
            this.textureX = textureX;
            this.textureY = textureY;
            this.width = width;
            this.height = height;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.advance = advance;
        }
    }
}