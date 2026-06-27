package wtf.tatp.meowtils.module.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Server;

public class NoParticles extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean removeGlyph = true;
    @Config
    public boolean removeSponge = true;
    @Config
    public boolean removeBreak = false;

    public NoParticles() {
        super("NoParticles", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Removes certain particles for visibility and performance.");
        addToggle(new ToggleValue("Remove bedwars glyph", "removeGlyph", this));
        addToggle(new ToggleValue("Remove bedwars sponge", "removeSponge", this));
        addToggle(new ToggleValue("Remove block break", "removeBreak", this));
    }

    @EventTarget
    public void onPacketReceived(ReceivePacketEvent event) {
        if (Server.HYPIXEL.isNotActive() && Server.UNIVERSAL.isNotActive()) return;
        if (Bedwars.GAME.isNotActive()) return;

        if (event.getPacket() instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.getPacket();

            if (this.removeGlyph && packet.getParticleType() == EnumParticleTypes.REDSTONE) {
                event.setCancelled(true);
            }

            if (this.removeSponge && packet.getParticleType() == EnumParticleTypes.CLOUD) {
                event.setCancelled(true);
            }
        }
    }

    public static boolean isRedstoneBlock(BlockPos pos) {
        Minecraft mc = Minecraft.getMinecraft();
        return (mc.theWorld.getBlockState(pos).getBlock() == Blocks.redstone_block);
    }
}