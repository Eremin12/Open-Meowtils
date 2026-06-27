package wtf.tatp.meowtils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.CapeManager;
import wtf.tatp.meowtils.manager.SkinManager;
import wtf.tatp.meowtils.module.hypixel.AccountHider;
import wtf.tatp.meowtils.module.render.Cape;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends EntityPlayer {

    public MixinAbstractClientPlayer(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void meowtils$getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        Cape c = Module.get(Cape.class);
        if (c != null && c.enabled) {
            Minecraft mc = Minecraft.getMinecraft();
            boolean isLocalPlayer = (Object) this == mc.thePlayer;
            boolean isOtherPlayer = (this instanceof EntityPlayer) && !isLocalPlayer;

            if (isLocalPlayer || (isOtherPlayer && c.renderOnAll)) {
                ResourceLocation cape = CapeManager.getCape(c.selectedCape);
                if (cape != null) {
                    cir.setReturnValue(cape);
                }
            }
        }
    }

    @Inject(method = "getLocationSkin", at = @At("RETURN"), cancellable = true)
    private void meowtils$getLocationSkin(CallbackInfoReturnable<ResourceLocation> cir) {
        Minecraft mc = Minecraft.getMinecraft();
        if ((Object) this != mc.thePlayer) return;

        AccountHider accountHider = Module.get(AccountHider.class);
        if (accountHider == null || !accountHider.enabled) return;
        if (!accountHider.skin) return;

        ResourceLocation skin = SkinManager.getSkin(accountHider.skinLocation);
        if (skin != null) {
            cir.setReturnValue(skin);
        }
    }
}