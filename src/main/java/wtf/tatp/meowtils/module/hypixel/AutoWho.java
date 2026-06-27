package wtf.tatp.meowtils.module.hypixel;

import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Server;

public class AutoWho extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean hide = false;

    private static int ticksRemaining = -1;

    public AutoWho() {
        super("AutoWho", Module.Category.Hypixel);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Automatically runs /who after a game started. May be required by some modules.");
        addToggle(new ToggleValue("Hide message", "hide", this));
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String message = event.getComponent().getUnformattedText();
        if (Server.HYPIXEL.isNotActive()) return;

        if (message.equals("The game starts in 1 second!")) {
            ticksRemaining = 60;
        }
    }

    @EventTarget(priority = EventPriority.LOWEST)
    public void hideAutoWhoMessage(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String message = event.getComponent().getUnformattedText();

        if ((message.contains("ONLINE:") || message.startsWith("Team #") || message.startsWith("Mode:")) && this.hide) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        if (ticksRemaining > 0) {
            ticksRemaining--;

            if (ticksRemaining == 0) {
                Meowtils.sendCleanMessage("/who");
                ticksRemaining = -1;
            }
        }
    }
}