package wtf.tatp.meowtils.gui.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.config.GuiConfig;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.TextureManager;

public class Frame {

    private static final ResourceLocation CATEGORY_OPEN = TextureManager.get("textures/gui/category_expanded.png");
    private static final ResourceLocation CATEGORY_CLOSED = TextureManager.get("textures/gui/category_not_expanded.png");
    private static final ResourceLocation ARROW_UP = TextureManager.get("textures/gui/arrow_up.png");
    private static final ResourceLocation ARROW_DOWN = TextureManager.get("textures/gui/arrow_down.png");
    private static final ResourceLocation DEFAULT_ICON = TextureManager.get("textures/gui/icons/default.png");
    private static final Map<Module.Category, ResourceLocation> ICON_CACHE = new EnumMap<>(Module.Category.class);
    private static final Color CATEGORY_COLOR = new Color(255, 255, 255);
    private static final GuiConfig GUI = ConfigManager.guiConfig;

    public ArrayList<Component> components;
    public Module.Category category;
    public boolean open;
    private int width;
    private int y;
    private int x;
    private final int barHeight;
    private boolean isDragging;
    public int dragX;
    public int dragY;
    private boolean wasDragging = false;
    public int scrollOffset = 0;
    public int scrollTarget = 0;
    public float visibleGUI = 200.0F;
    public int lastTotalHeight = -1;

    static {
        for (Module.Category cat : Module.Category.values()) {
            ResourceLocation resourceLocation = TextureManager.get("textures/gui/icons/" + cat.name().toLowerCase() + ".png");
            ICON_CACHE.put(cat, resourceLocation);
        }
    }

    public Frame(Module.Category cat) {
        this.components = new ArrayList<>();
        this.category = cat;
        this.width = 85;
        this.barHeight = 11;
        this.dragX = 0;

        loadFromConfig();

        int height = this.barHeight;
        for (Module mod : Module.getCategoryModules(this.category)) {
            this.components.add(new ModuleComponent(mod, this, height));
            height += 12;
        }
        this.isDragging = false;
    }

    public ArrayList<Component> getComponents() {
        return this.components;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y + this.scrollOffset;
    }

    public int getWidth() {
        return this.width;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        saveToConfig();
    }

    public void rebuild() {
        this.components.clear();
        int height = this.barHeight;
        for (Module mod : Module.getCategoryModules(this.category)) {
            this.components.add(new ModuleComponent(mod, this, height));
            height += 12;
        }
        loadFromConfig();
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            setX(mouseX - this.dragX);
            setY(mouseY - this.dragY);
        }

