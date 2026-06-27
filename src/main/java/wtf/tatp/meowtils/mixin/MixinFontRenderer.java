package wtf.tatp.meowtils.mixin;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wtf.tatp.meowtils.event.RenderStringEvent;
import wtf.tatp.meowtils.event.api.EventManager;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String meowtils$renderString(String string) {
        if (string == null || string.isEmpty()) return string;

        RenderStringEvent event = new RenderStringEvent(string);
        EventManager.post(event);

        return event.getString();
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String meowtils$getStringWidth(String string) {
        if (string == null || string.isEmpty()) return string;

        RenderStringEvent event = new RenderStringEvent(string);
        EventManager.post(event);

        return event.getString();
    }
}