package wtf.tatp.meowtils.event;

import net.minecraft.client.gui.GuiScreen;
import wtf.tatp.meowtils.event.api.Event;

public class GuiOpenEvent extends Event {

    private final GuiScreen gui;

    public GuiOpenEvent(GuiScreen gui) {
        this.gui = gui;
    }

    public GuiScreen getGui() {
        return this.gui;
    }
}