        if (this.wasDragging && !this.isDragging) {
            saveToConfig();
        }
        this.wasDragging = this.isDragging;
    }

    public boolean isWithinHeader(int x, int y) {
        return (x >= this.x - 1 && x <= this.x + this.width - 19 && y >= this.y - 3 && y <= this.y + this.barHeight + 2 + this.scrollOffset);
    }

    public void renderFrame() {
        Minecraft mc = Minecraft.getMinecraft();

        if (this.category == Module.Category.Extensions && !Meowtils.loadedExtensions) {
            return;
        }

        this.width = 100;
        int renderY = this.y + this.scrollOffset;

        mc.getTextureManager().bindTexture((this.open && !this.components.isEmpty()) ? CATEGORY_OPEN : CATEGORY_CLOSED);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(this.x - 1, renderY - 3, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        GL11.glPushMatrix();
        int centeredX = this.x;
        Meowtils.fontRenderer.drawStringWithShadow(this.category.name(), (centeredX + 15), (renderY + 9), CATEGORY_COLOR.getRGB(), 9.0F);

        mc.getTextureManager().bindTexture(this.open ? ARROW_DOWN : ARROW_UP);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture(this.x + 70, renderY + (this.open ? 2 : 3), 0.0F, 0.0F, 5, 5, 5.0F, 5.0F);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glPopMatrix();

        if (this.open && !this.components.isEmpty()) {
            for (Component component : this.components) {
                GlStateManager.pushMatrix();
                component.render();
                GlStateManager.popMatrix();
            }
        }

        ResourceLocation icon = ICON_CACHE.getOrDefault(this.category, DEFAULT_ICON);
        mc.getTextureManager().bindTexture(icon);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawScaledCustomSizeModalRect(this.x + 3, renderY + 1, 0.0F, 0.0F, 26, 26, 9, 9, 26.0F, 26.0F);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void refresh() {
        int offset = this.barHeight;
        for (Component component : this.components) {
            component.setOff(offset);
            offset += component.getHeight();
        }

        int newHeight = offset;
        int oldHeight = this.lastTotalHeight;

        if (oldHeight != -1 && newHeight < oldHeight) {
            resetScroll();
        }
        this.lastTotalHeight = newHeight;
    }

    public boolean isMouseOverFrame(int mouseX, int mouseY) {
        int totalHeight = this.barHeight;
        if (this.open) {
            for (Component component : this.components) {
                totalHeight += component.getHeight();
            }
        }
        int width = 80;
        return (mouseX >= this.x && mouseX <= this.x + width && mouseY >= this.y + this.scrollOffset && mouseY <= this.y + this.scrollOffset + totalHeight);
    }

    public void clampScroll() {
        int contentHeight = 0;
        if (this.open) {
            for (Component component : this.components) contentHeight += component.getHeight();
        }
        int visiblePixels = (int) (this.visibleGUI / GuiUtil.getScale());

        int minScroll = -Math.max(0, contentHeight - visiblePixels);
        int maxScroll = 0;

        this.scrollTarget = Math.max(minScroll, Math.min(this.scrollTarget, maxScroll));
    }

    public void updateScroll() {
        if (this.scrollOffset < this.scrollTarget) {
            this.scrollOffset = Math.min(this.scrollOffset + 1, this.scrollTarget);
        } else if (this.scrollOffset > this.scrollTarget) {
            this.scrollOffset = Math.max(this.scrollOffset - 1, this.scrollTarget);
        }
    }

    public void resetScroll() {
        this.scrollOffset = 0;
        this.scrollTarget = 0;
    }

    private void loadFromConfig() {
        switch (this.category) {
            case Meowtils:
                this.x = GUI.meowtilsCategoryX;
                this.y = GUI.meowtilsCategoryY;
                this.open = GUI.meowtilsCategoryExpanded;
                return;
            case Hypixel:
                this.x = GUI.hypixelCategoryX;
                this.y = GUI.hypixelCategoryY;
                this.open = GUI.hypixelCategoryExpanded;
                return;
            case Bedwars:
                this.x = GUI.bedwarsCategoryX;
                this.y = GUI.bedwarsCategoryY;
                this.open = GUI.bedwarsCategoryExpanded;
                return;
            case Skywars:
                this.x = GUI.skywarsCategoryX;
                this.y = GUI.skywarsCategoryY;
                this.open = GUI.skywarsCategoryExpanded;
                return;
            case Render:
                this.x = GUI.renderCategoryX;
                this.y = GUI.renderCategoryY;
                this.open = GUI.renderCategoryExpanded;
                return;
            case Antisnipe:
                this.x = GUI.antisnipeCategoryX;
                this.y = GUI.antisnipeCategoryY;
                this.open = GUI.antisnipeCategoryExpanded;
                return;
            case Utility:
                this.x = GUI.utilityCategoryX;
                this.y = GUI.utilityCategoryY;
                this.open = GUI.utilityCategoryExpanded;
                return;
            case Advanced:
                this.x = GUI.advancedCategoryX;
                this.y = GUI.advancedCategoryY;
                this.open = GUI.advancedCategoryExpanded;
                return;
            case Extensions:
                this.x = GUI.extensionCategoryX;
                this.y = GUI.extensionCategoryY;
                this.open = GUI.extensionCategoryExpanded;
                return;
        }
        this.x = 5;
        this.y = 5;
        this.open = false;
    }

    private void saveToConfig() {
        switch (this.category) {
            case Meowtils:
                GUI.meowtilsCategoryX = this.x;
                GUI.meowtilsCategoryY = this.y;
                GUI.meowtilsCategoryExpanded = this.open;
                break;
            case Hypixel:
                GUI.hypixelCategoryX = this.x;
                GUI.hypixelCategoryY = this.y;
                GUI.hypixelCategoryExpanded = this.open;
                break;
            case Skywars:
                GUI.skywarsCategoryX = this.x;
                GUI.skywarsCategoryY = this.y;
                GUI.skywarsCategoryExpanded = this.open;
                break;
            case Bedwars:
                GUI.bedwarsCategoryX = this.x;
                GUI.bedwarsCategoryY = this.y;
                GUI.bedwarsCategoryExpanded = this.open;
                break;
            case Render:
                GUI.renderCategoryX = this.x;
                GUI.renderCategoryY = this.y;
                GUI.renderCategoryExpanded = this.open;
                break;
            case Antisnipe:
                GUI.antisnipeCategoryX = this.x;
                GUI.antisnipeCategoryY = this.y;
                GUI.antisnipeCategoryExpanded = this.open;
                break;
            case Utility:
                GUI.utilityCategoryX = this.x;
                GUI.utilityCategoryY = this.y;
                GUI.utilityCategoryExpanded = this.open;
                break;
            case Advanced:
                GUI.advancedCategoryX = this.x;
                GUI.advancedCategoryY = this.y;
                GUI.advancedCategoryExpanded = this.open;
                break;
            case Extensions:
                GUI.extensionCategoryX = this.x;
                GUI.extensionCategoryY = this.y;
                GUI.extensionCategoryExpanded = this.open;
                break;
        }
        ConfigManager.save();
    }
}