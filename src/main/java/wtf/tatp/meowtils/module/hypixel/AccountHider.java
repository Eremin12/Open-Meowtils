package wtf.tatp.meowtils.module.hypixel;

import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderStringEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class AccountHider extends Module {
    @Config
    public boolean enabled = false;
    @Config
    public boolean skin = false;
    @Config
    public int key = 0;
    @Config
    public String skinLocation = "";
    @Config
    public boolean hideName = false;
    @Config
    public String customName = "You";

    public AccountHider() {
        super("AccountHider", Module.Category.Hypixel);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Visually modify account information.\n§d/customname <name> §f- Set custom name");
        addExpand(new ExpandValue("Custom name", e -> {
            e.addToggle(new ToggleValue("Enabled", "hideName", this));
            e.addText(new TextValue("Name", "You", "customName", this));
        }));
        addExpand(new ExpandValue("Custom skin", e -> {
            e.addToggle(new ToggleValue("Enabled", "skin", this));
            e.addText(new TextValue("Skin", "File name", "skinLocation", this));
            e.addButton(new ButtonValue("Skin folder", 5.0F, () -> {}));
        }));
    }

    @EventTarget(priority = EventPriority.LOWEST)
    public void onRenderString(RenderStringEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP localPlayer = mc.thePlayer;
        if (localPlayer == null || mc.theWorld == null || event.getString() == null)
            return;
        String text = event.getString();
        String playerName = localPlayer.getName();
        String fakeName = this.customName.isEmpty() ? "You" : ColorUtil.convertFormatting(this.customName);

        if (this.hideName && text.contains(playerName))
            event.setString(text.replace(playerName, fakeName));
    }
}