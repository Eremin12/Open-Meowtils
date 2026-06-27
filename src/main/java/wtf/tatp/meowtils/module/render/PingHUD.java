package wtf.tatp.meowtils.module.render;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.handler.LatencyHandler;

public class PingHUD extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int posX = 0;
    @Config
    public int posY = 0;
    @Config
    public float scale = 0.65F;
    @Config
    public boolean text = true;
    @Config
    public boolean brackets = false;
    @Config
    public boolean dynamicColor = true;

    private static long lastPingTime = 0L;
    private static final long PING_FREQUENCY = 60000L;
    private static int ping;

    public PingHUD() {
        super("PingHUD", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays your ping.");
        addToggle(new ToggleValue("Dynamic color", "dynamicColor", this));
        addToggle(new ToggleValue("Text", "text", this));
        addToggle(new ToggleValue("Brackets", "brackets", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        long now = System.currentTimeMillis();

        if (now - lastPingTime >= PING_FREQUENCY) {
            lastPingTime = now;

            ServerData serverData = this.mc.getCurrentServerData();

            if (serverData != null) {
                LatencyHandler.ping(serverData, p -> ping = p);

                lastPingTime = now;
                Meowtils.info("Pinged server: " + serverData.serverIP);
            }
        }
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (!GuiUtil.inEditor() && this.mc.currentScreen != null) return;

        ping = this.mc.isSingleplayer() ? 0 : ping;

        String pingText = this.text ? "Ping: " : "";

        String hudText = (this.brackets ? "[" : "") + pingText + getColor() + ping + "ms" + (this.brackets ? (EnumChatFormatting.WHITE + "]") : "");

        Meowtils.drawString(hudText, this.posX, this.posY, this.scale, -1);
    }

    private EnumChatFormatting getColor() {
        if (this.dynamicColor) return LatencyHandler.getLatencyColor(ping);
        return EnumChatFormatting.WHITE;
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("[Ping: 1000ms]", 1, this.scale)));
    }

    @Override
    public void onReset() {
        lastPingTime = 0L;
    }

    @Override
    public void onDisable() {
        lastPingTime = 0L;
    }
}