package wtf.tatp.meowtils.module.render;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class HealthDisplay extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public boolean showOwn = true;
    @Config
    public int key = 0;
    @Config
    public float healthScale = 0.65F;
    @Config
    public boolean showBow = true;
    @Config
    public float bowScale = 1.0F;
    @Config
    public boolean hideSuffix = false;
    @Config
    public int healthPosX = 310;
    @Config
    public int healthPosY = 190;
    @Config
    public int bowPosX = 310;
    @Config
    public int bowPosY = 50;

    private static float indicatorHealth = 0.0F;
    private static long lastIndicator = 0L;

    public HealthDisplay() {
        super("HealthDisplay", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays various health information.");
        addExpand(new ExpandValue("Show own health", e -> {
            e.addToggle(new ToggleValue("Enabled", "showOwn", this));
            e.addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "healthScale", this, float.class));
        }));
        addExpand(new ExpandValue("Show bow damage", e -> {
            e.addToggle(new ToggleValue("Enabled", "showBow", this));
            e.addSlider(new SliderValue("Scale", 0.5D, 5.0D, 0.05D, null, "bowScale", this, float.class));
            e.addCheck(new CheckValue("Hide suffix", "hideSuffix", this));
        }));
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;
        if (!GuiUtil.inEditor() && this.mc.currentScreen != null) {
            return;
        }

        if (this.showOwn) {
            int hearts = (int) this.mc.thePlayer.getHealth();
            int absorptionHearts = (int) this.mc.thePlayer.getAbsorptionAmount();
            boolean isAbsorptionActive = this.mc.thePlayer.isPotionActive(Potion.absorption);
            int health = isAbsorptionActive ? (hearts + absorptionHearts) : hearts;

            String heart = (health < 4) ? "❣" : "❤";
            EnumChatFormatting healthColor = (health < 3) ? EnumChatFormatting.DARK_RED :
                    ((health < 6) ? EnumChatFormatting.RED :
                     ((health < 10) ? EnumChatFormatting.YELLOW :
                      ((health < 15) ? EnumChatFormatting.GREEN : EnumChatFormatting.DARK_GREEN)));

            EnumChatFormatting heartColor = isAbsorptionActive ? EnumChatFormatting.GOLD : EnumChatFormatting.RED;

            Meowtils.drawString(healthColor + String.valueOf(health) + heartColor + heart, this.healthPosX, this.healthPosY, this.healthScale, -1);
        }

        if (this.showBow && (System.currentTimeMillis() - lastIndicator <= 2000L || GuiUtil.inEditor())) {
            EnumChatFormatting healthColor = (indicatorHealth < 3.0F) ? EnumChatFormatting.DARK_RED :
                    ((indicatorHealth < 6.0F) ? EnumChatFormatting.RED :
                     ((indicatorHealth < 10.0F) ? EnumChatFormatting.YELLOW :
                      ((indicatorHealth < 15.0F) ? EnumChatFormatting.GREEN : EnumChatFormatting.DARK_GREEN)));

            String suffix = this.hideSuffix ? "" : " HP";

            Meowtils.drawString(healthColor + String.valueOf(indicatorHealth) + suffix, this.bowPosX, this.bowPosY, this.bowScale, -1);
        }
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        if (!this.showBow) return;
        String msg = event.getComponent().getUnformattedText();

        Pattern pattern = Pattern.compile("is on ([0-9]+(?:\\.[0-9]+)?) HP!");
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find() && !msg.contains(":")) {
            indicatorHealth = Float.parseFloat(matcher.group(1));
            lastIndicator = System.currentTimeMillis();
        }
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Arrays.asList(
                new HudEntry("Health", this, "healthPosX", "healthPosY", () -> GuiUtil.getHudBounds("20V", 1, this.healthScale)),
                new HudEntry("Bow Damage", this, "bowPosX", "bowPosY", () -> GuiUtil.getHudBounds(this.hideSuffix ? "20 " : "20 HP ", 1, this.bowScale))
        );
    }
}