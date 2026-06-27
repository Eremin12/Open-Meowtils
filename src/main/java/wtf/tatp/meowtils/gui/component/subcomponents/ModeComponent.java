package wtf.tatp.meowtils.gui.component.subcomponents;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class ModeComponent extends Component {

    private static final ResourceLocation MODE_TOP = TextureManager.get("textures/gui/modepart/mode_top.png");
    private static final ResourceLocation MODE_TOP_OUTLINE = TextureManager.get("textures/gui/modepart/mode_top_outline.png");
    private static final ResourceLocation MODE_TOP_HOVER = TextureManager.get("textures/gui/modepart/mode_top_hover.png");
    private static final ResourceLocation MODE_NOT_EXPANDED = TextureManager.get("textures/gui/modepart/mode_not_expanded.png");
    private static final ResourceLocation MODE_NOT_EXPANDED_OUTLINE = TextureManager.get("textures/gui/modepart/mode_not_expanded_outline.png");
    private static final ResourceLocation MODE_NOT_EXPANDED_HOVER = TextureManager.get("textures/gui/modepart/mode_not_expanded_hover.png");
    private static final ResourceLocation MODE_BOTTOM = TextureManager.get("textures/gui/modepart/mode_bottom.png");
    private static final ResourceLocation MODE_BOTTOM_OUTLINE = TextureManager.get("textures/gui/modepart/mode_bottom_outline.png");
    private static final ResourceLocation MODE_BOTTOM_HOVER = TextureManager.get("textures/gui/modepart/mode_bottom_hover.png");
    private static final ResourceLocation MODE_MIDDLE = TextureManager.get("textures/gui/modepart/mode_middle.png");
    private static final ResourceLocation MODE_MIDDLE_OUTLINE = TextureManager.get("textures/gui/modepart/mode_middle_outline.png");
    private static final ResourceLocation MODE_MIDDLE_HOVER = TextureManager.get("textures/gui/modepart/mode_middle_hover.png");
    private static final ResourceLocation MODE_ARROW_DOWN = TextureManager.get("textures/gui/modepart/mode_arrow_down.png");
    private static final ResourceLocation MODE_ARROW_SIDE = TextureManager.get("textures/gui/modepart/mode_arrow_side.png");

    public final ModeValue option;
    public final ModuleComponent component;
    private final String name;
    public int offset;
    private int x;
    private int y;
    private boolean hovered;
    public boolean expanded;
    public static ModeComponent expandedPart = null;

    public boolean modeExpanded() {
        return this.expanded;
    }

    public ModeComponent(ModeValue option, ModuleComponent parent, int offset, String name) {
        this.option = option;
        this.component = parent;
        this.offset = offset;
        this.name = name;
        this.x = parent.parent.getX();
        this.y = parent.parent.getY() + offset;
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = this.component.parent.getX() - 1;
        int y = this.component.parent.getY() + 4 + this.offset;

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        renderBox(x, y, this.name + " - " + this.option.getValue(),
                (this.expanded && this.option.getModes().size() >= 2) ? MODE_TOP : MODE_NOT_EXPANDED,
                (this.expanded && this.option.getModes().size() >= 2) ? MODE_TOP_OUTLINE : MODE_NOT_EXPANDED_OUTLINE);

        if (this.hovered && !this.expanded) {
            renderBox(x, y, this.name + " - " + this.option.getValue(),
                    (this.expanded && this.option.getModes().size() >= 2) ? MODE_TOP_HOVER : MODE_NOT_EXPANDED_HOVER,
                    (this.expanded && this.option.getModes().size() >= 2) ? MODE_TOP_OUTLINE : MODE_NOT_EXPANDED_OUTLINE);
        }

        mc.getTextureManager().bindTexture(this.expanded ? MODE_ARROW_DOWN : MODE_ARROW_SIDE);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture(x + 72, y + 4, 0.0F, 0.0F, 5, 5, 5.0F, 5.0F);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderExpanded(int mouseX, int mouseY) {
        if (!this.expanded) return;
        List<String> modes = this.option.getModes();
        String current = this.option.getValue();
        List<String> displayModes = new ArrayList<>();

        for (String m : modes) {
            if (!m.equals(current)) {
                displayModes.add(m);
            }
        }

        for (int i = 0; i < displayModes.size(); i++) {
            String mode = displayModes.get(i);
            int scaleOffset = (GuiUtil.customGuiScale() == 1.0F) ? 10 : 9;
            int boxY = this.component.parent.getY() + 4 + this.offset + (i + 1) * scaleOffset;

            renderBox(this.component.parent.getX() - 1, boxY, mode,
                    (displayModes.size() == 1) ? MODE_BOTTOM : ((i == displayModes.size() - 1) ? MODE_BOTTOM : MODE_MIDDLE),
                    (displayModes.size() == 1) ? MODE_BOTTOM_OUTLINE : ((i == displayModes.size() - 1) ? MODE_BOTTOM_OUTLINE : MODE_MIDDLE_OUTLINE));

            if (isMouseOnExpanded(mouseX, mouseY, i)) {
                renderBox(this.component.parent.getX() - 1, boxY, mode,
                        (displayModes.size() == 1) ? MODE_BOTTOM_HOVER : ((i == displayModes.size() - 1) ? MODE_BOTTOM_HOVER : MODE_MIDDLE_HOVER),
                        (displayModes.size() == 1) ? MODE_BOTTOM_OUTLINE : ((i == displayModes.size() - 1) ? MODE_BOTTOM_OUTLINE : MODE_MIDDLE_OUTLINE));
            }
        }
    }

    private void renderBox(int x, int y, String text, ResourceLocation texture, ResourceLocation outline) {
        Minecraft mc = Minecraft.getMinecraft();
        float r = GuiUtil.getRed();
        float g = GuiUtil.getGreen();
        float b = GuiUtil.getBlue();

        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(outline);
        GlStateManager.color(r, g, b);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Meowtils.fontRenderer.drawString(text, (x + 5), (y + 8), -1, 5.0F);
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnCollapsed(mouseX, mouseY);
        this.y = this.component.parent.getY() + this.offset;
        this.x = this.component.parent.getX();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.component.open) return false;
        int boxX = this.component.parent.getX();
        int topY = this.component.parent.getY() + 4 + this.offset;
        int boxWidth = 80;
        int boxHeight = 11;
        boolean isMouseOn = (mouseX > boxX && mouseX < boxX + 80 && mouseY > topY && mouseY < topY + 11);
        boolean clickHandled = false;

        if (!this.expanded) {
            if (isMouseOn) {
                this.expanded = true;
                expandedPart = this;
                clickHandled = true;
            }
        } else {
            if (isMouseOn) {
                this.expanded = false;
                if (expandedPart == this) expandedPart = null;
                clickHandled = true;
            }

            List<String> modes = this.option.getModes();
            String current = this.option.getValue();
            List<String> displayModes = new ArrayList<>();
            for (String m : modes) {
                if (!m.equals(current)) {
                    displayModes.add(m);
                }
            }

            for (int i = 0; i < displayModes.size(); i++) {
                int boxY = topY + (i + 1) * 10;
                if (mouseX > boxX && mouseX < boxX + 80 && mouseY > boxY && mouseY < boxY + 11) {
                    this.option.setValue(displayModes.get(i));
                    this.expanded = false;
                    if (expandedPart == this) expandedPart = null;
                    clickHandled = true;
                    break;
                }
            }
        }
        return clickHandled;
    }

    private boolean isMouseOnCollapsed(int x, int y) {
        int boxX = this.component.parent.getX() - 1;
        int boxY = this.component.parent.getY() + 4 + this.offset;
        return (x > boxX && x < boxX + 80 && y > boxY && y < boxY + 14);
    }

    private boolean isMouseOnExpanded(int mouseX, int mouseY, int index) {
        int boxX = this.component.parent.getX();
        int topY = this.component.parent.getY() + 4 + this.offset;
        int boxWidth = 80;
        int boxHeight = 11;
        int boxY = topY + (index + 1) * 10;

        return (mouseX > boxX && mouseX < boxX + boxWidth && mouseY > boxY && mouseY < boxY + boxHeight);
    }
}