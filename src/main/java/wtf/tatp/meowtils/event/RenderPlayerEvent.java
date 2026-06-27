package wtf.tatp.meowtils.event;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import wtf.tatp.meowtils.event.api.Event;

public class RenderPlayerEvent extends Event {

    private final RenderPlayer renderer;
    private final AbstractClientPlayer player;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float partialTicks;
    private final Stage stage;

    public enum Stage {
        PRE,
        POST;
    }

    public RenderPlayerEvent(RenderPlayer renderer, AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, Stage stage) {
        this.renderer = renderer;
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.partialTicks = partialTicks;
        this.stage = stage;
    }

    public RenderPlayer getRenderer() {
        return this.renderer;
    }

    public AbstractClientPlayer getPlayer() {
        return this.player;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Stage getStage() {
        return this.stage;
    }
}