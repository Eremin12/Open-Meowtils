package wtf.tatp.meowtils.module.hypixel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.util.DelayedTask;
import wtf.tatp.meowtils.util.HypixelUtil;

public class AutoGG extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean autoggEnabled = true;
    @Config
    public boolean sendFirstMessage = true;
    @Config
    public boolean sendSecondMessage = false;
    @Config
    public String firstMessage = "gg";
    @Config
    public String secondMessage = "<3";
    @Config
    public int autoggDelay = 0;
    @Config
    public int autoggSecondDelay = 0;
    @Config
    public boolean autoglEnabled = false;
    @Config
    public String autoglMessage = "glhf";
    @Config
    public int autoglDelay = 5;
    @Config
    public boolean autoggRandom = false;
    @Config
    public boolean autoglRandom = false;

    private static final File AUTOGG_LIST = Meowtils.AUTOGG_LIST;
    private static final File AUTOGL_LIST = Meowtils.AUTOGL_LIST;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<ArrayList<String>>() {}.getType();
    private static ArrayList<String> gg_messages = new ArrayList<>();
    private static ArrayList<String> gl_messages = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private static String lastGgMessage = "";
    private static String lastGlMessage = "";
    private static boolean activated = false;

    private static final List<String> DEFAULT_GG_MESSAGES = Arrays.asList("gg", "Good Game", "gf");
    private static final List<String> DEFAULT_GL_MESSAGES = Arrays.asList("Have a nice game!", "glhf");

    public AutoGG() {
        super("AutoGG", Module.Category.Hypixel);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Automatically sends messages on game start/end.\n§d/autogg|/autogl <add|remove|list> <msg> §f- Add/remove/list messages");
        addExpand(new ExpandValue("Auto GG", e -> {
            e.addToggle(new ToggleValue("Enabled", "autoggEnabled", this));
            e.addToggle(new ToggleValue("Random message", "autoggRandom", this));
            e.addCheck(new CheckValue("First message", "sendFirstMessage", this));
            e.addText(new TextValue(null, "firstMessage", this));
            e.addSlider(new SliderValue("Delay", 0.0D, 1000.0D, 50.0D, "ms", "autoggDelay", this, int.class));
            e.addCheck(new CheckValue("Second message", "sendSecondMessage", this));
            e.addText(new TextValue(null, "secondMessage", this));
            e.addSlider(new SliderValue("Delay", 0.0D, 1000.0D, 50.0D, "ms", "autoggSecondDelay", this, int.class));
        }));
        addExpand(new ExpandValue("Auto GL", e -> {
            e.addToggle(new ToggleValue("Enabled", "autoglEnabled", this));
            e.addToggle(new ToggleValue("Random message", "autoglRandom", this));
            e.addText(new TextValue(null, "autoglMessage", this));
            e.addSlider(new SliderValue("Send at", 1.0D, 15.0D, 1.0D, "s", "autoglDelay", this, int.class));
        }));
    }

    public static void init() {
        load();
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();
        String end = (this.autoglDelay == 1) ? " second!" : " seconds!";
        if (Server.HYPIXEL.isNotActive() && Server.UNIVERSAL.isNotActive()) {
            return;
        }

        if (this.autoggEnabled && this.sendFirstMessage && Arrays.stream(HypixelUtil.GAME_END_MESSAGES).anyMatch(msg::contains) && !msg.contains(":") && !activated) {
            int firstDelay = this.autoggDelay / 50;
            int secondDelay = this.autoggSecondDelay / 50;

            if (this.firstMessage.isEmpty() || this.secondMessage.isEmpty() || gg_messages.isEmpty()) {
                Meowtils.addMessage(EnumChatFormatting.BLUE + "[AutoGG]: " + EnumChatFormatting.WHITE + "No message set!");
                return;
            }

            new DelayedTask(() -> {
                if (this.autoggRandom) {
                    String randomMessage = getRandomMessage(gg_messages, lastGgMessage);
                    lastGgMessage = randomMessage;
                    Meowtils.sendCleanMessage("/ac " + randomMessage);
                } else {
                    Meowtils.sendCleanMessage("/ac " + this.firstMessage);
                }
            }, firstDelay);

            if (this.sendSecondMessage) {
                new DelayedTask(() -> {
                    if (this.autoggRandom) {
                        String randomMessage = getRandomMessage(gg_messages, lastGgMessage);
                        lastGgMessage = randomMessage;
                        Meowtils.sendCleanMessage("/ac " + randomMessage);
                    } else {
                        Meowtils.sendCleanMessage("/ac " + this.secondMessage);
                    }
                }, secondDelay);
            }

            activated = true;
        }

        if (this.autoglEnabled && msg.contains("The game starts in " + this.autoglDelay + end) && !msg.contains(":")) {
            if (this.autoglMessage.isEmpty() || gl_messages.isEmpty()) {
                Meowtils.addMessage(EnumChatFormatting.BLUE + "[AutoGL]: " + EnumChatFormatting.WHITE + "No message set!");
                return;
            }

            if (this.autoglRandom) {
                String randomMessage = getRandomMessage(gl_messages, lastGlMessage);
                lastGlMessage = randomMessage;
                Meowtils.sendCleanMessage("/ac " + randomMessage);
            } else {
                Meowtils.sendCleanMessage("/ac " + this.autoglMessage);
            }
        }
    }

    private static String getRandomMessage(ArrayList<String> messages, String lastMessage) {
        if (messages.isEmpty()) return "";

        if (messages.size() == 1) {
            return messages.get(0);
        }

        String next;
        do {
            next = messages.get(RANDOM.nextInt(messages.size()));
        } while (next.equals(lastMessage));

        return next;
    }

    public static void showGgMessages() {
        if (gg_messages.isEmpty()) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Your AutoGG message list is empty!");
            return;
        }
        if (gg_messages.size() > 40) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Your AutoGG message list is too large to display, please open the file instead.");
            return;
        }
        for (String text : gg_messages) {
            Meowtils.addMessage(text);
        }
    }

    public static void showGlMessages() {
        if (gl_messages.isEmpty()) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Your AutoGL message list is empty!");
            return;
        }
        if (gl_messages.size() > 40) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Your AutoGL message list is too large to display, please open the file instead.");
            return;
        }
        for (String text : gl_messages) {
            Meowtils.addMessage(text);
        }
    }

    public static void addGgMessage(String msg) {
        gg_messages.add(msg);
        save();
    }

    public static void removeGgMessage(String msg) {
        gg_messages.remove(msg);
        save();
    }

    public static void addGlMessage(String msg) {
        gl_messages.add(msg);
        save();
    }

    public static void removeGlMessage(String msg) {
        gl_messages.remove(msg);
        save();
    }

    public static boolean hasGgMessage(String msg) {
        return gg_messages.contains(msg);
    }

    public static boolean hasGlMessage(String msg) {
        return gl_messages.contains(msg);
    }

    private static void load() {
        boolean initGgList = (!AUTOGG_LIST.exists() || AUTOGG_LIST.length() == 0L);
        boolean initGlList = (!AUTOGL_LIST.exists() || AUTOGL_LIST.length() == 0L);

        if (initGgList) {
            gg_messages.clear();
            gg_messages.addAll(DEFAULT_GG_MESSAGES);
        } else {
            readGgList();
        }

        if (initGlList) {
            gl_messages.clear();
            gl_messages.addAll(DEFAULT_GL_MESSAGES);
        } else {
            readGlList();
        }

        if (initGlList || initGgList) {
            save();
        }
    }

    private static void readGgList() {
        try (Reader reader = new FileReader(AUTOGG_LIST)) {
            ArrayList<String> data = gson.fromJson(reader, TYPE);
            gg_messages = (data != null) ? data : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            gg_messages = new ArrayList<>();
        }
    }

    private static void readGlList() {
        try (Reader reader = new FileReader(AUTOGL_LIST)) {
            ArrayList<String> data = gson.fromJson(reader, TYPE);
            gl_messages = (data != null) ? data : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            gl_messages = new ArrayList<>();
        }
    }

    private static void save() {
        try (Writer writer = new FileWriter(AUTOGG_LIST)) {
            gson.toJson(gg_messages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = new FileWriter(AUTOGL_LIST)) {
            gson.toJson(gl_messages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReset() {
        activated = false;
    }
}