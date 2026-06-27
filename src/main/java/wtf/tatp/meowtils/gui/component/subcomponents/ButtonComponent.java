package wtf.tatp.meowtils.gui.component.subcomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class ButtonComponent extends Component {

    private static final ResourceLocation BUTTON_PART = TextureManager.get("textures/gui/button_part.png");
    private static final ResourceLocation BUTTON_PART_HOVER = TextureManager.get("textures/gui/button_part_hover.png");
    private static final ResourceLocation BUTTON_PART_BORDER = TextureManager.get("textures/gui/button_part_border.png");

    private boolean hovered;
    private final ButtonValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public ButtonComponent(ButtonValue option, ModuleComponent moduleComponent, int offset) {
        this.option = option;
        this.component = moduleComponent;
        this.x = moduleComponent.parent.getX() + moduleComponent.parent.getWidth();
        this.y = moduleComponent.parent.getY() + moduleComponent.offset;
        this.offset = offset;
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = this.component.parent.getX() - 1;
        int y = this.component.parent.getY() + 4 + this.offset;
        float r = GuiUtil.getRed();
        float g = GuiUtil.getGreen();
        float b = GuiUtil.getBlue();
        float textScale = this.option.getScale();

        // 背景
        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(BUTTON_PART);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(BUTTON_PART_BORDER);
        GlStateManager.color(r, g, b);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.hovered) {
            mc.getTextureManager().bindTexture(BUTTON_PART_HOVER);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Meowtils.fontRenderer.drawStringWithShadow(this.option.getName(),
                (this.component.parent.getX() + 40) - Meowtils.fontRenderer.getStringWidth(this.option.getName(), textScale) / 2.0F,
                (this.component.parent.getY() + 12 + this.offset), -1, textScale);
    }

    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = this.component.parent.getY() + this.offset;
        this.x = this.component.parent.getX();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX + 2, mouseY - 3) && button == 0 && this.component.open) {
            this.option.click();
            return true;
        }
        return false;
    }

    public boolean isMouseOnButton(int x, int y) {
        return (x > this.x && x < this.x + 84 && y > this.y && y < this.y + 13);
    }
}