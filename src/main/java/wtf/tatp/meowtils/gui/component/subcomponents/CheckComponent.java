package wtf.tatp.meowtils.gui.component.subcomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class CheckComponent extends Component {

    private static final ResourceLocation CHECK_ENABLED = TextureManager.get("textures/gui/check_enabled.png");
    private static final ResourceLocation CHECK_DISABLED = TextureManager.get("textures/gui/check_disabled.png");
    private static final ResourceLocation CHECK_ENABLED_HOVER = TextureManager.get("textures/gui/check_enabled_hover.png");
    private static final ResourceLocation CHECK_DISABLED_HOVER = TextureManager.get("textures/gui/check_disabled_hover.png");

    private boolean hovered;
    private final CheckValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public CheckComponent(CheckValue option, ModuleComponent moduleComponent, int offset) {
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
        int booleanX = this.component.parent.getX();
        int booleanY = this.component.parent.getY() + 4 + this.offset;
        float r = GuiUtil.getRed();
        float g = GuiUtil.getGreen();
        float b = GuiUtil.getBlue();

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        // 复选框背景
        if (this.hovered) {
            mc.getTextureManager().bindTexture(CHECK_DISABLED_HOVER);
        } else {
            mc.getTextureManager().bindTexture(CHECK_DISABLED);
        }
        Gui.drawScaledCustomSizeModalRect(booleanX, booleanY, 0.0F, 0.0F, 128, 128, 12, 12, 128.0F, 128.0F);

        if (this.option.getState()) {
            if (this.hovered) {
                mc.getTextureManager().bindTexture(CHECK_ENABLED_HOVER);
            } else {
                mc.getTextureManager().bindTexture(CHECK_ENABLED);
            }
            GlStateManager.color(r, g, b);
            Gui.drawScaledCustomSizeModalRect(booleanX, booleanY, 0.0F, 0.0F, 128, 128, 12, 12, 128.0F, 128.0F);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Meowtils.fontRenderer.drawStringWithShadow(this.option.getName(),
                (this.component.parent.getX() + 12),
                (this.component.parent.getY() + 12 + this.offset), -1, 5.0F);
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
            this.option.toggle();
        }
        return false;
    }

    public boolean isMouseOnButton(int x, int y) {
        return (x > this.x && x < this.x + 84 && y > this.y && y < this.y + 13);
    }
}