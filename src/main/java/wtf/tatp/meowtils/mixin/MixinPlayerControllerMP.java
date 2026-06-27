package wtf.tatp.meowtils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.event.AttackEntityEvent;
import wtf.tatp.meowtils.event.PlayerInteractEvent;
import wtf.tatp.meowtils.event.api.EventManager;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    @Shadow
    public float curBlockDamageMP;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void meowtils$attackEntity(EntityPlayer player, Entity target, CallbackInfo ci) {
        AttackEntityEvent event = new AttackEntityEvent(player, target);
        EventManager.post(event);
    }

    @Inject(method = "onPlayerRightClick", at = @At("HEAD"))
    private void meowtils$onPlayerRightClick(EntityPlayerSP player, WorldClient world, ItemStack stack, BlockPos pos, EnumFacing side, Vec3 hitVec, CallbackInfoReturnable<Boolean> cir) {
        PlayerInteractEvent event = new PlayerInteractEvent(player, world, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, pos, side);
        EventManager.post(event);
    }

    @Inject(method = "clickBlock", at = @At("HEAD"))
    private void meowtils$clickBlock(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        WorldClient world = Minecraft.getMinecraft().theWorld;
        PlayerInteractEvent event = new PlayerInteractEvent(player, world, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, pos, side);
        EventManager.post(event);
    }
}