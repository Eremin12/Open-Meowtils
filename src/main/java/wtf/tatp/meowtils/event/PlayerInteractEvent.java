package wtf.tatp.meowtils.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.tatp.meowtils.event.api.Event;

public class PlayerInteractEvent extends Event {

    private final EntityPlayerSP player;
    private final WorldClient world;
    private final Action action;
    private final BlockPos pos;
    private final EnumFacing facing;

    public enum Action {
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK;
    }

    public PlayerInteractEvent(EntityPlayerSP player, WorldClient world, Action action, BlockPos pos, EnumFacing facing) {
        this.player = player;
        this.world = world;
        this.action = action;
        this.pos = pos;
        this.facing = facing;
    }

    public EntityPlayerSP getPlayer() {
        return this.player;
    }

    public WorldClient getWorld() {
        return this.world;
    }

    public Action getAction() {
        return this.action;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }
}