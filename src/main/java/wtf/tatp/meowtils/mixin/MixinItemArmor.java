package wtf.tatp.meowtils.mixin;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.skywars.NoArmorDye;

@Mixin(ItemArmor.class)
public abstract class MixinItemArmor {

    @Inject(method = "getColorFromItemStack", at = @At("HEAD"), cancellable = true)
    private void meowtils$getColorFromItemStack(ItemStack stack, int renderPass, CallbackInfoReturnable<Integer> cir) {
        ItemArmor self = (ItemArmor) (Object) this;
        NoArmorDye s = Module.get(NoArmorDye.class);

        if (s != null && s.enabled && Skywars.GAME.isActive() && self.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && (s.mode.equals("Item") || s.mode.equals("Both"))) {
            cir.setReturnValue(10511680);
        }
    }
}