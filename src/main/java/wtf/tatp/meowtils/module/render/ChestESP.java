package wtf.tatp.meowtils.module.render;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.PlayerInteractEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.WorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Render;

public class ChestESP extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public int redOpen = 255;
    @Config
    public int greenOpen = 0;
    @Config
    public int blueOpen = 0;
    @Config
    public String mode = "Normal";
    @Config
    public String render = "Full";
    @Config
    public boolean skywarsOnly = false;

    private static final List<BlockPos> HIGHLIGHTED_CHESTS = new CopyOnWriteArrayList<>();

    public ChestESP() {
        super("ChestESP", Module.Category.Render);
        tag(Module.ModuleTag.SAFE);
        tooltip("Highlights chests");
        addMode(new ModeValue("Mode", Arrays.asList("Opened", "Normal", "Both"), "mode", this));
        addMode(new ModeValue("Render", Arrays.asList("Full", "Outline"), "render", this));
        ColorLink color = new ColorLink("red", "green", "blue", this);
        ColorLink openedColor = new ColorLink("redOpen", "greenOpen", "blueOpen", this);
        addColor(new ColorValue("Chest color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addColor(new ColorValue("Opened color", openedColor));
        addToggle(new ToggleValue("Skywars only", "skywarsOnly", this));
        addButton(new ButtonValue("Reset opened", 5.0F, () -> {
            HIGHLIGHTED_CHESTS.clear();
            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                Meowtils.addMessage("Reset opened chest positions.");
            }
            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show("ChestESP", "Reset positions.", NotificationManager.Type.INFO, 1000L);
            }
        }));
    }

    @EventTarget
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        if (event.getPlayer() != this.mc.thePlayer) return;

        BlockPos pos = event.getPos();
        TileEntity tile = this.mc.theWorld.getTileEntity(pos);

        if (tile instanceof TileEntityChest && !HIGHLIGHTED_CHESTS.contains(pos)) {
            HIGHLIGHTED_CHESTS.add(pos);
        }
    }

    @EventTarget
    public void onWorldUnload(WorldEvent event) {
        if (event.getType() != WorldEvent.Type.UNLOAD) return;
        HIGHLIGHTED_CHESTS.clear();
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (this.skywarsOnly && (Skywars.GAME.isNotActive() && Skywars.MINI.isNotActive())) return;

        boolean full = this.render.equals("Full");
        boolean outline = this.render.equals("Outline");

        for (TileEntity tile : this.mc.theWorld.loadedTileEntityList) {
            if (!(tile instanceof TileEntityChest)) continue;
            BlockPos pos = tile.getPos();

            if (this.mode.equals("Opened") && !HIGHLIGHTED_CHESTS.contains(pos)) {
                continue;
            }

            AxisAlignedBB box = this.mc.theWorld.getBlockState(pos).getBlock().getCollisionBoundingBox(this.mc.theWorld, pos, this.mc.theWorld.getBlockState(pos));
            if (box == null) continue;

            boolean opened = HIGHLIGHTED_CHESTS.contains(pos);

            Color fillColor, outlineColor;
            switch (this.mode) {
                case "Opened":
                case "Normal":
                    fillColor = ColorUtil.getColor(this.red, this.green, this.blue, 120);
                    outlineColor = ColorUtil.getColor(this.red, this.green, this.blue, 255);
                    break;
                case "Both":
                    if (opened) {
                        fillColor = ColorUtil.getColor(this.redOpen, this.greenOpen, this.blueOpen, 120);
                        outlineColor = ColorUtil.getColor(this.redOpen, this.greenOpen, this.blueOpen, 255);
                    } else {
                        fillColor = ColorUtil.getColor(this.red, this.green, this.blue, 120);
                        outlineColor = ColorUtil.getColor(this.red, this.green, this.blue, 255);
                    }
                    break;
                default:
                    continue;
            }

            Render.drawBlockBox(box, pos, full, fillColor, outline, outlineColor, 0.06D, 0.06D, 0.06D);
        }
    }
}