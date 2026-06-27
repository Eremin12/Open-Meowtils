package wtf.tatp.meowtils.gui.component.subcomponents;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.manager.TextureManager;
import wtf.tatp.meowtils.util.Render;

public class SliderComponent extends Component {

    private static final ResourceLocation SLIDER_TRACK = TextureManager.get("textures/gui/sliderpart/slider_track.png");
    private static final ResourceLocation SLIDER_END = TextureManager.get("textures/gui/sliderpart/slider_end.png");
    private static final ResourceLocation SLIDER_BUTTON = TextureManager.get("textures/gui/sliderpart/slider_button.png");
    private static final ResourceLocation SLIDER_BUTTON_LARGE = TextureManager.get("textures/gui/sliderpart/slider_button_large.png");

    private boolean hovered;
    private boolean dragging = false;
    private final SliderValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public SliderComponent(SliderValue option, ModuleComponent moduleComponent, int offset) {
        this.option = option;
        this.component = moduleComponent;
        this.x = moduleComponent.parent.getX() + moduleComponent.parent.getWidth();
        this.y = moduleComponent.parent.getY() + moduleComponent.offset;
        this.offset = offset;
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        int newWidth = this.component.parent.getWidth() - 18;
        int drag = -2 + (int) ((this.option.get() - this.option.getMin()) / (this.option.getMax() - this.option.getMin()) * (newWidth - 8));
        float sliderHeight = 2.5F;
        int x = this.component.parent.getX() - 1;
        int y = this.component.parent.getY() + 4 + this.offset;
        float circleX = (this.component.parent.getX() + 5 + drag);
        float circleY = (this.component.parent.getY() + this.offset + 11) + 1.25F;
        float r = GuiUtil.getRed();
        float g = GuiUtil.getGreen();
        float b = GuiUtil.getBlue();
        int buttonScale = 5;
        float buttonX = circleX - 2.5F;
        float buttonY = circleY - 2.5F;
        int textLeftX = this.component.parent.getX() + 2;
        int textBaseY = this.component.parent.getY() + this.offset + 9;
        int textRightEdge = this.component.parent.getX() + this.component.parent.getWidth() - 22;
        String nameText = this.option.getName();
        String formatted = this.option.getFormattedValue();
        String valueSuffix = (this.option.getValueType() != null) ? (" " + this.option.getValueType()) : "";
        String valueText = formatted + valueSuffix;
        int valueWidth = (int) Meowtils.fontRenderer.getStringWidth(valueText, 5.0F);
        int valueX = textRightEdge - valueWidth;

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(SLIDER_TRACK);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(SLIDER_END);
        GlStateManager.color(r, g, b);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Render.drawRectFloat((this.component.parent.getX() + 4),
                (this.component.parent.getY() + this.offset) + 11.25F,
                (this.component.parent.getX() + 5 + drag),
                (this.component.parent.getY() + this.offset) + 11.5F + 2.5F,
                new Color(r, g, b).getRGB());

        if (this.hovered) {
            mc.getTextureManager().bindTexture(SLIDER_BUTTON_LARGE);
        } else {
            mc.getTextureManager().bindTexture(SLIDER_BUTTON);
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Gui.drawScaledCustomSizeModalRect((int) buttonX + 1, (int) buttonY + 1, 0.0F, 0.0F, 64, 64, 5, 5, 64.0F, 64.0F);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();

        Meowtils.fontRenderer.drawStringWithShadow(nameText, textLeftX, textBaseY, -1, 5.0F);
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

        int sliderX = this.component.parent.getX() + 4;
        int sliderWidth = this.component.parent.getWidth() - 26;

        if (this.dragging && Mouse.isButtonDown(0)) {
            double percent = (mouseX - sliderX) / (double) sliderWidth;
            percent = Math.max(0.0D, Math.min(percent, 1.0D));
            double range = this.option.getMax() - this.option.getMin();
            this.option.set(this.option.getMin() + percent * range);
        } else if (!Mouse.isButtonDown(0)) {
            this.dragging = false;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.component.open) return false;

        if (isMouseOnSlider(mouseX, mouseY)) {
            int sliderX = this.component.parent.getX() + 4;
            int sliderWidth = this.component.parent.getWidth() - 26;
            double percent = (mouseX - sliderX) / (double) sliderWidth;
            percent = Math.max(0.0D, Math.min(percent, 1.0D));
            double range = this.option.getMax() - this.option.getMin();
            this.option.set(this.option.getMin() + percent * range);

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