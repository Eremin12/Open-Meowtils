package wtf.tatp.meowtils.gui.component.subcomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class OpacityComponent extends Component {

    private static final ResourceLocation OPACITY_TRACK = TextureManager.get("textures/gui/colorpart/opacity_track.png");
    private static final ResourceLocation OPACITY_TRACK_HOVER = TextureManager.get("textures/gui/colorpart/opacity_track_hover.png");
    private static final ResourceLocation COLOR_BUTTON = TextureManager.get("textures/gui/colorpart/color_button.png");

    private boolean hovered;
    private boolean dragging = false;
    private final OpacityValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public OpacityComponent(OpacityValue option, ModuleComponent moduleComponent, int offset) {
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
        int textLeftX = this.component.parent.getX() + 2;
        int textBaseY = this.component.parent.getY() + this.offset + 9;
        int textRightEdge = this.component.parent.getX() + this.component.parent.getWidth() - 22;
        String valueSuffix = (this.option.getValueType() != null) ? (" " + this.option.getValueType()) : "";
        String valueText = this.option.getFormattedValue() + valueSuffix;
        int valueWidth = (int) Meowtils.fontRenderer.getStringWidth(valueText, 5.0F);
        int valueX = textRightEdge - valueWidth;

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(this.hovered ? OPACITY_TRACK_HOVER : OPACITY_TRACK);
        Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(COLOR_BUTTON);
        Gui.drawScaledCustomSizeModalRect((int) renderX, (int) renderY + 3, 0.0F, 0.0F, 64, 64, 8, 8, 64.0F, 64.0F);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Meowtils.fontRenderer.drawStringWithShadow(this.option.getName(), textLeftX, textBaseY, -1, 5.0F);
        Meowtils.fontRenderer.drawStringWithShadow(valueText, valueX, textBaseY, -1, 5.0F);
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
            int sliderWidth = this.component.parent.getWidth() - 18 - 7;
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
        int sliderWidth = this.component.parent.getWidth() - 18 - 7;
        int sliderHeight = 11;

        return (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= sliderY && mouseY <= sliderY + sliderHeight);
    }
}