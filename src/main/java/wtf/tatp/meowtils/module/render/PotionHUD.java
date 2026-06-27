package wtf.tatp.meowtils.module.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.Util;

public class PotionHUD extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public String name = "Full";
    @Config
    public String display = "Both";
    @Config
    public float scale = 0.65F;
    @Config
    public int alertTime = 5;
    @Config
    public boolean expireSound = true;
    @Config
    public boolean hideInfinite = true;
    @Config
    public boolean invis = true;
    @Config
    public boolean jump = true;
    @Config
    public boolean speed = true;
    @Config
    public boolean regen = true;
    @Config
    public boolean strength = true;
    @Config
    public boolean fireRes = true;
    @Config
    public boolean haste = true;
    @Config
    public boolean fatigue = true;
    @Config
    public int posX = 1;
    @Config
    public int posY = 80;

    private static final Set<Integer> ALERTED_POTIONS = new HashSet<>();

    public PotionHUD() {
        super("PotionHUD", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Display important potion effects on screen.");
        addMode(new ModeValue("Name", Arrays.asList("Full", "Short", "None"), "name", this));
        addMode(new ModeValue("Display", Arrays.asList("Both", "HUD", "Chat"), "display", this));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addSlider(new SliderValue("Alert time", 0.0D, 10.0D, 1.0D, "s", "alertTime", this, int.class));
        addToggle(new ToggleValue("Expire sound", "expireSound", this));
        addToggle(new ToggleValue("Hide infinite effects", "hideInfinite", this));
        addExpand(new ExpandValue("Potions", e -> {
            e.addCheck(new CheckValue("§bInvisibility", "invis", this));
            e.addCheck(new CheckValue("§aJump Boost", "jump", this));
            e.addCheck(new CheckValue("§eSpeed", "speed", this));
            e.addCheck(new CheckValue("§dRegeneration", "regen", this));
            e.addCheck(new CheckValue("§4Strength", "strength", this));
            e.addCheck(new CheckValue("§6Fire Resistance", "fireRes", this));
            e.addCheck(new CheckValue("§9Haste", "haste", this));
            e.addCheck(new CheckValue("§7Mining Fatigue", "fatigue", this));
        }));
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;
        if (this.mc.currentScreen != null && !GuiUtil.inEditor()) return;

        int x = this.posX;
        int y = this.posY;

        List<String> potionStrings = new ArrayList<>();

        for (PotionEffect effect : this.mc.thePlayer.getActivePotionEffects()) {
            int id = effect.getPotionID();
            Potion potion = Potion.potionTypes[id];
            if (potion == null) continue;

            if ((id != Potion.invisibility.id || !this.invis) &&
                    (id != Potion.jump.id || !this.jump) &&
                    (id != Potion.moveSpeed.id || !this.speed) &&
                    (id != Potion.regeneration.id || !this.regen) &&
                    (id != Potion.damageBoost.id || !this.strength) &&
                    (id != Potion.fireResistance.id || !this.fireRes) &&
                    (id != Potion.digSlowdown.id || !this.fatigue) &&
                    (id != Potion.digSpeed.id || !this.haste)) {
                continue;
            }

            String timeLeft = Potion.getDurationString(effect);

            if (this.hideInfinite) {
                if (timeLeft.contains("*")) continue;
                if (effect.getDuration() > 36000) continue;
            }

            int seconds = this.alertTime * 20;

            if (effect.getDuration() <= seconds) {
                timeLeft = !this.name.equals("None") ? (EnumChatFormatting.RED + timeLeft) : timeLeft;

                if (!ALERTED_POTIONS.contains(id) && (this.display.equals("Chat") || this.display.equals("Both"))) {
                    String messageSuffix = (seconds <= 0) ? " has expired!" : " is about to expire!";
                    Meowtils.addMessage(getPotionName(id, "Full").replace(":", "") + EnumChatFormatting.RED + messageSuffix);
                    ALERTED_POTIONS.add(id);

                    if (this.expireSound) {
                        Util.playSound(Util.Sound.PING_MEDIUM, 100);
                    }
                }
            } else {
                ALERTED_POTIONS.remove(id);
            }

            potionStrings.add(getPotionName(id, this.name) + " " + (this.name.equals("None") ? "" : EnumChatFormatting.GRAY) + timeLeft);
        }

        potionStrings.sort(Comparator.comparingInt(s -> this.mc.fontRendererObj.getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes((String) s))).reversed());

        if (potionStrings.isEmpty() || this.display.equals("Chat")) return;
        for (String potion : potionStrings) {
            Meowtils.drawString(potion, x, y, this.scale, -1);
            y += Meowtils.offsetString(this.scale);
        }
    }

    private String getPotionName(int id, String mode) {
        String fullName;
        String shortName;
        EnumChatFormatting color;

        if (id == Potion.invisibility.id) {
            fullName = "Invisibility";
            shortName = "Inv";
            color = EnumChatFormatting.AQUA;
        } else if (id == Potion.jump.id) {
            fullName = "Jump Boost";
            shortName = "Jmp";
            color = EnumChatFormatting.GREEN;
        } else if (id == Potion.moveSpeed.id) {
            fullName = "Speed";
            shortName = "Spd";
            color = EnumChatFormatting.YELLOW;
        } else if (id == Potion.regeneration.id) {
            fullName = "Regeneration";
            shortName = "Reg";
            color = EnumChatFormatting.LIGHT_PURPLE;
        } else if (id == Potion.damageBoost.id) {
            fullName = "Strength";
            shortName = "Str";
            color = EnumChatFormatting.DARK_RED;
        } else if (id == Potion.fireResistance.id) {
            fullName = "Fire Resistance";
            shortName = "Fire";
            color = EnumChatFormatting.GOLD;
        } else if (id == Potion.digSlowdown.id) {
            fullName = "Mining Fatigue";
            shortName = "Mine";
            color = EnumChatFormatting.GRAY;
        } else if (id == Potion.digSpeed.id) {
            fullName = "Haste";
            shortName = "Hst";
            color = EnumChatFormatting.BLUE;
        } else {
            return "Unknown";
        }

        switch (mode) {
            case "Full":
                return color + fullName + ":";
            case "Short":
                return color + shortName + ":";
            case "None":
                return color + "";
        }
        return "Unknown";
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("Fire Resistance: 10_00", 8, this.scale)));
    }
}