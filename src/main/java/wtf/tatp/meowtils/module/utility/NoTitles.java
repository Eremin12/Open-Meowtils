package wtf.tatp.meowtils.module.utility;

import net.minecraft.network.play.server.S45PacketTitle;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;

public class NoTitles extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;

    public NoTitles() {
        super("NoTitles", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Disables titles");
    }

    @EventTarget
    public void onPacketReceived(ReceivePacketEvent event) {
        if (event.getPacket() instanceof S45PacketTitle) {
            event.setCancelled(true);
        }
    }
}