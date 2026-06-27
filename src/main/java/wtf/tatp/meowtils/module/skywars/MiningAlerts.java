package wtf.tatp.meowtils.module.skywars;

import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.Util;

public class MiningAlerts extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public String alertType = "Chat";
    @Config
    public boolean sound = true;

    public MiningAlerts() {
        super("MiningAlerts", Module.Category.Skywars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts you when a player mines Diamond Ore.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
    }

    @EventTarget
    public void onPacketReceived(ReceivePacketEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;
        if (!(event.getPacket() instanceof S25PacketBlockBreakAnim)) return;
        if (Skywars.GAME.isNotActive() && Skywars.MINI.isNotActive()) return;

        S25PacketBlockBreakAnim packet = (S25PacketBlockBreakAnim) event.getPacket();
        BlockPos pos = packet.getPosition();
        int progress = packet.getProgress();
        if (this.mc.theWorld.getBlockState(pos).getBlock() != Blocks.diamond_ore) return;
        if (progress != 9) return;

        EntityPlayer closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == null || player == this.mc.thePlayer) continue;
            double distanceSq = player.getDistanceSq(pos);
            if (distanceSq < closestDistance) {
                closestDistance = distanceSq;
                closest = player;
            }
        }

        if (closest != null) {
            String text = NameUtil.getTabDisplayName(closest.getName()) + EnumChatFormatting.GRAY + " mined " + EnumChatFormatting.AQUA + "Diamond Ore";

            if (!this.alertType.equals("Notification")) {
                Meowtils.addMessage(text);
            }

            if (!this.alertType.equals("Chat")) {
                NotificationManager.show("MiningAlerts", text, NotificationManager.Type.ALERT, 1500L);
            }

            if (this.sound) {
                Util.playSound(Util.Sound.PING_DEEP, 100);
            }
        }
    }
}