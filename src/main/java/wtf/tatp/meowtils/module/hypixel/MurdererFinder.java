package wtf.tatp.meowtils.module.hypixel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.MurderMystery;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Render;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class MurdererFinder extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean findMurderers = true;
    @Config
    public boolean findDetectives = true;
    @Config
    public boolean chatAlerts = true;
    @Config
    public boolean sound = true;
    @Config
    public String mode = "3D";
    @Config
    public String render = "Full";
    @Config
    public boolean nameIcons = true;

    private static final Color MURDERER_COLOR = new Color(ColorUtil.DARK_RED.getRed(), ColorUtil.DARK_RED.getGreen(), ColorUtil.DARK_RED.getBlue(), 80);
    private static final Color DETECTIVE_COLOR = new Color(ColorUtil.GOLD.getRed(), ColorUtil.GOLD.getGreen(), ColorUtil.GOLD.getBlue(), 80);
    private static final ArrayList<UUID> MURDERERS = new ArrayList<>();
    private static final ArrayList<UUID> DETECTIVES = new ArrayList<>();

    private static final List<Integer> MURDERER_ITEMS = Arrays.asList(
            267, 54, 130, 272, 280, 271, 268, 32, 338, 273, 369, 277, 406, 400, 285, 334, 421, 263, 318, 352, 391, 396, 357, 279, 175, 409, 364, 405, 366, 2258, 294, 351, 283, 276, 293, 359, 349, 351, 297, 333, 382, 340, 6, 286, 278, 284
    );

    public MurdererFinder() {
        super("MurdererFinder", Module.Category.Hypixel);
        tag(Module.ModuleTag.SAFE);
        tooltip("Highlights the murderer and alerts for other relevant roles in Murder Mystery.");
        addMode(new ModeValue("Mode", Arrays.asList("3D", "2D"), "mode", this));
        addMode(new ModeValue("Render", Arrays.asList("Full", "Outline", "None"), "render", this));
        addToggle(new ToggleValue("Name icons", "nameIcons", this));
        addToggle(new ToggleValue("Chat alerts", "chatAlerts", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
        addCheck(new CheckValue("Find murderers", "findMurderers", this));
        addCheck(new CheckValue("Find detectives", "findDetectives", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Server.HYPIXEL.isNotActive()) return;
        if (MurderMystery.ALL.isNotActive()) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == null || player == this.mc.thePlayer || TeamUtil.isBot(player)) continue;

            ItemStack held = player.getHeldItem();
            UUID uuid = player.getUniqueID();
            int distanceToEntity = (int) player.getDistanceToEntity(this.mc.thePlayer);
            String distance = EnumChatFormatting.GRAY + " (" + EnumChatFormatting.AQUA + distanceToEntity + "m" + EnumChatFormatting.GRAY + ")";

            if (this.findMurderers) {
                if (held == null || MURDERERS.contains(uuid) || DETECTIVES.contains(uuid)) continue;
                if (MURDERER_ITEMS.contains(Item.getIdFromItem(held.getItem()))) {
                    MURDERERS.add(uuid);
                    alert(EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "Murderer " + EnumChatFormatting.GRAY + "found: " + EnumChatFormatting.LIGHT_PURPLE + player.getName() + distance);
                }
            }

            if (!this.findDetectives || held == null || MURDERERS.contains(uuid) || DETECTIVES.contains(uuid)) continue;
            if (held.getItem() == Items.bow) {
                DETECTIVES.add(uuid);
                alert(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Detective " + EnumChatFormatting.GRAY + "found: " + EnumChatFormatting.LIGHT_PURPLE + player.getName() + distance);
            }
        }
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (Server.HYPIXEL.isNotActive()) return;
        if (MurderMystery.ALL.isNotActive()) return;
        if (this.render.equals("None")) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == null || player == this.mc.thePlayer || TeamUtil.isBot(player)) continue;

            boolean renderMode = this.render.equals("Full");
            boolean boxMode = this.mode.equals("3D");
            UUID uuid = player.getUniqueID();

            if (MURDERERS.contains(uuid)) {
                Render.drawEntityBox(player, boxMode, renderMode, MURDERER_COLOR, !renderMode, MURDERER_COLOR, 0.1D, 0.1D, 0.1D);
                continue;
            }

            if (DETECTIVES.contains(uuid)) {
                Render.drawEntityBox(player, boxMode, renderMode, DETECTIVE_COLOR, !renderMode, DETECTIVE_COLOR, 0.1D, 0.1D, 0.1D);
            }
        }
    }

    private void alert(String msg) {
        if (this.chatAlerts) {
            Meowtils.addMessage(msg);
        }

        if (this.sound) {
            Util.playSound(Util.Sound.PING_MEDIUM, 100);
        }
    }

    public static boolean isMurderer(UUID uuid) {
        return MURDERERS.contains(uuid);
    }

    public static boolean isDetective(UUID uuid) {
        return DETECTIVES.contains(uuid);
    }

    @Override
    public void onReset() {
        MURDERERS.clear();
        DETECTIVES.clear();
    }
}