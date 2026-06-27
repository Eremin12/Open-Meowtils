package wtf.tatp.meowtils.module.utility;

import java.util.Arrays;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.handler.LatencyHandler;
import wtf.tatp.meowtils.manager.NotificationManager;

public class LatencyAlerts extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public int threshold = 500;
    @Config
    public String alertType = "Chat";
    @Config
    public boolean ignoreLimbo = true;

    private static long lastAlert;

    public LatencyAlerts() {
        super("LatencyAlerts", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Warns you in chat when you lose connection to the server. \n May not work on all servers.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addSlider(new SliderValue("Latency threshold", 0.0D, 3000.0D, 50.0D, "ms", "threshold", this, int.class));
        addToggle(new ToggleValue("Ignore limbo", "ignoreLimbo", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.isSingleplayer() || this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebar == null && this.ignoreLimbo) return;

        long now = System.currentTimeMillis();
        long last = now - LatencyHandler.getLastPacket();

        if (last >= this.threshold && now - lastAlert >= 3000L) {
            if (!this.alertType.equals("Notification")) {
                Meowtils.addMessage(EnumChatFormatting.DARK_GRAY + "Packet loss detected: " + EnumChatFormatting.RED + last + "ms");
            }

            if (!this.alertType.equals("Chat")) {
                NotificationManager.show("LatencyAlerts", EnumChatFormatting.RED + String.valueOf(last) + "ms", NotificationManager.Type.ALERT, 2000L);
            }

            lastAlert = now;
        }
    }
}