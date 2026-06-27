package wtf.tatp.meowtils.module.bedwars;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.util.ColorUtil;

public class UpgradeHUD extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public boolean shortNames = false;
    @Config
    public float scale = 0.65F;
    @Config
    public boolean showSharpness = true;
    @Config
    public boolean showProtection = true;
    @Config
    public boolean showTraps = true;
    @Config
    public boolean showFeatherFalling = true;
    @Config
    public boolean showHealPool = true;
    @Config
    public boolean showForge = true;

    private static final Queue<String> TRAP_QUEUE = new ArrayDeque<>();
    private static final String FALSE_ICON = EnumChatFormatting.RED + "✗";
    private static int sharpnessLevel = 0;
    private static int sharpnessLevelCached = 0;
    private static int protectionLevel = 0;
    private static int protectionLevelCached = 0;
    private static String trapName = "";
    private static String trapNameCached = "";
    private static int featherFallingLevel = 0;
    private static int featherFallingLevelCached = 0;
    private static boolean healPoolEnabled = false;
    private static boolean healPoolEnabledCached = false;
    private static String forgeLevel = "";
    private static String forgeLevelCached = "";

    public UpgradeHUD() {
        super("UpgradeHUD", Module.Category.Bedwars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays team upgrades on your screen. Only works with English language selected on Hypixel.");
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addToggle(new ToggleValue("Short names", "shortNames", this));
        addCheck(new CheckValue("Show §bSharpness", "showSharpness", this));
        addCheck(new CheckValue("Show §3Protection", "showProtection", this));
        addCheck(new CheckValue("Show §9Traps", "showTraps", this));
        addCheck(new CheckValue("Show §aFeather Falling", "showFeatherFalling", this));
        addCheck(new CheckValue("Show §dHeal Pool", "showHealPool", this));
        addCheck(new CheckValue("Show §7Forge", "showForge", this));
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        boolean inGame = ((Bedwars.GAME.isActive() && Bedwars.PRE_GAME.isNotActive()) || Duels.BEDWARS.isActive());
        if (!GuiUtil.inEditor() && (!inGame || this.mc.currentScreen != null)) return;

        int x = this.posX;
        int y = this.posY;

        List<String> lines = getDisplayString().stream()
                .sorted(Comparator.comparingInt(s -> this.mc.fontRendererObj.getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes((String) s))).reversed())
                .collect(Collectors.toList());

        for (String line : lines) {
            Meowtils.drawString(line, x, y, this.scale, ColorUtil.rgb(this.red, this.green, this.blue));
            y += Meowtils.offsetString(this.scale);
        }
    }

    private List<String> getDisplayString() {
        List<String> lines = new ArrayList<>();

        if (this.showSharpness) lines.add(formatUpgradeName("Sharpness: ") + ((sharpnessLevel > 0) ? (EnumChatFormatting.GREEN + String.valueOf(sharpnessLevel)) : FALSE_ICON));
        if (this.showProtection) lines.add(formatUpgradeName("Protection: ") + ((protectionLevel > 0) ? (EnumChatFormatting.GREEN + String.valueOf(protectionLevel)) : FALSE_ICON));
        if (this.showTraps) lines.add(formatUpgradeName("Trap: ") + (trapName.isEmpty() ? FALSE_ICON : (EnumChatFormatting.GREEN + trapName)));
        if (this.showFeatherFalling) lines.add(formatUpgradeName("Feather Falling: ") + ((featherFallingLevel > 0) ? (EnumChatFormatting.GREEN + String.valueOf(featherFallingLevel)) : FALSE_ICON));
        if (this.showHealPool) lines.add(formatUpgradeName("Heal Pool: ") + (healPoolEnabled ? (EnumChatFormatting.GREEN + "✓") : FALSE_ICON));
        if (this.showForge) lines.add(formatUpgradeName("Forge: ") + (forgeLevel.isEmpty() ? FALSE_ICON : getForgeLevel(forgeLevel)));

        return lines;
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;
        String msg = event.getComponent().getUnformattedText();

        if (msg.equals("You will respawn because you still have a bed!")) {
            sharpnessLevel = sharpnessLevelCached;
            protectionLevel = protectionLevelCached;
            trapName = trapNameCached;
            featherFallingLevel = featherFallingLevelCached;
            healPoolEnabled = healPoolEnabledCached;
            forgeLevel = forgeLevelCached;
        }

        if (msg.contains("purchased") && !msg.contains(":")) {
            if (msg.contains("Sharpened Swords")) {
                if (msg.contains("II")) {
                    sharpnessLevel = 2;
                    sharpnessLevelCached = 2;
                } else {
                    sharpnessLevel = 1;
                    sharpnessLevelCached = 1;
                }
            }

            if (msg.contains("Reinforced Armor")) {
                if (msg.contains("IV")) {
                    protectionLevel = 4;
                    protectionLevelCached = 4;
                } else if (msg.contains("III")) {
                    protectionLevel = 3;
                    protectionLevelCached = 3;
                } else if (msg.contains("II")) {
                    protectionLevel = 2;
                    protectionLevelCached = 2;
                } else if (msg.contains("I")) {
                    protectionLevel = 1;
                    protectionLevelCached = 1;
                }
            }

            if (msg.contains("Trap")) {
                if (msg.contains("Miner Fatigue")) {
                    addTrap("Miner Fatigue");
                } else if (msg.contains("Blindness")) {
                    addTrap("Blindness");
                } else if (msg.contains("Reveal")) {
                    addTrap("Reveal");
                } else if (msg.contains("Counter-Offensive")) {
                    addTrap("Counter-Offensive");
                }
            }

            if (msg.contains("Cushioned Boots")) {
                if (msg.contains("II")) {
                    featherFallingLevel = 2;
                    featherFallingLevelCached = 2;
                } else if (msg.contains("I")) {
                    featherFallingLevel = 1;
                    featherFallingLevelCached = 1;
                }
            }

            if (msg.contains("Heal Pool")) {
                healPoolEnabled = true;
                healPoolEnabledCached = true;
            }

            if (msg.contains("Forge")) {
                if (msg.contains("Iron")) {
                    forgeLevel = "Iron";
                    forgeLevelCached = "Iron";
                } else if (msg.contains("Golden")) {
                    forgeLevel = "Golden";
                    forgeLevelCached = "Golden";
                } else if (msg.contains("Emerald")) {
                    forgeLevel = "Emerald";
                    forgeLevelCached = "Emerald";
                } else if (msg.contains("Molten")) {
                    forgeLevel = "Molten";
                    forgeLevelCached = "Molten";
                }
            }
        }

        if (msg.contains("Trap was set off!") || msg.contains("Your Bed was destroyed")) {
            trapName = TRAP_QUEUE.poll();
            trapNameCached = trapName;

            if (trapName == null) {
                trapName = "";
                trapNameCached = "";
            }
        }
    }

    private static String getForgeLevel(String forgeLevel) {
        if (forgeLevel.equals("Iron")) return EnumChatFormatting.GRAY + forgeLevel;
        if (forgeLevel.equals("Golden")) return EnumChatFormatting.GOLD + forgeLevel;
        if (forgeLevel.equals("Emerald")) return EnumChatFormatting.DARK_GREEN + forgeLevel;
        if (forgeLevel.equals("Molten")) return EnumChatFormatting.DARK_RED + forgeLevel;
        return forgeLevel;
    }

    private static void addTrap(String trap) {
        if (trapName.isEmpty()) {
            trapName = trap;
            trapNameCached = trap;
            return;
        }
        TRAP_QUEUE.offer(trap);
    }

    private String formatUpgradeName(String upgradeName) {
        if (this.shortNames) {
            if (upgradeName.equals("Sharpness: ")) return "Sharp: ";
            if (upgradeName.equals("Protection: ")) return "Prot: ";
            if (upgradeName.equals("Feather Falling: ")) return "Feather: ";
            if (upgradeName.equals("Heal Pool: ")) return "Heal: ";
        }
        return upgradeName;
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds(this.shortNames ? "Feather: X" : "Feather Falling: X", 6, this.scale)));
    }

    @Override
    public void onReset() {
        sharpnessLevel = 0;
        protectionLevel = 0;
        trapName = "";
        featherFallingLevel = 0;
        healPoolEnabled = false;
        forgeLevel = "";
        TRAP_QUEUE.clear();
    }
}