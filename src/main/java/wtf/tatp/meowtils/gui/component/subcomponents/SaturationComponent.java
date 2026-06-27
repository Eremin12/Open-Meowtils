package wtf.tatp.meowtils.gui.component.subcomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class SaturationComponent extends Component {

    private static final ResourceLocation BLANK_TRACK = TextureManager.get("textures/gui/colorpart/blank_track.png");
    private static final ResourceLocation BLANK_TRACK_HOVER = TextureManager.get("textures/gui/colorpart/blank_track_hover.png");
    private static final ResourceLocation SATURATION_FADE = TextureManager.get("textures/gui/colorpart/saturation_fade.png");
    private static final ResourceLocation SATURATION_FADE_HOVER = TextureManager.get("textures/gui/colorpart/saturation_fade_hover.png");
    private static final ResourceLocation TRACK_OVERLAY = TextureManager.get("textures/gui/colorpart/track_overlay.png");
    private static final ResourceLocation TRACK_OVERLAY_HOVER = TextureManager.get("textures/gui/colorpart/track_overlay_hover.png");
    private static final ResourceLocation COLOR_BUTTON = TextureManager.get("textures/gui/colorpart/color_button.png");

    private boolean hovered;
    private boolean dragging = false;
    private final SaturationValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public SaturationComponent(SaturationValue option, ModuleComponent moduleComponent, int offset) {
        this.option = option;
        this.component = moduleComponent;
        this.offset = offset;
        this.x = moduleComponent.parent.getX();
        this.y = moduleComponent.parent.getY() + offset;
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        int width = this.component.parent.getWidth() - 18;
        int baseX = this.component.parent.getX() - 1;
        int baseY = this.component.parent.getY() + 4 + this.offset;
        int sliderX = this.component.parent.getX() + 3;
        int sliderY = this.component.parent.getY() + this.offset + 6;
        int sliderWidth = width - 7;
        int sliderHeight = 6;
        float percent = (float) (this.option.get() / 100.0D);
        float circleX = sliderX + percent * sliderWidth;
        float circleY = sliderY + 3.0F;
        int buttonScale = 8;
        float renderX = circleX - 4.0F;
        float renderY = circleY - 4.0F;
        int rgb = this.option.getLink().getPureHueRGB();
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(this.hovered ? BLANK_TRACK_HOVER : BLANK_TRACK);
        GlStateManager.color(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(this.hovered ? SATURATION_FADE_HOVER : SATURATION_FADE);
        Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(this.hovered ? TRACK_OVERLAY_HOVER : TRACK_OVERLAY);
        Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(COLOR_BUTTON);
        Gui.drawScaledCustomSizeModalRect((int) renderX, (int) renderY + 3, 0.0F, 0.0F, 64, 64, 8, 8, 64.0F, 64.0F);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnSlider(mouseX, mouseY);
        this.y = this.component.parent.getY() + this.offset;
        this.x = this.component.parent.getX();

        int sliderX = this.component.parent.getX() + 3;
        int sliderWidth = this.component.parent.getWidth() - 25;

        if (this.dragging && Mouse.isButtonDown(0)) {
            float percent = (mouseX - sliderX) / (float) sliderWidth;
            percent = Math.max(0.0F, Math.min(1.0F, percent));
            this.option.set(percent * 100.0D);
        } else if (!Mouse.isButtonDown(0)) {
            this.dragging = false;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.component.open) return false;

        if (isMouseOnSlider(mouseX, mouseY)) {
            int sliderX = this.component.parent.getX() + 3;
            int sliderWidth = this.component.parent.getWidth() - 25;
            float percent = (mouseX - sliderX) / (float) sliderWidth;
            percent = Math.max(0.0F, Math.min(1.0F, percent));
            this.option.set(percent * 100.0D);

            this.dragging = true;
            return true;
        }
        return false;
    }

    private boolean isMouseOnSlider(int mouseX, int mouseY) {
        int sliderX = this.component.parent.getX() + 3;
        int sliderY = this.component.parent.getY() + this.offset + 4;
        int sliderWidth = this.component.parent.getWidth() - 25;
        int sliderHeight = 11;

        return (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= sliderY && mouseY <= sliderY + sliderHeight);
    }
}