package wtf.tatp.meowtils.gui.component.subcomponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.component.Component;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.manager.TextureManager;

public class TextComponent extends Component {

    private static final ResourceLocation TEXT_PART = TextureManager.get("textures/gui/text_part.png");
    private static final ResourceLocation TEXT_PART_OVERLAY = TextureManager.get("textures/gui/text_part_overlay.png");
    private static final ResourceLocation TEXT_PART_HOVER = TextureManager.get("textures/gui/text_part_hover.png");
    private static final int MAX_LENGTH = 256;

    public final TextValue option;
    public final ModuleComponent component;
    public int offset;
    private final String description;
    private final String prefix;
    private int x;
    private int y;
    private boolean hovered;
    private boolean focused;
    private static TextComponent focusedPart = null;

    public static boolean isTyping() {
        return (focusedPart != null && focusedPart.focused);
    }

    public TextComponent(TextValue option, ModuleComponent moduleComponent, int offset, String prefix) {
        this.option = option;
        this.component = moduleComponent;
        this.offset = offset;
        this.description = option.getDescription();
        this.prefix = (prefix == null) ? "" : prefix;
        this.x = this.component.parent.getX();
        this.y = this.component.parent.getY() + offset;
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = this.component.parent.getX() - 1;
        int y = this.component.parent.getY() + 4 + this.offset;
        float r = GuiUtil.getRed();
        float g = GuiUtil.getGreen();
        float b = GuiUtil.getBlue();
        String raw = this.option.get();
        boolean showPlaceholder = ((raw == null || raw.isEmpty()) && !this.focused);
        String shown = showPlaceholder ? this.description : ((raw == null) ? "" : raw);
        int valueColor = showPlaceholder ? -7829368 : -1;
        int textX = x + 5;
        int textY = y + 8;

        mc.getTextureManager().bindTexture(GuiUtil.getComponentBackground(this.component, this));
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(TEXT_PART);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        mc.getTextureManager().bindTexture(TEXT_PART_OVERLAY);
        GlStateManager.color(r, g, b);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.hovered) {
            mc.getTextureManager().bindTexture(TEXT_PART_HOVER);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 90, 20, 90.0F, 20.0F);
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        int prefixWidth = 0;
        if (this.prefix != null && !this.prefix.isEmpty()) {
            Meowtils.fontRenderer.drawStringWithShadow(this.prefix + ":", textX, textY, -1, 5.0F);
            prefixWidth = (int) Meowtils.fontRenderer.getStringWidth(this.prefix + ":", 5.0F);
        }

        int inputTextX = textX + prefixWidth;
        if (prefixWidth > 0) inputTextX += 2;
        int boxRightX = x + 80;
        int maxTextWidth = Math.max(0, boxRightX - inputTextX - 4);

        String displayText = shown;
        while (Meowtils.fontRenderer.getStringWidth(displayText, 5.0F) > maxTextWidth && !displayText.isEmpty()) {
            displayText = displayText.substring(1);
        }

        Meowtils.fontRenderer.drawStringWithShadow(displayText, inputTextX, textY, valueColor, 5.0F);

        if (this.focused) {
            boolean blinkOn = (System.currentTimeMillis() / 500L % 2L == 0L);

            if (blinkOn) {
                int shownWidth = (int) Meowtils.fontRenderer.getStringWidth(displayText, 5.0F);
                int caretX = inputTextX + shownWidth + 1;

                Meowtils.fontRenderer.drawString("I", caretX, textY, -3355444, 5.0F);
            }
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
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
        if (!this.component.open) return false;

        if (button == 0 && this.hovered) {
            if (focusedPart != null && focusedPart != this) focusedPart.focused = false;
            this.focused = true;
            focusedPart = this;
            return false;
        }

        if (button == 0 && this.focused) {
            this.focused = false;
            if (focusedPart == this) focusedPart = null;
        }
        if (button == 1 && this.focused) {
            this.focused = false;
            if (focusedPart == this) focusedPart = null;
        }
        if (button == 2 && this.focused) {
            this.focused = false;
            if (focusedPart == this) focusedPart = null;
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (!this.component.open) return;
        if (!this.focused) return;

        if (GuiScreen.isKeyComboCtrlV(key)) {
            String clipboard = GuiScreen.getClipboardString();
            if (clipboard == null || clipboard.isEmpty()) {
                return;
            }

            String current = this.option.get();
            if (current == null) current = "";

            StringBuilder sb = new StringBuilder(current);
            for (int i = 0; i < clipboard.length() && sb.length() < MAX_LENGTH; i++) {
                char c = clipboard.charAt(i);
                if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                    sb.append(c);
                }
            }
            this.option.set(sb.toString());
            return;
        }

        if (key == org.lwjgl.input.Keyboard.KEY_ESCAPE) {
            this.focused = false;
            if (focusedPart == this) focusedPart = null;
            return;
        }

        if (key == org.lwjgl.input.Keyboard.KEY_RETURN) {
            this.focused = false;
            if (focusedPart == this) focusedPart = null;
            return;
        }

        if (key == org.lwjgl.input.Keyboard.KEY_BACK) {
            String cur = this.option.get();
            if (cur == null) cur = "";
            if (!cur.isEmpty()) {
                this.option.set(cur.substring(0, cur.length() - 1));
            }
            return;
        }

        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            String cur = this.option.get();
            if (cur == null) cur = "";
            if (cur.length() < MAX_LENGTH) {
                this.option.set(cur + typedChar);
            }
        }
    }

    public boolean isMouseOnButton(int mouseX, int mouseY) {
        int boxX = this.component.parent.getX() - 1;
        int boxY = this.component.parent.getY() + 4 + this.offset;
        return (mouseX > boxX && mouseX < boxX + 80 && mouseY > boxY && mouseY < boxY + 14);
    }
}