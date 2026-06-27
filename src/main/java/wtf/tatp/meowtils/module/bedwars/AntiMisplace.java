package wtf.tatp.meowtils.module.bedwars;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.MouseInputEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.meowtils.Notifications;

public class AntiMisplace extends Module {
    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;

    public AntiMisplace() {
        super("AntiMisplace", Module.Category.Bedwars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Prevents placing obsidian if it isn't around a bed.");
    }

    @EventTarget
    public void onMouseInput(MouseInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        World world = mc.theWorld;
        if (player == null || world == null)
            return;
        if (mc.objectMouseOver == null)
            return;
        if (event.getAction() != MouseInputEvent.Action.RIGHT_CLICK)
            return;
        MovingObjectPosition rayTrace = mc.objectMouseOver;
        if (rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive())
            return;
        if (player.getHeldItem() == null)
            return;
        if (player.getHeldItem().getItem() != Item.getItemFromBlock(Blocks.obsidian))
            return;

        if (!isAdjacentToBed(world, rayTrace.getBlockPos().add(rayTrace.sideHit.getDirectionVec()))) {
            event.setCancelled(true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Prevented you from placing obsidian!");
            }

            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show("AntiMisplace", "Can't place here!", NotificationManager.Type.ALERT, 1500L);
            }
        }
    }

    private boolean isAdjacentToBed(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == Blocks.bed) {
                return true;
            }
        }
        return false;
    }
}