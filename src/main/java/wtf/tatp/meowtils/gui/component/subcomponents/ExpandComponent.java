package wtf.tatp.meowtils.gui.component.subcomponents;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class ExpandComponent extends Component {

    private static final ResourceLocation EXPANDED = TextureManager.get("textures/gui/expandpart/expand_down.png");
    private static final ResourceLocation NOT_EXPANDED = TextureManager.get("textures/gui/expandpart/expand_side.png");
    private static final ResourceLocation EXPANDED_HOVER = TextureManager.get("textures/gui/expandpart/expand_down_hover.png");
    private static final ResourceLocation NOT_EXPANDED_HOVER = TextureManager.get("textures/gui/expandpart/expand_side_hover.png");

    private final List<Component> sub = new ArrayList<>();
    private boolean hovered;
    public final ExpandValue option;
    private final ModuleComponent component;
    private int offset;
    private int x;
    private int y;

    public ExpandComponent(ExpandValue option, ModuleComponent moduleComponent, int offset) {
        this.option = option;
        this.component = moduleComponent;
        this.x = moduleComponent.parent.getX() + moduleComponent.parent.getWidth();
        this.y = moduleComponent.parent.getY() + moduleComponent.offset;
        this.offset = offset;
        buildSub(offset + 12);
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = this.component.parent.getX() - 1;
        int y = this.component.parent.getY() + 4 + this.offset;
        int arrowX = this.component.parent.getX() + this.component.parent.getWidth() - 26;
        int arrowY = this.component.parent.getY() + 8 + this.offset;

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        if (!this.option.getState()) {
            if (this.hovered) {
                mc.getTextureManager().bindTexture(NOT_EXPANDED_HOVER);
            } else {
                mc.getTextureManager().bindTexture(NOT_EXPANDED);
            }
        } else if (this.hovered) {
            mc.getTextureManager().bindTexture(EXPANDED_HOVER);
        } else {
            mc.getTextureManager().bindTexture(EXPANDED);
        }

        Gui.drawModalRectWithCustomSizedTexture(arrowX, arrowY, 0.0F, 0.0F, 5, 5, 5.0F, 5.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Meowtils.fontRenderer.drawStringWithShadow(this.option.getName(),
                (this.component.parent.getX() + 2),
                (this.component.parent.getY() + 12 + this.offset), -1, 5.0F);

        if (this.option.getState()) {
            for (Component c : this.sub) {
                c.render();
            }
        }
    }

    public int getHeight() {
        if (this.option.getState() && !this.sub.isEmpty()) {
            return 12 + this.sub.size() * 12;
        }
        return 12;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;

        int subY = newOff + 12;
        for (Component c : this.sub) {
            c.setOff(subY);
            subY += 12;
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = this.component.parent.getY() + this.offset;
        this.x = this.component.parent.getX();

        if (this.option.getState()) {
            for (Component c : this.sub) {
                c.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX + 2, mouseY - 3) && button == 0 && this.component.open) {
            this.option.toggle();
            this.component.parent.refresh();
        }

        if (this.option.getState()) {
            for (Component c : this.sub) {
                c.mouseClicked(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.option.getState()) {
            for (Component c : this.sub) {
                c.keyTyped(typedChar, key);
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return (x > this.x && x < this.x + 84 && y > this.y && y < this.y + 13);
    }

    public boolean isLastSub(Component expand) {
        return (this.sub.indexOf(expand) == this.sub.size() - 1);
    }

    public List<Component> getSub() {
        return this.sub;
    }

    private void buildSub(int y) {
        for (Object v : this.option.getSubValues()) {
            Component c = null;

            if (v instanceof ColorValue) {
                c = ((ColorValue) v).createComponent(this.component, y);
            } else if (v instanceof SaturationValue) {
                c = ((SaturationValue) v).createComponent(this.component, y);
            } else if (v instanceof BrightnessValue) {
                c = ((BrightnessValue) v).createComponent(this.component, y);
            } else if (v instanceof OpacityValue) {
                c = ((OpacityValue) v).createComponent(this.component, y);
            } else if (v instanceof ToggleValue) {
                c = ((ToggleValue) v).createComponent(this.component, y);
            } else if (v instanceof SliderValue) {
                c = ((SliderValue) v).createComponent(this.component, y);
            } else if (v instanceof ModeValue) {
                c = ((ModeValue) v).createComponent(this.component, y);
            } else if (v instanceof TextValue) {
                c = ((TextValue) v).createComponent(this.component, y);
            } else if (v instanceof CheckValue) {
                c = ((CheckValue) v).createComponent(this.component, y);
            } else if (v instanceof ButtonValue) {
                c = ((ButtonValue) v).createComponent(this.component, y);
            } else if (v instanceof BindValue) {
                c = ((BindValue) v).createComponent(this.component, y);
            }

            if (c != null) {
                c.nested = true;
                c.expandParent = this;
                this.sub.add(c);
                y += 12;
            }
        }
    }
}