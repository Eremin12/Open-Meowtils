package wtf.tatp.meowtils.gui.component.subcomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.module.meowtils.GUI;

public class BindComponent extends Component {

    private boolean hovered;
    private boolean binding;
    private final BindValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public BindComponent(BindValue option, ModuleComponent moduleComponent, int offset) {
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

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        String text;
        if (this.binding) {
            text = "...";
        } else {
            int key = this.option.getBind();
            text = (key == 0) ? "None" : Keyboard.getKeyName(key);
        }

        Meowtils.fontRenderer.drawStringWithShadow(this.option.getName() + ": " + text,
                (this.component.parent.getX() + 2),
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
        if (this.binding && !isMouseOnButton(mouseX + 2, mouseY - 3)) {
            this.binding = false;
            return false;
        }

        if (isMouseOnButton(mouseX + 2, mouseY - 3) && button == 0 && this.component.open) {
            this.binding = true;
            return true;
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.binding) {
            if (key == Keyboard.KEY_BACK || key == Module.get(GUI.class).key || key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_NONE) {
                this.option.setBind(0);
            } else {
                this.option.setBind(key);
            }

            this.binding = false;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return (x > this.x && x < this.x + 84 && y > this.y && y < this.y + 13);
    }
}