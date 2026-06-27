package wtf.tatp.meowtils.mixin;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.event.KeyInputEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.module.utility.NullMove;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding {

    @Shadow
    private int keyCode;

    @Shadow
    private boolean pressed;

    @Shadow
    private int pressTime;

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void meowtils$isPressed(CallbackInfoReturnable<Boolean> cir) {
        if (this.pressTime <= 0) return;

        KeyBinding self = (KeyBinding) (Object) this;

        KeyInputEvent event = new KeyInputEvent(self);
        EventManager.post(event);

        if (event.isCancelled()) {
            this.pressed = false;
            this.pressTime = 0;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private void meowtils$isKeyDown(CallbackInfoReturnable<Boolean> cir) {
        if (!NullMove.shouldOverride()) return;
        if (!NullMove.isMovementKey(this.keyCode)) return;

        cir.setReturnValue(NullMove.checkKey(this.keyCode, this.pressed));
        cir.cancel();
    }

    @Inject(method = "setKeyBindState", at = @At("HEAD"))
    private static void meowtils$setKeyBindState(int keyCode, boolean pressed, CallbackInfo ci) {
        if (!NullMove.shouldOverride()) return;
        if (!NullMove.isMovementKey(keyCode)) return;

        NullMove.updateTime(keyCode, pressed);
    }
}