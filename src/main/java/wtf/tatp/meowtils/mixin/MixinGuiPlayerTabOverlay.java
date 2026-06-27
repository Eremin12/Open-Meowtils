package wtf.tatp.meowtils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.manager.icons.IconManager;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay
{
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void meowtils$getPlayerName(NetworkPlayerInfo info, CallbackInfoReturnable<String> cir)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen == null)
            return;
        String originalName = cir.getReturnValue();
        String prefix = IconManager.buildPrefix(info.getGameProfile(), true, false);
        String suffix = IconManager.buildSuffix(info.getGameProfile(), true, false);
        cir.setReturnValue(prefix + originalName + suffix);
    }
}