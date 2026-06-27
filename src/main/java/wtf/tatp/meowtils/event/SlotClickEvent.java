package wtf.tatp.meowtils.event;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import wtf.tatp.meowtils.event.api.Event;

public class SlotClickEvent extends Event {

    public static final int CLICK_NORMAL = 0;
    public static final int CLICK_SHIFT = 1;
    public static final int CLICK_HOTBAR = 2;
    public static final int CLICK_MIDDLE = 3;
    public static final int CLICK_DROP = 4;
    public static final int CLICK_DRAG = 5;
    public static final int CLICK_DOUBLE = 6;

    public static final int BUTTON_LEFT = 0;
    public static final int BUTTON_RIGHT = 1;
    public static final int BUTTON_MIDDLE = 2;

    private final GuiContainer guiContainer;
    private final Slot slot;
    private final int slotId;
    private int clickedButton;
    private int clickType;
    private boolean replaceClick;

    public SlotClickEvent(GuiContainer guiContainer, Slot slot, int slotId, int clickedButton, int clickType) {
        this.guiContainer = guiContainer;
        this.slot = slot;
        this.slotId = slotId;
        this.clickedButton = clickedButton;
        this.clickType = clickType;
    }

    public GuiContainer getGuiContainer() {
        return this.guiContainer;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public int getSlotId() {
        return this.slotId;
    }

    public int getClickedButton() {
        return this.clickedButton;
    }

    public int getClickType() {
        return this.clickType;
    }

    public boolean getReplaceClick() {
        return this.replaceClick;
    }

    public void setClickedButton(int clickedButton) {
        this.clickedButton = clickedButton;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    public void setReplaceClick() {
        this.replaceClick = true;
    }
}