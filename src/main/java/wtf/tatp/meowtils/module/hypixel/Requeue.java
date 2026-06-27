package wtf.tatp.meowtils.module.hypixel;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.EntityJoinWorldEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.DelayedTask;
import wtf.tatp.meowtils.util.HypixelUtil;

public class Requeue extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public String lastPlayCommand = "";
    @Config
    public boolean feedback = true;
    @Config
    public boolean autoRequeue = false;
    @Config
    public int autoRequeueDelay = 4;
    @Config
    public int requeueKey = 0;

    private static String detectedMode = "";
    private static int ticks = 0;
    private static boolean scoreboardAvailable = false;
    private static boolean awaitingLocraw = false;
    private static boolean pressed = false;
    private static boolean held = false;

    public Requeue() {
        super("Requeue", Module.Category.Hypixel);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Allows you to manually or automatically requeue last played Hypixel game.\n§d/rq §f- Requeue last played game");
        addToggle(new ToggleValue("Feedback message", "feedback", this));
        addToggle(new ToggleValue("Auto requeue", "autoRequeue", this));
        addSlider(new SliderValue("Delay", 0.0D, 10.0D, 1.0D, "s", "autoRequeueDelay", this, int.class));
        addBind(new BindValue("Requeue Bind", "requeueKey", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        if (Server.HYPIXEL.isActive()) {
            if (awaitingLocraw && !scoreboardAvailable && this.mc.theWorld.getScoreboard() != null) {
                scoreboardAvailable = true;
            }

            if (scoreboardAvailable && ++ticks == 20) {
                Meowtils.sendCleanMessage("/locraw");
            }
        }

        if (this.requeueKey != 0) {
            held = Keyboard.isKeyDown(this.requeueKey);
            if (held && !pressed) {
                requeue();
            }
            pressed = held;
        }
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();

        if (awaitingLocraw) {
            String locraw = msg.trim();

            if (!locraw.startsWith("{")) return;
            awaitingLocraw = false;

            try {
                if (!locraw.contains("dynamic") && !locraw.contains("REPLAY") && !locraw.contains("hub") && !locraw.equals("{\"server\":\"limbo\"}") && !locraw.equals("HOUSING")) {
                    int index = locraw.indexOf("mode\":\"");
                    if (index != -1) {
                        detectedMode = locraw.substring(index + 7).split("\"")[0];
                        this.lastPlayCommand = detectedMode;
                        ConfigManager.save();
                        if (this.feedback) {
                            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                                Meowtils.addMessage(EnumChatFormatting.GREEN + "Saved play command: " + EnumChatFormatting.WHITE + EnumChatFormatting.ITALIC + detectedMode);
                            }

                            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                                NotificationManager.show("Requeue", EnumChatFormatting.BLUE + "Saved command", NotificationManager.Type.INFO, 1500L);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (this.autoRequeue && !msg.contains(":") && Arrays.stream(HypixelUtil.GAME_END_MESSAGES).anyMatch(msg::contains)) {
            int delay = this.autoRequeueDelay * 20;
            new DelayedTask(Requeue::requeue, delay);
        }
    }

    @EventTarget(priority = EventPriority.LOWEST)
    public void cancelLocraw(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();

        if (msg.startsWith("{")) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() == this.mc.thePlayer) {
            scoreboardAvailable = false;
            awaitingLocraw = true;
            ticks = 0;
        }
    }

    public static void requeue() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        Requeue r = Module.get(Requeue.class);
        if (r == null) return;

        if (!r.enabled) {
            Meowtils.addMessage("Enable " + EnumChatFormatting.BLUE + "Requeue" + EnumChatFormatting.WHITE + " module to use this!");
            return;
        }

        if (Server.HYPIXEL.isNotActive()) {
            Meowtils.addMessage(EnumChatFormatting.RED + "This is only supported on " + EnumChatFormatting.GOLD + "Hypixel" + EnumChatFormatting.RED + ".");
            return;
        }

        if (!r.lastPlayCommand.isEmpty()) {
            if (r.feedback) {
                if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                    Meowtils.addMessage("Requeuing..");
                }

                if (Notifications.getMode() != Notifications.Mode.CHAT) {
                    NotificationManager.show("Requeue", EnumChatFormatting.BLUE + "Requeuing..", NotificationManager.Type.INFO, 1500L);
                }
            }

            Meowtils.sendCleanMessage("/play " + r.lastPlayCommand);
        } else {
            Meowtils.addMessage(EnumChatFormatting.RED + "No previously saved /play command found.");
        }
    }
}