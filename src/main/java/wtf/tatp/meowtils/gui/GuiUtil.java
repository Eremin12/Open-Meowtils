package wtf.tatp.meowtils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.ExpandComponent;
import wtf.tatp.meowtils.gui.hudeditor.HudEditor;
import wtf.tatp.meowtils.manager.TextureManager;
import wtf.tatp.meowtils.module.meowtils.GUI;

public class GuiUtil {

    private static final ResourceLocation SUB_COMPONENT_BACKGROUND = TextureManager.get("textures/gui/sub_component_background.png");
    private static final ResourceLocation SUB_COMPONENT_BACKGROUND_BOTTOM = TextureManager.get("textures/gui/sub_component_background_bottom.png");
    private static final ResourceLocation SUB_COMPONENT_BACKGROUND_LAST = TextureManager.get("textures/gui/sub_component_background_last.png");
    private static final ResourceLocation SUB_COMPONENT_BACKGROUND_TOP = TextureManager.get("textures/gui/sub_component_background_top.png");
    private static final ResourceLocation SUB_COMPONENT_BACKGROUND_ONLY = TextureManager.get("textures/gui/sub_component_background_only.png");
    private static final ResourceLocation SUB_COMPONENT_BACKGROUND_ONLY_LAST = TextureManager.get("textures/gui/sub_component_background_only_last.png");
    private static final ResourceLocation EXPANDED_BACKGROUND = TextureManager.get("textures/gui/expandpart/expand_background.png");
    private static final ResourceLocation EXPANDED_BACKGROUND_LAST = TextureManager.get("textures/gui/expandpart/expand_background_last.png");

    public static float customGuiScale() {
        Minecraft mc = Minecraft.getMinecraft();
        GUI gui = Module.get(GUI.class);
        String guiScale = (gui != null) ? gui.scale : "Normal";
        switch (guiScale) {
            case "Auto":
                return mc.displayWidth / 1920.0F;
            case "Tiny":
                return 0.8F;
            case "Small":
                return 0.9F;
            case "Normal":
                return 1.0F;
            case "Large":
                return 1.1F;
            case "Huge":
                return 1.2F;
        }
        return 1.0F;
    }

    public static float getScale() {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleSetting = mc.gameSettings.guiScale;
        int scaled = (scaleSetting == 0) ? 1000 : scaleSetting;
        float baseScale = 3.0F;
        int effectiveScale = 0;

        while (effectiveScale < scaled && mc.displayWidth / (effectiveScale + 1) >= 320 && mc.displayHeight / (effectiveScale + 1) >= 240) {
            effectiveScale++;
        }
        return baseScale / effectiveScale * customGuiScale();
    }

    public static ResourceLocation getComponentBackground(ModuleComponent component, Component owner) {
        boolean above = component.isComponentAbove(owner);
        boolean below = component.isComponentBelow(owner);
        boolean lastModule = !component.isModuleBelow();
        boolean last = (lastModule && !component.isComponentBelow(owner));

        if (owner.nested) {
            ExpandComponent e = owner.expandParent;
            boolean isLast = (e != null && e.isLastSub(owner) && lastModule && !component.isComponentBelow(e));
            return isLast ? EXPANDED_BACKGROUND_LAST : EXPANDED_BACKGROUND;
        }

        if (owner instanceof ExpandComponent) {
            ExpandComponent e = (ExpandComponent) owner;
            if (e.option.getState() && last) {
                return SUB_COMPONENT_BACKGROUND;
            }
        }

        if (above && below)
            return SUB_COMPONENT_BACKGROUND;
        if (above && component.isModuleBelow())
            return SUB_COMPONENT_BACKGROUND_BOTTOM;
        if (above && last)
            return SUB_COMPONENT_BACKGROUND_LAST;
        if (below)
            return SUB_COMPONENT_BACKGROUND_TOP;
        if (last) {
            return SUB_COMPONENT_BACKGROUND_ONLY_LAST;
        }
        return SUB_COMPONENT_BACKGROUND_ONLY;
    }

    public static boolean inEditor() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.currentScreen instanceof HudEditor;
    }

    public static int[] getHudBounds(String longestText, int lineCount, float scale) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.fontRendererObj == null) {
            return new int[] { 1, 1 };
        }

        if (scale == 0.0F) {
            scale = 1.0F;
        }

        float lineHeight = mc.fontRendererObj.FONT_HEIGHT * scale;
        float lineGap = 3.0F * scale;
        int height = (int) (lineCount * lineHeight + (lineCount - 1) * lineGap);
        int width = (int) (mc.fontRendererObj.getStringWidth(longestText) * scale);

        return new int[] { width, height };
    }

    public static float getRed() {
        GUI gui = Module.get(GUI.class);
        return (gui != null) ? gui.red / 255.0F : 1.0F;
    }

    public static float getGreen() {
        GUI gui = Module.get(GUI.class);
        return (gui != null) ? gui.green / 255.0F : 1.0F;
    }

    public static float getBlue() {
        GUI gui = Module.get(GUI.class);
        return (gui != null) ? gui.blue / 255.0F : 1.0F;
    }
}