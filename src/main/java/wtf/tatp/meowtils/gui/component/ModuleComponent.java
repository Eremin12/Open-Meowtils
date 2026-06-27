package wtf.tatp.meowtils.gui.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
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
import wtf.tatp.meowtils.module.meowtils.GUI;
import wtf.tatp.meowtils.util.ColorUtil;

public class ModuleComponent extends Component {

    private static final ResourceLocation MODULE_DISABLED = TextureManager.get("textures/gui/module_disabled.png");
    private static final ResourceLocation MODULE_DISABLED_BOTTOM = TextureManager.get("textures/gui/module_disabled_bottom.png");
    private static final ResourceLocation MODULE_CONNECTED_BOTH = TextureManager.get("textures/gui/module_connected_both.png");
    private static final ResourceLocation MODULE_CONNECTED_TOP = TextureManager.get("textures/gui/module_connected_top.png");
    private static final ResourceLocation MODULE_CONNECTED_BOTTOM = TextureManager.get("textures/gui/module_connected_bottom.png");
    private static final ResourceLocation MODULE_NOT_CONNECTED = TextureManager.get("textures/gui/module_not_connected.png");
    private static final ResourceLocation CUSTOM_SCALE_LINE = TextureManager.get("textures/gui/custom_scale_line.png");
    private static final ResourceLocation MODULE_ARROW_UP = TextureManager.get("textures/gui/module_arrow_up.png");
    private static final ResourceLocation MODULE_ARROW_DOWN = TextureManager.get("textures/gui/module_arrow_down.png");
    private static final ResourceLocation MODULE_ARROW_UP_HOVER = TextureManager.get("textures/gui/module_arrow_up_hover.png");
    private static final ResourceLocation MODULE_ARROW_DOWN_HOVER = TextureManager.get("textures/gui/module_arrow_down_hover.png");
    private static final ResourceLocation DOTS = TextureManager.get("textures/gui/dots.png");
    private static final ResourceLocation DOTS_HOVER = TextureManager.get("textures/gui/dots_hover.png");
    private static final int brightTextColor = new Color(255, 255, 255).getRGB();
    private static final int darkTextColor = new Color(40, 40, 40).getRGB();

    public Module mod;
    public Frame parent;
    public int offset;
    private boolean binding;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    public boolean open;

