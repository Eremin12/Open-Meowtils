package wtf.tatp.meowtils.util;

import net.minecraft.client.Minecraft;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.event.api.EventTarget;

public class DelayedTask {

    private final Runnable runnable;
    private int counter;

    public DelayedTask(Runnable task, int ticks) {
        this.runnable = task;
        this.counter = ticks;
        EventManager.register(this);
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (event.getPhase() != ClientTickEvent.Phase.PRE) return;

        if (this.counter-- <= 0) {
            EventManager.unregister(this);
            try {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.theWorld == null || mc.thePlayer == null) return;
                this.runnable.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}