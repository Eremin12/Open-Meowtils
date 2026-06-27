package wtf.tatp.meowtils.mixin;

import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.skywars.NoArmorDye;

@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase {

    @Redirect(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemArmor;getColor(Lnet/minecraft/item/ItemStack;)I"))
    private int meowtils$renderLayer(ItemArmor item, ItemStack stack) {
        NoArmorDye s = Module.get(NoArmorDye.class);
        if (s != null && s.enabled && Skywars.GAME.isActive() && item.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && (s.mode.equals("Model") || s.mode.equals("Both"))) {
            return 10511680;
        }
        return item.getColor(stack);
    }
}