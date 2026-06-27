package wtf.tatp.meowtils.gui.hudeditor;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.ModuleManager;

public class HudEditor extends GuiScreen {

    private static final int OUTLINE_COLOR = new Color(0, 0, 0, 90).getRGB();
    private static final int FILL_COLOR = new Color(80, 80, 80, 30).getRGB();
    private final ArrayList<HudElement> elements = new ArrayList<>();
    private HudElement dragging = null;
    private int dragOffsetX;
    private int dragOffsetY;

    public HudEditor() {
        for (Module m : ModuleManager.getModules()) {
            if (!m.getState()) continue;
            for (HudEntry entry : m.hudEditor()) {
                this.elements.add(new HudElement(entry));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        if (this.dragging != null) {
            this.dragging.entry.setX(mouseX - this.dragOffsetX);
            this.dragging.entry.setY(mouseY - this.dragOffsetY);
            this.dragging.sync();
        }

        for (HudElement e : this.elements) {
            if (e != this.dragging) e.sync();
        }

        HudElement hovered = null;
        for (HudElement e : this.elements) {
            if (e.isMouseOver(mouseX, mouseY)) {
                hovered = e;
            }
        }

        if (this.dragging != null) {
            this.dragging.drawBox();
        } else if (hovered != null) {
            hovered.drawBox();
        }

        HudElement showName = (this.dragging != null) ? this.dragging : hovered;
        if (showName != null) {
            int offset = 6;
            Meowtils.fontRenderer.drawScaledStringWithShadow(showName.entry.getName(), (mouseX + offset), (mouseY + offset), -1, 6.0F);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (HudElement e : this.elements) {
                if (e.isMouseOver(mouseX, mouseY)) {
                    this.dragging = e;
                    this.dragOffsetX = mouseX - e.x;
                    this.dragOffsetY = mouseY - e.y;
                    break;
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (state == 0 && this.dragging != null) {
            ConfigManager.save();
            this.dragging = null;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class HudElement {
        final HudEntry entry;
        int x;
        int y;
        int w;
        int h;

        HudElement(HudEntry entry) {
            this.entry = entry;
        }

        void sync() {
            this.x = this.entry.getX();
            this.y = this.entry.getY();

            int[] bounds = this.entry.getBounds();
            if (bounds != null) {
                this.w = bounds[0];
                this.h = bounds[1];
            }
        }

        void drawBox() {
            Gui.drawRect(this.x - 1, this.y - 1, this.x + this.w + 1, this.y + this.h + 1, OUTLINE_COLOR);
            Gui.drawRect(this.x, this.y, this.x + this.w, this.y + this.h, FILL_COLOR);
        }

        boolean isMouseOver(int mouseX, int mouseY) {
            return (mouseX >= this.x && mouseX <= this.x + this.w && mouseY >= this.y && mouseY <= this.y + this.h);
        }
    }
}