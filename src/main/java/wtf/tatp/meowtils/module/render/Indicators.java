package wtf.tatp.meowtils.module.render;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Render;

public class Indicators extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public float opacity = 50.0F;
    @Config
    public String mode = "3D";
    @Config
    public String render = "Full";
    @Config
    public boolean arrow = true;
    @Config
    public float arrowExpand = 0.0F;
    @Config
    public boolean fireball = true;
    @Config
    public float fireballExpand = 0.0F;
    @Config
    public boolean pearl = true;
    @Config
    public float pearlExpand = 0.0F;

    public Indicators() {
        super("Indicators", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Highlights dangerous projectiles.");
        addMode(new ModeValue("Mode", Arrays.asList("3D", "2D"), "mode", this));
        addMode(new ModeValue("Render", Arrays.asList("Full", "Outline"), "render", this));
        addOpacity(new OpacityValue("Opacity", "opacity", this));
        addCheck(new CheckValue("Arrows", "arrow", this));
        addSlider(new SliderValue("Arrow expand", 0.0D, 1.0D, 0.1D, "x", "arrowExpand", this, Float.class));
        addCheck(new CheckValue("§cFireballs", "fireball", this));
        addSlider(new SliderValue("Fireball expand", 0.0D, 1.0D, 0.1D, "x", "fireballExpand", this, Float.class));
        addCheck(new CheckValue("§5Ender Pearls", "pearl", this));
        addSlider(new SliderValue("Pearl expand", 0.0D, 1.0D, 0.1D, "x", "pearlExpand", this, Float.class));
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;

        boolean renderMode = this.render.equals("Full");
        boolean boxMode = this.mode.equals("3D");

        Color arrowColor = ColorUtil.getColor(255, 255, 255, ColorUtil.convertOpacity(this.opacity));
        Color fireballColor = ColorUtil.getColor(170, 0, 0, ColorUtil.convertOpacity(this.opacity));
        Color pearlColor = ColorUtil.getColor(170, 0, 170, ColorUtil.convertOpacity(this.opacity));

        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            float expand = getExpandValue(entity);

            if (entity instanceof net.minecraft.entity.projectile.EntityArrow && this.arrow) {
                Render.drawEntityBox(entity, boxMode, renderMode, arrowColor, !renderMode, arrowColor, expand, expand, expand);
            }

            if (entity instanceof net.minecraft.entity.projectile.EntityFireball && this.fireball) {
                Render.drawEntityBox(entity, boxMode, renderMode, fireballColor, !renderMode, fireballColor, expand, expand, expand);
            }

            if (entity instanceof net.minecraft.entity.item.EntityEnderPearl && this.pearl) {
                Render.drawEntityBox(entity, boxMode, renderMode, pearlColor, !renderMode, pearlColor, expand, expand, expand);
            }
        }
    }

    private float getExpandValue(Entity entity) {
        if (entity instanceof net.minecraft.entity.projectile.EntityArrow) return this.arrowExpand;
        if (entity instanceof net.minecraft.entity.projectile.EntityFireball) return this.fireballExpand;
        if (entity instanceof net.minecraft.entity.item.EntityEnderPearl) return this.pearlExpand;
        return 0.0F;
    }
}