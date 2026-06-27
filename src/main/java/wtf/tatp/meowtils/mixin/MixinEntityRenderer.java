package wtf.tatp.meowtils.mixin;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.Event;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.advanced.GhostHand;
import wtf.tatp.meowtils.module.utility.Freelook;
import wtf.tatp.meowtils.module.utility.ViewClip;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    private float thirdPersonDistance;

    private float originalYaw;
    private float originalPrevYaw;
    private float originalPitch;
    private float originalPrevPitch;
    private float originalRenderYawOffset;
    private float originalRotationYawHead;

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;disableLightmap()V"))
    private void meowtils$renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        RenderWorldLastEvent event = new RenderWorldLastEvent(partialTicks);
        EventManager.post(event);
    }

    @Inject(method = "setupOverlayRendering", at = @At("TAIL"))
    private void meowtils$setupOverlayRendering(CallbackInfo ci) {
        RenderGameOverlayEvent event = new RenderGameOverlayEvent();
        EventManager.post(event);
    }

    @Redirect(method = "orientCamera", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D"))
    private double meowtils$orientCameraViewClip(Vec3 hitVec, Vec3 cameraPos) {
        ViewClip viewClip = Module.get(ViewClip.class);
        if (viewClip != null && viewClip.enabled) {
            return this.thirdPersonDistance;
        }
        return hitVec.distanceTo(cameraPos);
    }

    @Inject(method = "orientCamera", at = @At("HEAD"))
    private void meowtils$orientCameraHead(float partialTicks, CallbackInfo ci) {
        if (Freelook.isActive()) {
            Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                this.originalYaw = entity.rotationYaw;
                this.originalPrevYaw = entity.prevRotationYaw;
                this.originalPitch = entity.rotationPitch;
                this.originalPrevPitch = entity.prevRotationPitch;
                this.originalRenderYawOffset = living.renderYawOffset;
                this.originalRotationYawHead = living.rotationYawHead;

                entity.rotationYaw = Freelook.getYaw();
                entity.prevRotationYaw = Freelook.getYaw();
                entity.rotationPitch = Freelook.getPitch();
                entity.prevRotationPitch = Freelook.getPitch();
                living.renderYawOffset = Freelook.getYaw();
                living.rotationYawHead = Freelook.getYaw();
            }
        }
    }

    @Inject(method = "orientCamera", at = @At("RETURN"))
    private void meowtils$orientCameraReturn(float partialTicks, CallbackInfo ci) {
        if (Freelook.isActive()) {
            Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                entity.rotationYaw = this.originalYaw;
                entity.prevRotationYaw = this.originalPrevYaw;
                entity.rotationPitch = this.originalPitch;
                entity.prevRotationPitch = this.originalPrevPitch;
                living.renderYawOffset = this.originalRenderYawOffset;
                living.rotationYawHead = this.originalRotationYawHead;
            }
        }
    }

    @Inject(method = "getMouseOver", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;size()I", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void meowtils$getMouseOverGhostHand(float partialTicks, CallbackInfo ci, Entity entity, double d0, double d1, Vec3 vec3, boolean flag, int i, Vec3 vec31, Vec3 vec32, Vec3 vec33, float f, List<Entity> list, double d2, int j) {
        GhostHand g = Module.get(GhostHand.class);
        if (g == null || !g.enabled) return;
        list.removeIf(g::shouldActivate);
    }
}