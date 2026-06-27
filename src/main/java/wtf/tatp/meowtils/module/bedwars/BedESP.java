package wtf.tatp.meowtils.module.bedwars;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.chunk.Chunk;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.WorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.*;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Render;

public class BedESP extends Module {
    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public String mode = "Full";
    @Config
    public boolean showObsidian = true;
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public float opacity = 25.0F;
    @Config
    public boolean bedwarsOnly = false;

    private static boolean scanned = false;
    public static final Set<BlockPos> BEDS = ConcurrentHashMap.newKeySet();
    public static final Set<BlockPos> OBSIDIAN = ConcurrentHashMap.newKeySet();
    public final Minecraft mc = Minecraft.getMinecraft();

    public BedESP() {
        super("BedESP", Module.Category.Bedwars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Highlight beds and surrounding obsidian.");
        ColorLink colorLink = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Bed color", colorLink));
        addSaturation(new SaturationValue(colorLink));
        addBrightness(new BrightnessValue(colorLink));
        addOpacity(new OpacityValue("Opacity", "opacity", this));
        addMode(new ModeValue("Mode", Arrays.asList("Full", "Outline"), "mode", this));
        addToggle(new ToggleValue("Show obsidian", "showObsidian", this));
        addToggle(new ToggleValue("Bedwars only", "bedwarsOnly", this));
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        if (bedwarsOnly && Bedwars.GAME.isNotActive())
            return;
        renderBeds();
        if (showObsidian) {
            renderObsidian();
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST)
            return;
        if (bedwarsOnly && Bedwars.ALL.isNotActive())
            return;
        if (scanned)
            return;
        scanned = true;
        BEDS.clear();
        OBSIDIAN.clear();
        init();
    }

    @EventTarget
    public void onWorldEvent(WorldEvent event) {
        if (event.getType() != WorldEvent.Type.UNLOAD)
            return;
        BEDS.clear();
        OBSIDIAN.clear();
        scanned = false;
    }

    public static void updateBlocks(BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockBed && state.getValue((IProperty) BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
            BEDS.add(pos);
            checkObsidian(pos);
            return;
        }
        if (BEDS.remove(pos)) {
            OBSIDIAN.removeIf(o -> o.distanceSq((Vec3i) pos) <= 4.0D);
        }
        if (block == Blocks.obsidian || block == Blocks.portal) {
            for (BlockPos bed : BEDS) {
                if (bed.distanceSq((Vec3i) pos) <= 4.0D) {
                    checkObsidian(bed);
                }
            }
        }
    }

    private static void checkObsidian(BlockPos headPos) {
        Minecraft mc = Minecraft.getMinecraft();
        OBSIDIAN.removeIf(pos -> pos.distanceSq((Vec3i) headPos) <= 4.0D);
        IBlockState state = mc.theWorld.getBlockState(headPos);
        if (!(state.getBlock() instanceof BlockBed))
            return;
        EnumFacing bedFacing = state.getValue((IProperty<EnumFacing>) BlockBed.FACING);
        BlockPos foot = headPos.offset(bedFacing.getOpposite());
        for (BlockPos part : new BlockPos[]{headPos, foot}) {
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset = part.offset(facing);
                if (mc.theWorld.getBlockState(offset).getBlock() == Blocks.obsidian) {
                    OBSIDIAN.add(offset);
                }
            }
        }
    }

    public static void updateChunk(Chunk chunk) {
        int minX = chunk.xPosition << 4;
        int minZ = chunk.zPosition << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    BlockPos pos = new BlockPos(minX + x, y, minZ + z);
                    IBlockState state = chunk.getBlockState(pos);
                    if (state.getBlock() instanceof BlockBed && state.getValue((IProperty) BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
                        BEDS.add(pos);
                        checkObsidian(pos);
                    }
                }
            }
        }
    }

    private void renderBeds() {
        Iterator<BlockPos> iterator = BEDS.iterator();
        while (iterator.hasNext()) {
            BlockPos head = iterator.next();
            IBlockState state = mc.theWorld.getBlockState(head);
            if (!(state.getBlock() instanceof BlockBed) || state.getValue((IProperty) BlockBed.PART) != BlockBed.EnumPartType.HEAD) {
                iterator.remove();
                continue;
            }
            EnumFacing bedFacing = state.getValue((IProperty<EnumFacing>) BlockBed.FACING);
            BlockPos foot = head.offset(bedFacing.getOpposite());
            IBlockState footState = mc.theWorld.getBlockState(foot);
            if (!(footState.getBlock() instanceof BlockBed)) {
                continue;
            }
            boolean renderMode = mode.equals("Full");
            Color color = ColorUtil.getColor(red, green, blue, ColorUtil.convertOpacity(opacity));
            AxisAlignedBB box = new AxisAlignedBB(
                    Math.min(head.getX(), foot.getX()),
                    head.getY(),
                    Math.min(head.getZ(), foot.getZ()),
                    Math.max(head.getX() + 1, foot.getX() + 1),
                    head.getY() + 0.5625D,
                    Math.max(head.getZ() + 1, foot.getZ() + 1)
            );
            Render.drawBlockBox(box, null, renderMode, color, !renderMode, color, 0.0D, 0.0D, 0.0D);
        }
    }

    private void renderObsidian() {
        Iterator<BlockPos> iterator = OBSIDIAN.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            IBlockState state = mc.theWorld.getBlockState(pos);
            if (state.getBlock() != Blocks.obsidian) {
                iterator.remove();
                continue;
            }
            boolean renderMode = mode.equals("Full");
            Color color = ColorUtil.getColor(170, 0, 170, ColorUtil.convertOpacity(opacity));
            AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            Render.drawBlockBox(box, pos, renderMode, color, !renderMode, color, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void init() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        BlockPos player = mc.thePlayer.getPosition();
        for (BlockPos pos : BlockPos.getAllInBox(player.add(-64, -20, -64), player.add(64, 20, 64))) {
            IBlockState state = mc.theWorld.getBlockState(pos);
            if (state.getBlock() instanceof BlockBed && state.getValue((IProperty) BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
                BEDS.add(pos);
                checkObsidian(pos);
            }
        }
    }

    public void onEnable() {
        BEDS.clear();
        OBSIDIAN.clear();
        scanned = false;
        init();
    }

    public void onDisable() {
        BEDS.clear();
        OBSIDIAN.clear();
        scanned = false;
    }
}