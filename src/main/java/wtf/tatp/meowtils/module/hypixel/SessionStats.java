package wtf.tatp.meowtils.module.hypixel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.DelayedTask;
import wtf.tatp.meowtils.util.HypixelUtil;
import wtf.tatp.meowtils.util.Util;

public class SessionStats extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public String mode = "Horizontal";
    @Config
    public float scale = 0.65F;
    @Config
    public boolean reset = true;
    @Config
    public boolean recap = true;
    @Config
    public boolean bedwars = true;
    @Config
    public boolean bedwarsSessionKills = true;
    @Config
    public boolean bedwarsSessionFinals = true;
    @Config
    public boolean bedwarsSessionExp = true;
    @Config
    public boolean bedwarsSessionFkdr = true;
    @Config
    public boolean skywars = true;
    @Config
    public boolean skywarsSessionKills = true;
    @Config
    public boolean skywarsSessionExp = true;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;

    private static final String SEPARATOR = EnumChatFormatting.GRAY + " | " + EnumChatFormatting.RESET;
    private static boolean activated = false;
    private static int bedwarsKills = 0;
    private static int bedwarsFinalKills = 0;
    private static int bedwarsFinalDeaths = 0;
    private static float bedwarsFkdr = 0.0F;
    private static int bedwarsExp = 0;
    private static int bedwarsKillsTemp = 0;
    private static int bedwarsFinalKillsTemp = 0;
    private static int bedwarsExpTemp = 0;
    private static int skywarsKills = 0;
    private static int skywarsExp = 0;
    private static int skywarsKillsTemp = 0;
    private static int skywarsExpTemp = 0;

    public SessionStats() {
        super("SessionStats", Module.Category.Hypixel);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Tracks your own stats for this game session.");
        ColorLink colorLink = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Color", colorLink));
        addSaturation(new SaturationValue(colorLink));
        addBrightness(new BrightnessValue(colorLink));
        addMode(new ModeValue("Display", Arrays.asList("Horizontal", "Vertical"), "mode", this));
        addButton(new ButtonValue("Reset", 5.0F, () -> {
            resetNormal();
            resetTemp();
            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                Meowtils.addMessage("Reset session stats!");
            }
            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show("SessionStats", "Reset stats!", NotificationManager.Type.INFO, 1500L);
            }
        }));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addToggle(new ToggleValue("Reset after game", "reset", this));
        addToggle(new ToggleValue("Game recap message", "recap", this));
        addExpand(new ExpandValue("Bedwars", e -> {
            e.addToggle(new ToggleValue("Enabled", "bedwars", this));
            e.addCheck(new CheckValue("Show: §7Kills", "bedwarsSessionKills", this));
            e.addCheck(new CheckValue("Show: §7Final kills", "bedwarsSessionFinals", this));
            e.addCheck(new CheckValue("Show: §7FKDR", "bedwarsSessionFkdr", this));
            e.addCheck(new CheckValue("Show: §7Earned exp", "bedwarsSessionExp", this));
        }));
        addExpand(new ExpandValue("Skywars", e -> {
            e.addToggle(new ToggleValue("Enabled", "skywars", this));
            e.addCheck(new CheckValue("Show: §7Kills", "skywarsSessionKills", this));
            e.addCheck(new CheckValue("Show: §7Earned exp", "skywarsSessionExp", this));
        }));
    }

    @EventTarget(priority = EventPriority.HIGHEST)
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        if (Server.HYPIXEL.isNotActive() && Server.UNIVERSAL.isNotActive()) return;

        String msg = event.getComponent().getUnformattedText();

        // Bedwars stats
        if (Bedwars.GAME.isActive()) {
            if (msg.equals("You have been eliminated!")) {
                bedwarsFinalDeaths++;
                bedwarsFkdr = (bedwarsFinalDeaths == 0) ? bedwarsFinalKills : ((float) bedwarsFinalKills / bedwarsFinalDeaths);
            }

            if (msg.contains("Bed Wars XP") && msg.contains("+") && !msg.contains("Quest")) {
                try {
                    String value = msg.replaceAll("[^0-9]", "");
                    if (!value.isEmpty()) {
                        int parsedValue = Integer.parseInt(value);
                        bedwarsExp += parsedValue;
                        bedwarsExpTemp += parsedValue;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Skywars stats
        if (Skywars.GAME.isActive() || Skywars.MINI.isActive()) {
            if (msg.startsWith("+") && msg.contains("SkyWars Experience") && msg.endsWith("Kill") && !msg.contains(":")) {
                skywarsKills++;
                skywarsKillsTemp++;
            }

            if (msg.contains("SkyWars Experience") && msg.contains("+") && !msg.contains("Quest")) {
                try {
                    String value = msg.replaceAll("[^0-9]", "");
                    if (!value.isEmpty()) {
                        int parsedValue = Integer.parseInt(value);
                        skywarsExp += parsedValue;
                        skywarsExpTemp += parsedValue;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Game recap
        if (this.recap && !activated && Arrays.stream(HypixelUtil.GAME_END_MESSAGES).anyMatch(msg::contains)) {
            if (Bedwars.GAME.isActive()) {
                new DelayedTask(() -> {
                    Meowtils.addMessage(EnumChatFormatting.LIGHT_PURPLE + "Last game: " + EnumChatFormatting.WHITE +
                            "Kills: " + EnumChatFormatting.BLUE + bedwarsKillsTemp + SEPARATOR +
                            "Finals: " + EnumChatFormatting.BLUE + bedwarsFinalKillsTemp + SEPARATOR +
                            "EXP: " + EnumChatFormatting.BLUE + bedwarsExpTemp);
                    resetTemp();
                }, 40);
            } else if (Skywars.GAME.isActive() || Skywars.MINI.isActive()) {
                new DelayedTask(() -> {
                    Meowtils.addMessage(EnumChatFormatting.LIGHT_PURPLE + "Last game: " + EnumChatFormatting.WHITE +
                            "Kills: " + EnumChatFormatting.BLUE + skywarsKillsTemp + SEPARATOR +
                            "EXP: " + EnumChatFormatting.BLUE + skywarsExpTemp);
                    resetTemp();
                }, 40);
            }
            activated = true;
        }
    }

    @EventTarget
    public void onReceivePacket(ReceivePacketEvent event) {
        if (!(event.getPacket() instanceof S47PacketPlayerListHeaderFooter)) return;
        if (Server.HYPIXEL.isNotActive() && Server.UNIVERSAL.isNotActive()) return;
        if (Bedwars.GAME.isNotActive()) return;

        S47PacketPlayerListHeaderFooter packet = (S47PacketPlayerListHeaderFooter) event.getPacket();
        if (packet.getFooter() == null) return;

        String footer = packet.getFooter().getUnformattedText();
        if (footer == null || footer.isEmpty()) return;

        int killsIndex = footer.indexOf("Kills: ");
        int finalKillsIndex = footer.indexOf("Final Kills: ");
        if (killsIndex == -1 || finalKillsIndex == -1) return;

        int parsedKills = Util.parseIntFromString(footer, killsIndex + 7);
        int parsedFinalKills = Util.parseIntFromString(footer, finalKillsIndex + 13);

        bedwarsKills = parsedKills;
        bedwarsKillsTemp = parsedKills;
        bedwarsFinalKills = parsedFinalKills;
        bedwarsFinalKillsTemp = parsedFinalKills;
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (this.mc.currentScreen != null && !GuiUtil.inEditor()) return;
        if (Server.HYPIXEL.isNotActive() && Server.UNIVERSAL.isNotActive() && !GuiUtil.inEditor()) return;

        int x = this.posX;
        int y = this.posY;
        int color = new Color(this.red, this.green, this.blue).getRGB();
        List<String> counterStrings = new ArrayList<>();

        // Bedwars display
        if ((this.bedwars && Bedwars.ALL.isActive()) || GuiUtil.inEditor()) {
            EnumChatFormatting killsColor, finalsColor, fkdrColor, expColor;

            if (this.reset) {
                killsColor = (bedwarsKills < 5) ? EnumChatFormatting.GREEN : ((bedwarsKills < 10) ? EnumChatFormatting.YELLOW : ((bedwarsKills < 15) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                finalsColor = (bedwarsFinalKills < 4) ? EnumChatFormatting.GREEN : ((bedwarsFinalKills < 6) ? EnumChatFormatting.YELLOW : ((bedwarsFinalKills < 8) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                fkdrColor = (bedwarsFkdr < 2.0F) ? EnumChatFormatting.GREEN : ((bedwarsFinalKills < 5) ? EnumChatFormatting.YELLOW : ((bedwarsFinalKills < 8) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                expColor = (bedwarsExp < 300) ? EnumChatFormatting.GREEN : ((bedwarsExp < 500) ? EnumChatFormatting.YELLOW : ((bedwarsExp < 750) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
            } else {
                killsColor = (bedwarsKills < 50) ? EnumChatFormatting.GREEN : ((bedwarsKills < 100) ? EnumChatFormatting.YELLOW : ((bedwarsKills < 200) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                finalsColor = (bedwarsFinalKills < 30) ? EnumChatFormatting.GREEN : ((bedwarsFinalKills < 60) ? EnumChatFormatting.YELLOW : ((bedwarsFinalKills < 100) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                fkdrColor = (bedwarsFinalKills < 20) ? EnumChatFormatting.GREEN : ((bedwarsFinalKills < 40) ? EnumChatFormatting.YELLOW : ((bedwarsFinalKills < 60) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                expColor = (bedwarsExp < 2000) ? EnumChatFormatting.GREEN : ((bedwarsExp < 4000) ? EnumChatFormatting.YELLOW : ((bedwarsExp < 6000) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
            }

            if (this.mode.equals("Vertical")) {
                if (this.bedwarsSessionKills) counterStrings.add("Kills: " + killsColor + bedwarsKills);
                if (this.bedwarsSessionFinals) counterStrings.add("Finals: " + finalsColor + bedwarsFinalKills);
                if (this.bedwarsSessionFkdr) counterStrings.add("FKDR: " + fkdrColor + String.format("%.1f", bedwarsFkdr).replace(",", "."));
                if (this.bedwarsSessionExp) counterStrings.add("EXP: " + expColor + bedwarsExp);

                for (String text : counterStrings) {
                    Meowtils.drawString(text, x, y, this.scale, color);
                    y += Meowtils.offsetString(this.scale);
                }
            } else {
                if (this.bedwarsSessionKills) counterStrings.add("Kills: " + killsColor + bedwarsKills);
                if (this.bedwarsSessionFinals) counterStrings.add("Finals: " + finalsColor + bedwarsFinalKills);
                if (this.bedwarsSessionFkdr) counterStrings.add("FKDR: " + fkdrColor + String.format("%.1f", bedwarsFkdr).replace(",", "."));
                if (this.bedwarsSessionExp) counterStrings.add("EXP: " + expColor + bedwarsExp);

                String text = String.join(SEPARATOR, counterStrings);
                Meowtils.drawString(text, x, y, this.scale, color);
            }
        }

        // Skywars display
        if (this.skywars && Skywars.ALL.isActive() && !GuiUtil.inEditor()) {
            EnumChatFormatting killsColor, expColor;

            if (this.reset) {
                killsColor = (skywarsKills < 2) ? EnumChatFormatting.GREEN : ((skywarsKills < 5) ? EnumChatFormatting.YELLOW : ((skywarsKills < 10) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                expColor = (skywarsExp < 3) ? EnumChatFormatting.GREEN : ((skywarsExp < 7) ? EnumChatFormatting.YELLOW : ((skywarsExp < 15) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
            } else {
                killsColor = (skywarsKills < 20) ? EnumChatFormatting.GREEN : ((skywarsKills < 50) ? EnumChatFormatting.YELLOW : ((skywarsKills < 100) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
                expColor = (skywarsExp < 50) ? EnumChatFormatting.GREEN : ((skywarsExp < 100) ? EnumChatFormatting.YELLOW : ((skywarsExp < 200) ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE));
            }

            if (this.mode.equals("Vertical")) {
                if (this.skywarsSessionKills) counterStrings.add("Kills: " + killsColor + skywarsKills);
                if (this.skywarsSessionExp) counterStrings.add("EXP: " + expColor + skywarsExp);

                for (String text : counterStrings) {
                    Meowtils.drawString(text, x, y, this.scale, color);
                    y += Meowtils.offsetString(this.scale);
                }
            } else {
                String killString = this.skywarsSessionKills ? ("Kills: " + killsColor + skywarsKills + (this.skywarsSessionExp ? SEPARATOR : "") + EnumChatFormatting.RESET) : "";
                String expString = this.skywarsSessionExp ? ("EXP: " + expColor + skywarsExp + EnumChatFormatting.RESET) : "";
                String text = killString + expString;

                Meowtils.drawString(text, x, y, this.scale, color);
            }
        }
    }

    private static void resetNormal() {
        bedwarsKills = 0;
        bedwarsFinalKills = 0;
        bedwarsExp = 0;
        skywarsKills = 0;
        skywarsExp = 0;
        bedwarsFinalDeaths = 0;
        bedwarsFkdr = 0.0F;
    }

    private static void resetTemp() {
        bedwarsKillsTemp = 0;
        bedwarsFinalKillsTemp = 0;
        bedwarsExpTemp = 0;
        skywarsKillsTemp = 0;
        skywarsExpTemp = 0;
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("Kills: 0 Finals: 0 FKDR: 0 EXP: 9999", 1, this.scale)));
    }

    @Override
    public void onReset() {
        if (this.reset) {
            resetNormal();
        }
        resetTemp();
        activated = false;
    }
}