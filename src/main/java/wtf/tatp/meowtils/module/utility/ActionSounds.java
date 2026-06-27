package wtf.tatp.meowtils.module.utility;

import net.minecraft.network.play.server.S19PacketEntityStatus;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.AttackEntityEvent;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.Util;

public class ActionSounds extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean blockSound = true;
    @Config
    public int volume = 100;
    @Config
    public boolean critSound = true;

    private static long hurtTime = 0L;
    private static long lastCritTime = 0L;

    public ActionSounds() {
        super("ActionSounds", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Plays a sound when performing certain actions.");
        addSlider(new SliderValue("Volume", 0.0D, 100.0D, 5.0D, "%", "volume", this, Integer.class));
        addToggle(new ToggleValue("Blocked damage", "blockSound", this));
        addToggle(new ToggleValue("Critical hit", "critSound", this));
    }

    @EventTarget
    public void onPacketReceived(ReceivePacketEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;
        if (!this.blockSound) return;

        if (event.getPacket() instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus packet = (S19PacketEntityStatus) event.getPacket();
            if (packet.getEntity(this.mc.theWorld) != this.mc.thePlayer || packet.getOpCode() != 2) return;
            if (!this.mc.thePlayer.isBlocking()) return;

            long now = System.currentTimeMillis();
            if (now - hurtTime >= 250L) {
                Util.playSound(Util.Sound.ANVIL, this.volume);
                hurtTime = now;
            }
        }
    }

    @EventTarget
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getTarget() instanceof net.minecraft.entity.player.EntityPlayer)) return;
        if (!this.critSound) return;
        if (this.mc.thePlayer.onGround || this.mc.thePlayer.motionY <= 0.0F || this.mc.thePlayer.isInWater() || this.mc.thePlayer.isOnLadder() || this.mc.thePlayer.isRiding()) return;

        long now = System.currentTimeMillis();
        if (now - lastCritTime > 250L) {
            Util.playSound(Util.Sound.CRIT, this.volume);
            lastCritTime = now;
        }
    }
}