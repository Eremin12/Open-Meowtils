package wtf.tatp.meowtils.module.bedwars;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class BedTracker extends Module {
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
    public float scale = 0.65F;
    @Config
    public int frequency = 10;
    @Config
    public int distance = 50;
    @Config
    public boolean sound = true;
    @Config
    public boolean hud = true;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;

    private static final String CROSS_ICON = EnumChatFormatting.RED + "✗";
    private static final String CHECK_ICON = EnumChatFormatting.GREEN + "✓";
    private static BlockPos bedPos = null;
    private static long bedScanTime = 0L;
    private static long startTime = 0L;
    private static boolean rangeAlert = false;
    private static final Map<UUID, Long> LAST_ALERT_TIME = new HashMap<>();

    private final Minecraft mc = Minecraft.getMinecraft();

    public BedTracker() {
        super("BedTracker", Module.Category.Bedwars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts when enemy players are near your bed and shows distance to your bed on screen.");
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addSlider(new SliderValue("Alert frequency", 5.0D, 30.0D, 1.0D, "s", "frequency", this, int.class));
        addSlider(new SliderValue("Max distance", 10.0D, 100.0D, 5.0D, "m", "distance", this, int.class));
        addToggle(new ToggleValue("Ping sound", "sound", this));
        addToggle(new ToggleValue("Show HUD", "hud", this));
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() == 2)
            return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive())
            return;
        String msg = event.getComponent().getUnformattedText();

        if (!msg.contains(":")) {
            if (msg.contains("The game starts in 1 second!")) {
                bedPos = null;
                bedScanTime = System.currentTimeMillis() + 6000L;
                startTime = System.currentTimeMillis() + 7000L;
            } else if (msg.equals("You will respawn in 6 seconds!")) {
                bedPos = null;
                bedScanTime = System.currentTimeMillis() + 9000L;
                startTime = System.currentTimeMillis() + 10000L;
            }
            Meowtils.debugMessage(EnumChatFormatting.YELLOW + "[BedTracker]: " + EnumChatFormatting.WHITE + "Locating bed..");
        }

        if (event.getComponent().getUnformattedText().contains("BED DESTRUCTION > Your Bed") && !msg.contains(":")) {
            bedPos = null;
            Meowtils.addMessage(EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "⚠ Your bed was destroyed!");
        }

        if (msg.contains("Your team swapped and you are now:")) {
            bedPos = null;
            bedScanTime = System.currentTimeMillis() + 1000L;
            startTime = System.currentTimeMillis() + 1000L;
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (event.getPhase() != ClientTickEvent.Phase.POST || mc.thePlayer == null || mc.theWorld == null)
            return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive())
            return;
        if (bedPos == null && bedScanTime > 0L && System.currentTimeMillis() > bedScanTime) {
            bedPos = findNearbyBed(mc.theWorld, mc.thePlayer.getPosition(), 25);
            bedScanTime = 0L;
            if (bedPos != null) {
                Meowtils.addMessage(EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD + "✓ " + EnumChatFormatting.RESET + "Whitelisted your bed at" + EnumChatFormatting.GRAY + " (" + EnumChatFormatting.GREEN + bedPos.getX() + ", " + bedPos.getY() + ", " + bedPos.getZ() + EnumChatFormatting.GRAY + ")");
            } else {
                Meowtils.addMessage(EnumChatFormatting.RED + "⚠ Error locating your bed.");
            }
        }

        if (bedPos != null && isBedOutOfRange() && !rangeAlert) {
            Meowtils.addMessage(EnumChatFormatting.BOLD.toString() + EnumChatFormatting.LIGHT_PURPLE + "⚠ Your bed is out of range!");
            rangeAlert = true;
        } else if (!isBedOutOfRange() && bedPos != null) {
            rangeAlert = false;
        }

        if (bedPos != null && mc.theWorld != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime < 6000L)
                return;
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player == mc.thePlayer || TeamUtil.isBot(player) || TeamUtil.isTeam(player) || TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName())) {
                    continue;
                }
                if (player.isDead || player.getHealth() < 100)
                    continue;
                int distanceToBed = (int)player.getDistance(bedPos.getX(), bedPos.getY(), bedPos.getZ());
                if (distanceToBed > this.distance)
                    continue;
                UUID uuid = player.getUniqueID();
                long lastAlert = LAST_ALERT_TIME.getOrDefault(uuid, 0L);
                if (currentTime - lastAlert >= this.frequency * 1000L) {
                    String distanceColor = distanceToBed <= 5 ? EnumChatFormatting.DARK_RED.toString() : distanceToBed <= 15 ? EnumChatFormatting.RED.toString() : distanceToBed <= 30 ? EnumChatFormatting.GOLD.toString() : distanceToBed <= 40 ? EnumChatFormatting.YELLOW.toString() : EnumChatFormatting.GREEN.toString();
                    Meowtils.addMessage(NameUtil.getTabDisplayName(player.getName()) + EnumChatFormatting.WHITE + " is " + distanceColor + distanceToBed + EnumChatFormatting.WHITE + " blocks from your bed!" + distanceColor + " ⚠");
                    PartyNotifier.bedTracker(player.getName(), distanceToBed);
                    LAST_ALERT_TIME.put(uuid, currentTime);
                    if (this.sound) {
                        Util.playSound(Util.Sound.MEOW, 100);
                    }
                }
            }
            LAST_ALERT_TIME.keySet().removeIf(uuid -> mc.theWorld.getPlayerEntityByUUID(uuid) == null);
        }
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive() && !GuiUtil.inEditor())
            return;
        if (mc.currentScreen != null && !GuiUtil.inEditor())
            return;
        if (!this.hud) {
            return;
        }
        EnumChatFormatting distanceColor = getDistanceToBed() < 70 ? EnumChatFormatting.GREEN : isBedOutOfRange() ? EnumChatFormatting.RED : EnumChatFormatting.YELLOW;
        String separator = bedPos != null ? EnumChatFormatting.GRAY + " | " + EnumChatFormatting.RESET : "";
        int color = ColorUtil.rgb(this.red, this.green, this.blue);
        String bedState = bedPos == null ? CROSS_ICON : CHECK_ICON;
        String distance = bedPos == null ? "" : "Distance: " + distanceColor + getDistanceToBed();
        String doesChunkExist = isBedOutOfRange() && bedPos != null ? EnumChatFormatting.RED + " ⚠" : "";
        Meowtils.drawString("Bed: " + bedState + separator + distance + doesChunkExist, this.posX, this.posY, this.scale, color);
    }

    private BlockPos findNearbyBed(World world, BlockPos center, int radius) {
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {
                for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockBed) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private int getDistanceToBed() {
        if (bedPos == null) return 0;
        Vec3 playerPos = mc.thePlayer.getPositionVector();
        return (int)Math.sqrt(playerPos.distanceTo(new Vec3(bedPos.getX() + 0.5D, bedPos.getY(), bedPos.getZ() + 0.5D)));
    }

    private boolean isBedOutOfRange() {
        if (bedPos == null || mc.thePlayer == null || mc.theWorld == null) return true;
        int chunkX = bedPos.getX() >> 4;
        int chunkZ = bedPos.getZ() >> 4;
        ChunkProviderServer chunkProvider = (ChunkProviderServer) mc.theWorld.getChunkProvider();
        boolean serverOutOfRange = !chunkProvider.chunkExists(chunkX, chunkZ);
        int renderDistanceBlocks = mc.gameSettings.renderDistanceChunks * 16;
        double dx = mc.thePlayer.posX - bedPos.getX();
        double dz = mc.thePlayer.posZ - bedPos.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        boolean clientOutOfRange = horizontalDistance > renderDistanceBlocks;
        return serverOutOfRange || clientOutOfRange;
    }

    public List<HudEntry> hudEditor() {
        if (this.hud) {
            return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("Bed: X   Distance: 999 X", 1, this.scale)));
        }
        return Collections.emptyList();
    }

    public void onReset() {
        bedPos = null;
        bedScanTime = 0L;
        startTime = 0L;
        LAST_ALERT_TIME.clear();
    }
}