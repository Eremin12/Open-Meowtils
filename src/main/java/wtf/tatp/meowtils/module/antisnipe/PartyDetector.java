package wtf.tatp.meowtils.module.antisnipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.EntityJoinWorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.DelayedTask;
import wtf.tatp.meowtils.util.Util;

public class PartyDetector extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean sound = true;
    @Config
    public boolean twos = true;
    @Config
    public boolean threes = true;
    @Config
    public boolean foursNormal = true;
    @Config
    public boolean foursTwo = true;
    @Config
    public boolean showMissed = true;

    private static int playerCounter = 0;
    private static long lastJoinTime = 0L;
    private static boolean countingPlayers = false;
    private static int missedCounter = 0;
    private static boolean alertedMissed = false;
    private static int tickCounter = 0;
    private static boolean gameStarted = false;

    public PartyDetector() {
        super("PartyDetector", Module.Category.Antisnipe);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Detects when a party joins your lobby.");
        addToggle(new ToggleValue("Ping sound", "sound", this));
        addToggle(new ToggleValue("Show missed players", "showMissed", this));
        addCheck(new CheckValue("Bedwars 2s", "twos", this));
        addCheck(new CheckValue("Bedwars 3s", "threes", this));
        addCheck(new CheckValue("Bedwars 4s", "foursNormal", this));
        addCheck(new CheckValue("Bedwars 4v4", "foursTwo", this));
    }

    @EventTarget
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity() == this.mc.thePlayer) return;
        if (Bedwars.PRE_GAME.isNotActive()) return;
        if (getBedwarsMode() == 0) return;

        long now = System.currentTimeMillis();

        if (!countingPlayers) {
            lastJoinTime = now;
        }

        if (now - lastJoinTime <= 1000L) {
            playerCounter++;
            countingPlayers = true;
        } else {
            countingPlayers = false;
            lastJoinTime = 0L;
            playerCounter = 0;
        }

        if (playerCounter != 0 && playerCounter >= getBedwarsMode()) {
            new DelayedTask(() -> {
                if (!gameStarted) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Warning: " + EnumChatFormatting.YELLOW + getBedwarsMode() + EnumChatFormatting.WHITE + " players joined! " + EnumChatFormatting.DARK_GRAY + "(" + EnumChatFormatting.BLUE + "Party" + EnumChatFormatting.DARK_GRAY + ")");
                    if (this.sound) Util.playSound(Util.Sound.PING_DEEP, 100);
                    PartyNotifier.partyDetector(getBedwarsMode());
                }
            }, 1);

            playerCounter = 0;
            lastJoinTime = 0L;
            countingPlayers = false;
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Bedwars.PRE_GAME.isNotActive()) return;
        if (!this.showMissed) return;
        if (alertedMissed) return;

        tickCounter++;
        if (tickCounter < 10) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player != null && player != this.mc.thePlayer && player.getUniqueID().version() == 2) {
                missedCounter++;
            }
        }

        if (missedCounter != 0) {
            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                Meowtils.addMessage("Missed players: " + EnumChatFormatting.YELLOW + missedCounter);
            }

            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show("PartyDetector", "Missed: " + EnumChatFormatting.YELLOW + missedCounter, NotificationManager.Type.INFO, 1500L);
            }
        }

        alertedMissed = true;
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();

        if (msg.contains("The game starts in 1 second!")) {
            gameStarted = true;
        }
    }

    private int getBedwarsMode() {
        if (Bedwars.FOUR_FOUR.isActive() && this.foursTwo) return 4;
        if (Bedwars.FOURS.isActive() && this.foursNormal) return 4;
        if (Bedwars.THREES.isActive() && this.threes) return 3;
        if (Bedwars.DOUBLES.isActive() && this.twos) return 2;
        return 0;
    }

    @Override
    public void onReset() {
        playerCounter = 0;
        lastJoinTime = 0L;
        countingPlayers = false;
        alertedMissed = false;
        missedCounter = 0;
        tickCounter = 0;
        gameStarted = false;
    }
}