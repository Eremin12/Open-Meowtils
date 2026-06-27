package wtf.tatp.meowtils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.GuiOpenEvent;
import wtf.tatp.meowtils.event.MouseInputEvent;
import wtf.tatp.meowtils.event.RenderTickEvent;
import wtf.tatp.meowtils.event.WorldEvent;
import wtf.tatp.meowtils.event.api.EventManager;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    private Timer timer;

    @Inject(method = "startGame", at = @At("HEAD"))
    private void meowtils$startGameHead(CallbackInfo ci) {
        Meowtils.preInit();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void meowtils$startGameTail(CallbackInfo ci) {
        Meowtils.init();
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void meowtils$runTickHead(CallbackInfo ci) {
        ClientTickEvent event = new ClientTickEvent(ClientTickEvent.Phase.PRE);
        EventManager.post(event);
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void meowtils$runTickReturn(CallbackInfo ci) {
        ClientTickEvent event = new ClientTickEvent(ClientTickEvent.Phase.POST);
        EventManager.post(event);
    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    private void meowtils$clickMouse(CallbackInfo ci) {
        MouseInputEvent event = new MouseInputEvent(MouseInputEvent.Action.LEFT_CLICK);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void meowtils$rightClickMouse(CallbackInfo ci) {
        MouseInputEvent event = new MouseInputEvent(MouseInputEvent.Action.RIGHT_CLICK);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"), cancellable = true)
    private void meowtils$middleClickMouse(CallbackInfo ci) {
        MouseInputEvent event = new MouseInputEvent(MouseInputEvent.Action.MIDDLE_CLICK);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void meowtils$loadWorld(WorldClient world, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            WorldEvent event = new WorldEvent(world, WorldEvent.Type.UNLOAD);
            EventManager.post(event);
        }

        if (world != null) {
            WorldEvent event = new WorldEvent(world, WorldEvent.Type.LOAD);
            EventManager.post(event);
        }
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void meowtils$runGameLoopHead(CallbackInfo ci) {
        RenderTickEvent event = new RenderTickEvent(RenderTickEvent.Phase.PRE, this.timer.renderPartialTicks);
        EventManager.post(event);
    }

    @Inject(method = "runGameLoop", at = @At("TAIL"))
    private void meowtils$runGameLoopTail(CallbackInfo ci) {
        RenderTickEvent event = new RenderTickEvent(RenderTickEvent.Phase.POST, this.timer.renderPartialTicks);
        EventManager.post(event);
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void meowtils$displayGuiScreen(GuiScreen screen, CallbackInfo ci) {
        if (screen != null) {
            GuiOpenEvent event = new GuiOpenEvent(screen);
            EventManager.post(event);
        }
    }
}