    public List<Component> getSubcomponents() {
        return this.subcomponents;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public ModuleComponent(Module mod, Frame parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<>();
        this.open = false;
        int optionY = offset + 14;

        for (Object value : mod.getOrderedValues()) {
            if (value instanceof ColorValue) {
                ColorValue color = (ColorValue) value;
                this.subcomponents.add(color.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof SaturationValue) {
                SaturationValue saturation = (SaturationValue) value;
                this.subcomponents.add(saturation.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof BrightnessValue) {
                BrightnessValue brightness = (BrightnessValue) value;
                this.subcomponents.add(brightness.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof OpacityValue) {
                OpacityValue opacity = (OpacityValue) value;
                this.subcomponents.add(opacity.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof ToggleValue) {
                ToggleValue toggle = (ToggleValue) value;
                this.subcomponents.add(toggle.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof SliderValue) {
                SliderValue slider = (SliderValue) value;
                this.subcomponents.add(slider.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof ModeValue) {
                ModeValue mode = (ModeValue) value;
                this.subcomponents.add(mode.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof TextValue) {
                TextValue text = (TextValue) value;
                this.subcomponents.add(text.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof CheckValue) {
                CheckValue check = (CheckValue) value;
                this.subcomponents.add(check.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof ButtonValue) {
                ButtonValue button = (ButtonValue) value;
                this.subcomponents.add(button.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof BindValue) {
                BindValue bind = (BindValue) value;
                this.subcomponents.add(bind.createComponent(this, optionY));
                optionY += 12;
            } else if (value instanceof ExpandValue) {
                ExpandValue expand = (ExpandValue) value;
                this.subcomponents.add(expand.createComponent(this, optionY));
                optionY += 12;
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
        int optionY = this.offset + 12;
        for (Component c : this.subcomponents) {
            c.setOff(optionY);
            optionY += c.getHeight();
        }
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        GUI gui = Module.get(GUI.class);
        if (gui == null) return;

        boolean moduleAbove = isModuleAbove();
        boolean moduleBelow = isModuleBelow();
        float r = gui.red / 255.0F;
        float g = gui.green / 255.0F;
        float b = gui.blue / 255.0F;
        int textScale = 6;
        float textOffsetX = (this.parent.getWidth() - 20) / 2.0F - 37.0F;
        int textOffsetY = this.offset + 10;

        mc.getTextureManager().bindTexture((moduleAbove && moduleBelow) ? MODULE_DISABLED :
                ((moduleAbove && !this.open) ? MODULE_DISABLED_BOTTOM :
                 ((!moduleAbove && !moduleBelow && !this.open) ? MODULE_DISABLED_BOTTOM : MODULE_DISABLED)));

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(this.parent.getX() - 1, this.parent.getY() + 1 + this.offset, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        if (this.mod.getState()) {
            boolean enabledAbove = isAboveEnabled();
            boolean enabledBelow = isBelowEnabled();

            mc.getTextureManager().bindTexture((enabledAbove && enabledBelow) ? MODULE_CONNECTED_BOTH :
                    (enabledAbove ? MODULE_CONNECTED_TOP :
                     (enabledBelow ? MODULE_CONNECTED_BOTTOM : MODULE_NOT_CONNECTED)));

            GlStateManager.color(r, g, b, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(this.parent.getX() - 1, this.parent.getY() + 1 + this.offset, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();

            if (GuiUtil.customGuiScale() < 1.0F && enabledBelow) {
                mc.getTextureManager().bindTexture(CUSTOM_SCALE_LINE);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                Gui.drawModalRectWithCustomSizedTexture(this.parent.getX() - 1, this.parent.getY() + 1 + this.offset, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GlStateManager.disableBlend();
                GlStateManager.disableAlpha();
            }
        }

        if (ColorUtil.isColorTooBright(gui.red, gui.green, gui.blue) && this.mod.getState()) {
            Meowtils.fontRenderer.drawStringWithLightShadow(this.binding ? "" : this.mod.getName(),
                    this.parent.getX() + textOffsetX, (this.parent.getY() + textOffsetY), darkTextColor, 6.0F);

            Meowtils.fontRenderer.drawStringWithLightShadow(this.binding ? ("Bind.. " + Keyboard.getKeyName(this.mod.getKey())) : "",
                    this.parent.getX() + textOffsetX, (this.parent.getY() + textOffsetY), darkTextColor, 6.0F);
        } else {
            Meowtils.fontRenderer.drawStringWithShadow(this.binding ? "" : this.mod.getName(),
                    this.parent.getX() + textOffsetX, (this.parent.getY() + textOffsetY), brightTextColor, 6.0F);

            Meowtils.fontRenderer.drawStringWithShadow(this.binding ? ("Bind.. " + Keyboard.getKeyName(this.mod.getKey())) : "",
                    this.parent.getX() + textOffsetX, (this.parent.getY() + textOffsetY), brightTextColor, 6.0F);
        }

        float color = this.mod.getState() ? 1.0F : 0.5882353F;
        int arrowX = this.parent.getX() + 70;
        int arrowY = this.parent.getY() + this.offset + 6;

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(color, color, color, 1.0F);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        if (!this.subcomponents.isEmpty()) {
            mc.getTextureManager().bindTexture((this.open && this.isHovered) ? MODULE_ARROW_DOWN_HOVER :
                    (this.open ? MODULE_ARROW_DOWN : (this.isHovered ? MODULE_ARROW_UP_HOVER : MODULE_ARROW_UP)));
            Gui.drawModalRectWithCustomSizedTexture(arrowX, arrowY, 0.0F, 0.0F, 5, 5, 5.0F, 5.0F);
        } else {
            mc.getTextureManager().bindTexture(this.isHovered ? DOTS_HOVER : DOTS);
            Gui.drawModalRectWithCustomSizedTexture(arrowX, arrowY, 0.0F, 0.0F, 5, 5, 5.0F, 5.0F);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        if (this.open && !this.subcomponents.isEmpty()) {
            for (Component component : this.subcomponents) {
                component.render();
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            if (this.subcomponents.isEmpty()) return 28;
            int total = 15;
            for (Component c : this.subcomponents) {
                total += c.getHeight();
            }
            return total;
        }
        return 15;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.parent.refresh();
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component component : this.subcomponents) {
                component.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 2 && this.parent.open && !this.mod.alwaysEnabled) {
            this.binding = !this.binding;
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.setState(!this.mod.getState());
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1 && !this.subcomponents.isEmpty()) {
            this.open = !this.open;
            this.parent.refresh();
        }

        for (Component component : this.subcomponents) {
            component.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    public boolean isMouseOnButton(int mouseX, int mouseY) {
        return (mouseX > this.parent.getX() && mouseX < this.parent.getX() + this.parent.getWidth() - 18 &&
                mouseY > this.parent.getY() + this.offset && mouseY < this.parent.getY() + 16 + this.offset);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.binding) {
            if (key == Keyboard.KEY_BACK) {
                this.mod.setKey(0);
                this.binding = false;
                return;
            }
            this.mod.setKey(key);
            this.binding = false;
            if (key == Keyboard.KEY_LSHIFT) {
                this.mod.setKey(0);
                this.binding = false;
            }
            return;
        }
        if (this.open && !this.subcomponents.isEmpty()) {
            for (Component component : this.subcomponents) {
                component.keyTyped(typedChar, key);
            }
        }
    }

    private ModuleComponent getConnected(int direction) {
        int index = this.parent.getComponents().indexOf(this);
        int connectedIndex = index + direction;

        if (connectedIndex >= 0 && connectedIndex < this.parent.getComponents().size()) {
            Component neighbor = this.parent.getComponents().get(connectedIndex);
            if (neighbor instanceof ModuleComponent) {
                return (ModuleComponent) neighbor;
            }
        }
        return null;
    }

    private boolean isAboveEnabled() {
        ModuleComponent above = getConnected(-1);
        return (above != null && above.mod.getState());
    }

    private boolean isBelowEnabled() {
        ModuleComponent below = getConnected(1);
        return (below != null && below.mod.getState());
    }

    public boolean isModuleAbove() {
        ModuleComponent above = getConnected(-1);
        return (above != null);
    }

    public boolean isModuleBelow() {
        ModuleComponent below = getConnected(1);
        return (below != null);
    }

    private Component getConnectedComponent(Component current, int direction) {
        int index = this.subcomponents.indexOf(current);
        int neighborIndex = index + direction;

        if (neighborIndex >= 0 && neighborIndex < this.subcomponents.size()) {
            return this.subcomponents.get(neighborIndex);
        }
        return null;
    }

    public boolean isComponentAbove(Component comp) {
        return (getConnectedComponent(comp, -1) != null);
    }

    public boolean isComponentBelow(Component comp) {
        return (getConnectedComponent(comp, 1) != null);
    }
}