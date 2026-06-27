package wtf.tatp.meowtils.module.advanced;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderPlayerEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.util.TeamUtil;

public class GhostHand extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean interactTeammates = true;
    @Config
    public boolean interactEnemies = false;
    @Config
    public boolean interactArmorstands = false;
    @Config
    public boolean useItemWhitelist = true;
    @Config
    public boolean swords = false;
    @Config
    public boolean tools = true;
    @Config
    public boolean hand = true;
    @Config
    public boolean blocks = false;
    @Config
    public boolean buckets = false;
    @Config
    public boolean flintAndSteel = false;
    @Config
    public boolean useBlockWhitelist = false;
    @Config
    public boolean beds = true;
    @Config
    public boolean obsidian = true;
    @Config
    public boolean defenseBlocks = true;
    @Config
    public boolean chests = true;
    @Config
    public boolean bedwarsOnly = false;
    @Config
    public String transparent = "None";

    public GhostHand() {
        super("GhostHand", Module.Category.Advanced);
        tag(Module.ModuleTag.BLATANT);
        tooltip("Allows you to interact through entities.");
        addMode(new ModeValue("Transparent", Arrays.asList("Always", "None"), "transparent", this));
        addToggle(new ToggleValue("Use item whitelist", "useItemWhitelist", this));
        addToggle(new ToggleValue("Use block whitelist", "useBlockWhitelist", this));
        addToggle(new ToggleValue("Bedwars only", "bedwarsOnly", this));
        addCheck(new CheckValue("Through teammates", "interactTeammates", this));
        addCheck(new CheckValue("Through enemies", "interactEnemies", this));
        addCheck(new CheckValue("Through armorstands", "interactArmorstands", this));
        addExpand(new ExpandValue("Item whitelist", e -> {
            e.addCheck(new CheckValue("§bSwords", "swords", this));
            e.addCheck(new CheckValue("§6Tools", "tools", this));
            e.addCheck(new CheckValue("§eHand", "hand", this));
            e.addCheck(new CheckValue("Blocks", "blocks", this));
            e.addCheck(new CheckValue("§9Buckets", "buckets", this));
            e.addCheck(new CheckValue("§7Flint and steel", "flintAndSteel", this));
        }));
        addExpand(new ExpandValue("Block whitelist", e -> {
            e.addCheck(new CheckValue("§cBeds", "beds", this));
            e.addCheck(new CheckValue("§8Obsidian", "obsidian", this));
            e.addCheck(new CheckValue("Defense blocks", "defenseBlocks", this));
            e.addCheck(new CheckValue("§6Chests", "chests", this));
        }));
    }

    @EventTarget
    public void onRenderPlayer(RenderPlayerEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        AbstractClientPlayer abstractClientPlayer = event.getPlayer();
        if (this.transparent.equals("None")) return;
        if (abstractClientPlayer.isInvisible()) return;
        if (abstractClientPlayer == this.mc.thePlayer) return;
        if (!isWhitelistedEntity(abstractClientPlayer)) return;

        if (event.getStage() == RenderPlayerEvent.Stage.PRE) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        }

        if (event.getStage() == RenderPlayerEvent.Stage.POST) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public boolean shouldActivate(Entity entity) {
        if (this.bedwarsOnly && Bedwars.GAME.isNotActive()) return false;
        return (isWhitelistedEntity(entity) && isWhitelistedItem() && isWhitelistedBlock());
    }

    private static boolean isWhitelistedEntity(Entity entity) {
        GhostHand g = Module.get(GhostHand.class);
        if (g == null) return false;
        if (g.interactTeammates && entity instanceof EntityPlayer && TeamUtil.isTeam((EntityPlayer) entity) && !TeamUtil.isBot((EntityPlayer) entity)) return true;
        if (g.interactEnemies && entity instanceof EntityPlayer && !TeamUtil.isBot((EntityPlayer) entity)) return true;
        if (g.interactArmorstands && entity instanceof net.minecraft.entity.item.EntityArmorStand) return true;
        return false;
    }

    private static boolean isWhitelistedItem() {
        Minecraft mc = Minecraft.getMinecraft();
        GhostHand g = Module.get(GhostHand.class);
        if (g == null) return false;
        if (!g.useItemWhitelist) return true;
        ItemStack itemStack = mc.thePlayer.getHeldItem();
        if (itemStack == null) return g.hand;
        Item item = mc.thePlayer.getHeldItem().getItem();

        if (item instanceof net.minecraft.item.ItemSword && g.swords) return true;
        if ((item instanceof net.minecraft.item.ItemTool || item instanceof net.minecraft.item.ItemShears) && g.tools) return true;
        if (item instanceof net.minecraft.item.ItemBlock && g.blocks) return true;
        if ((item == Items.water_bucket || item == Items.lava_bucket || item == Items.bucket) && g.buckets) return true;
        if (item instanceof net.minecraft.item.ItemFlintAndSteel && g.flintAndSteel) return true;
        return false;
    }

    private static boolean isWhitelistedBlock() {
        Minecraft mc = Minecraft.getMinecraft();
        GhostHand g = Module.get(GhostHand.class);
        if (g == null) return false;
        if (!g.useBlockWhitelist) return true;
        if (mc.objectMouseOver == null) return false;

        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

            if (block instanceof net.minecraft.block.BlockBed && g.beds) return true;
            if (block instanceof net.minecraft.block.BlockObsidian && g.obsidian) return true;
            if ((block instanceof net.minecraft.block.BlockColored || block instanceof net.minecraft.block.BlockLog || block instanceof net.minecraft.block.BlockPlanks || block instanceof net.minecraft.block.BlockStone || block instanceof net.minecraft.block.BlockPackedIce) && g.defenseBlocks) {
                return true;
            }
            if ((block instanceof net.minecraft.block.BlockChest || block instanceof net.minecraft.block.BlockEnderChest) && g.chests) return true;
        }
        return false;
    }
}