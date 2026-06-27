package wtf.tatp.meowtils.module.render;

import java.time.LocalTime;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class TimeChanger extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int time = 12;
    @Config
    public boolean realTime = false;
    @Config
    public int increaseKey = 0;
    @Config
    public int decreaseKey = 0;

    private static int tickCounter = 0;

    public TimeChanger() {
        super("TimeChanger", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Changes time clientside.");
        addSlider(new SliderValue("Time", 0.0D, 24.0D, 1.0D, "hours", "time", this, Integer.class));
        addToggle(new ToggleValue("Use real time", "realTime", this));
        addBind(new BindValue("Increase", "increaseKey", this));
        addBind(new BindValue("Decrease", "decreaseKey", this));
    }

    public static long getRealTime() {
        LocalTime now = LocalTime.now();
        double totalHours = now.getHour() + now.getMinute() / 60.0D + now.getSecond() / 3600.0D;
        return toIngameTime(totalHours);
    }

    public static long toIngameTime(double hour) {
        double shifted = hour - 6.0D;
        if (shifted < 0.0D) shifted += 24.0D;
        return (long) (shifted * 1000.0D);
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Keyboard.isKeyDown(0)) return;
        if (this.realTime) return;
        if (this.time < 0 || this.time > 24) return;

        tickCounter++;
        if (tickCounter < 2) return;
        tickCounter = 0;

        if (Keyboard.isKeyDown(this.increaseKey)) {
            if (this.time > 23) return;
            this.time++;
        } else if (Keyboard.isKeyDown(this.decreaseKey)) {
            if (this.time < 1) return;
            this.time--;
        }
    }
}