package wtf.tatp.meowtils.module.bedwars;

import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.Util;

public class TrapNotifier extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean sound = true;
    @Config
    public boolean triggerAlert = true;
    @Config
    public boolean missingReminder = true;

    private static int tickCounter = 0;
    private static boolean triggerNotified = false;
    private static String trap = "";
    private static boolean revealTrap = false;
    private static boolean reminderNotified = false;
    private static int reminderSeconds = -1;

    public TrapNotifier() {
        super("TrapNotifier", Module.Category.Bedwars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Reminds when you're missing a trap and notify when you trigger one.");
        addToggle(new ToggleValue("Triggered trap alert", "triggerAlert", this));
        addToggle(new ToggleValue("Missing trap reminder", "missingReminder", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;
        String msg = event.getComponent().getUnformattedText();

        if (this.triggerAlert && msg.equals("Your invisibility was removed by an Reveal Trap!")) {
            revealTrap = true;
        }

        if (this.missingReminder) {
            if (msg.contains("Trap was set off!")) {
                reminderSeconds = 30;
                reminderNotified = false;
            }

            if (msg.contains("Your Bed was destroyed")) {
                reminderSeconds = 0;
                reminderNotified = true;
            }

            if (msg.toLowerCase().contains("purchased") && msg.toLowerCase().contains("trap") && !msg.contains(":")) {
                reminderSeconds = 0;
                reminderNotified = true;
            }
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;

        tickCounter++;
        if (tickCounter != 10) return;
        tickCounter = 0;

        boolean currentTrap = (this.mc.thePlayer.isPotionActive(Potion.moveSlowdown) || this.mc.thePlayer.isPotionActive(Potion.blindness) || revealTrap);

        if (this.triggerAlert && !triggerNotified) {
            if (this.mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
                trap = "Miner Fatigue";
                alert();
            } else if (this.mc.thePlayer.isPotionActive(Potion.blindness)) {
                trap = "Blindness";
                alert();
            } else if (revealTrap) {
                trap = "Reveal";
                revealTrap = false;
                alert();
            }
        }

        if (!currentTrap) {
            triggerNotified = false;
        }

        if (this.missingReminder && reminderSeconds > 0) {
            reminderSeconds--;
        }

        if (this.missingReminder && !reminderNotified && reminderSeconds == 0) {
            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                Meowtils.addMessage(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "You currently don't have a trap active!");
            }

            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show("TrapNotifier", "No trap active!", NotificationManager.Type.WARNING, 2000L);
            }

            if (this.sound) {
                Util.playSound(Util.Sound.ANVIL_BREAK, 100);
            }

            reminderNotified = true;
        }
    }

    private void alert() {
        if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
            Meowtils.addMessage(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "TRAP TRIGGERED! " + EnumChatFormatting.DARK_GRAY + "(" + EnumChatFormatting.GOLD + trap + EnumChatFormatting.DARK_GRAY + ")");
        }

        if (Notifications.getMode() != Notifications.Mode.CHAT) {
            NotificationManager.show("TrapNotifier", EnumChatFormatting.RED + "TRAP TRIGGERED! " + EnumChatFormatting.DARK_GRAY + "(" + EnumChatFormatting.GOLD + trap + EnumChatFormatting.DARK_GRAY + ")", NotificationManager.Type.ALERT, 2000L);
        }

        triggerNotified = true;

        if (this.sound) {
            Util.playSound(Util.Sound.ANVIL_BREAK, 100);
        }
    }

    @Override
    public void onReset() {
        triggerNotified = false;
        tickCounter = 0;
        trap = "";
        revealTrap = false;
        reminderNotified = false;
        reminderSeconds = -1;
    }
}