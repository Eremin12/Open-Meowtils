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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.GuiOpenEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.DelayedTask;

public class NickBot extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int delay = 500;
    @Config
    public boolean fourChar = true;
    @Config
    public boolean maxChar = false;
    @Config
    public boolean legacyNicks = true;
    @Config
    public boolean customWords = true;
    @Config
    public boolean plainText = false;
    @Config
    public boolean specialAffix = true;
    @Config
    public boolean autoAccept = true;
    @Config
    public boolean ignoreLimboKick = false;
    @Config
    public boolean listSkippedNicks = false;

    private static final File NICK_LIST = Meowtils.NICKBOT_LIST;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Set<String>>() {}.getType();
    private static Set<String> list = new HashSet<>();
    private static boolean active = false;
    private static String currentNick = "";
    private static int skippedNicks = 0;
    private static final Map<Character, Character> ALT_LETTERS = new HashMap<>();

    public NickBot() {
        super("NickBot", Module.Category.Hypixel);
        tag(Module.ModuleTag.SAFE);
        tooltip("Automatically reroll nicks on Hypixel. Press ESC to stop it.\n§d/nickbot §f- For more info about commands");
        addSlider(new SliderValue("Delay", 0.0D, 2000.0D, 50.0D, "ms", "delay", this, int.class));
        addButton(new ButtonValue("Start", 5.0F, NickBot::start));
        addToggle(new ToggleValue("Auto accept good nick", "autoAccept", this));
        addToggle(new ToggleValue("Ignore limbo kick", "ignoreLimboKick", this));
        addToggle(new ToggleValue("List skipped nicks", "listSkippedNicks", this));
        addCheck(new CheckValue("Legacy nicks", "legacyNicks", this));
        addCheck(new CheckValue("Four char", "fourChar", this));
        addCheck(new CheckValue("Max char", "maxChar", this));
        addCheck(new CheckValue("Plain text", "plainText", this));
        addCheck(new CheckValue("Special affixes", "specialAffix", this));
        addCheck(new CheckValue("Custom words", "customWords", this));
    }

    static {
        ALT_LETTERS.put('0', 'o');
        ALT_LETTERS.put('1', 'i');
        ALT_LETTERS.put('3', 'e');
        ALT_LETTERS.put('4', 'a');
        ALT_LETTERS.put('5', 's');
        ALT_LETTERS.put('6', 'g');
        ALT_LETTERS.put('7', 't');
        ALT_LETTERS.put('8', 'b');
        ALT_LETTERS.put('9', 'g');
    }

    public static void init() {
        load();
    }

    @EventTarget
    public void onGuiOpen(GuiOpenEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (!(event.getGui() instanceof GuiScreenBook)) return;
        if (!active) return;

        int clickDelay = this.delay / 50;
        GuiScreenBook book = (GuiScreenBook) event.getGui();
        if (book == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Unable to find book.");
            stop();
            return;
        }

        new DelayedTask(() -> {
            if (!active) return;
            if (!(this.mc.currentScreen instanceof GuiScreenBook)) { stop(); return; }

            String text = getBookText(book);
            if (text == null) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Unable to find book text.");
                stop();
                return;
            }

            if (text.contains("Uh-oh!")) {
                new DelayedTask(() -> Meowtils.sendCleanMessage("/nick help setrandom"), 40);
                return;
            }

            if (text.contains("USE NAME") && text.contains("TRY AGAIN")) {
                String nick = extractNick(text);
                if (nick == null || nick.isEmpty()) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Nick was null or empty.");
                    stop();
                    return;
                }

                if (this.listSkippedNicks) {
                    Meowtils.addMessage("Skipped: " + EnumChatFormatting.RED.toString() + EnumChatFormatting.ITALIC + nick);
                }

                if (isGoodNick(nick)) {
                    if (this.autoAccept) Meowtils.sendCleanMessage("/nick actuallyset " + nick);
                    Meowtils.addMessage("Skipped nicks: " + EnumChatFormatting.YELLOW + skippedNicks);
                    active = false;
                    skippedNicks = 0;
                } else {
                    Meowtils.sendCleanMessage("/nick help setrandom");
                    skippedNicks++;
                }
            } else if (text.contains("TRY AGAIN")) {
                Meowtils.addMessage(EnumChatFormatting.RED + "An error occured in " + EnumChatFormatting.BLUE + "NickBot" + EnumChatFormatting.RED + ", re-rolling...");
                Meowtils.sendCleanMessage("/nick help setrandom");
            } else {
                stop();
            }
        }, clickDelay);
    }

    @EventTarget(priority = EventPriority.LOW)
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();

        if (active) {
            if (msg.contains("You are now nicked as ")) {
                currentNick = msg.replace("You are now nicked as ", "").replace("!", "").replace(" ", "");
                Meowtils.sendCleanMessage("/nick help setrandom");
            } else if (msg.equals("You are not allowed to do this!")) {
                active = false;
                Meowtils.addMessage(EnumChatFormatting.RED + "Unable to nick.");
            }
            event.setCancelled(true);
        }

        if (this.ignoreLimboKick && msg.equals("You were spawned in Limbo.") && !active) {
            Meowtils.addMessage(EnumChatFormatting.BLUE + "NickBot" + EnumChatFormatting.WHITE + ": Ignoring limbo..");
            new DelayedTask(() -> {
                Meowtils.sendCleanMessage("/lobby");
                new DelayedTask(NickBot::start, 20);
            }, 10);
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (Keyboard.isKeyDown(0)) return;
        if (!active) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            stop();
        }
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (this.mc.currentScreen instanceof GuiScreenBook && active) {
            Meowtils.drawString("NickBot Active", 10, 10, 1.0F, -1);
            Meowtils.drawString("Press " + EnumChatFormatting.RED + "ESC" + EnumChatFormatting.WHITE + " to stop.", 10, 20, 1.0F, -1);
            Meowtils.drawString("Skipped nicks: " + EnumChatFormatting.GREEN + skippedNicks, 10, 30, 1.0F, -1);
        }
    }

    private static String getBookText(GuiScreenBook gui) {
        for (String field : new String[] { "bookObj", "field_146474_h" }) {
            try {
                Field f = GuiScreenBook.class.getDeclaredField(field);
                f.setAccessible(true);

                ItemStack book = (ItemStack) f.get(gui);
                if (book == null || !book.hasTagCompound()) return null;

                NBTTagList pages = book.getTagCompound().getTagList("pages", 8);
                if (pages.tagCount() == 0) return null;

                String text = pages.getStringTagAt(0);
                return jsonSerializer(text);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static String jsonSerializer(String bookText) {
        try {
            IChatComponent c = IChatComponent.Serializer.jsonToComponent(bookText);
            return c.getUnformattedText();
        } catch (Exception e) {
            return EnumChatFormatting.getTextWithoutFormattingCodes(bookText);
        }
    }

    private static boolean isGoodNick(String nick) {
        NickBot n = Module.get(NickBot.class);
        if (n == null) return false;

        String lower = nick.toLowerCase();
        String normalized = normalizeAlt(lower);

        if (currentNick.toLowerCase().equals(lower)) {
            return false;
        }

        if ((lower.startsWith("xx") || lower.endsWith("xx")) && n.specialAffix) {
            return true;
        }

        if (lower.length() == 4 && n.fourChar) {
            Meowtils.addMessage("Four char nick: " + EnumChatFormatting.YELLOW + nick);
            return true;
        }

        if (lower.length() == 16 && normalized.matches("^[a-z]+$") && n.maxChar) {
            Meowtils.addMessage("Max char nick: " + EnumChatFormatting.YELLOW + nick);
            return true;
        }

        if (normalized.matches("^[a-z_]+$") && n.plainText) {
            Meowtils.addMessage("Plain text nick: " + EnumChatFormatting.YELLOW + nick);
            return true;
        }

        if (Character.isUpperCase(lower.charAt(0)) && n.legacyNicks) {
            for (int i = 1; i < lower.length(); i++) {
                if (Character.isUpperCase(lower.charAt(i))) {
                    return false;
                }
            }

            char last = Character.MIN_VALUE;
            int count = 1;

            for (int j = 0; j < lower.length(); j++) {
                char c = lower.charAt(j);

                if ("aeiouy".indexOf(c) >= 0 && c == last) {
                    count++;
                    if (count >= 3) {
                        Meowtils.addMessage("Legacy nick: " + EnumChatFormatting.YELLOW + nick);
                        return true;
                    }
                } else {
                    last = c;
                    count = 1;
                }
            }
        }

        if ((matches(lower) || matches(normalized)) && n.customWords) {
            Meowtils.addMessage("Nick matched list: " + EnumChatFormatting.YELLOW + nick);
            return true;
        }

        return false;
    }

    private static String extractNick(String text) {
        if (text == null) return null;
        String[] lines = text.split("\n");
        if (lines.length < 2 || lines[1] == null) return null;
        return ColorUtil.unformattedText(lines[1].trim());
    }

    private static String normalizeAlt(String nick) {
        StringBuilder sb = new StringBuilder();
        for (char c : nick.toCharArray()) {
            sb.append(ALT_LETTERS.getOrDefault(c, c));
        }
        return sb.toString();
    }

    public static void start() {
        active = true;
        Meowtils.sendCleanMessage("/nick reuse");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Started " + EnumChatFormatting.BLUE + "NickBot" + EnumChatFormatting.GREEN + ".");
    }

    public static void stop() {
        active = false;
        Meowtils.addMessage(EnumChatFormatting.RED + "Stopped " + EnumChatFormatting.BLUE + "NickBot" + EnumChatFormatting.RED + ".");
    }

    public static boolean matches(String name) {
        if (name == null) return false;
        name = name.toLowerCase();

        for (String entry : list) {
            if (entry == null || entry.length() < 2) continue;
            char type = entry.charAt(0);
            String value = entry.substring(1).toLowerCase();

            switch (type) {
                case '?':
                    if (name.contains(value)) return true;
                    continue;
                case '=':
                    if (name.equals(value)) return true;
                    continue;
                case '<':
                    if (name.startsWith(value)) return true;
                    continue;
                case '>':
                    if (name.endsWith(value)) return true;
                    continue;
            }
            if (name.contains(entry)) return true;
        }
        return false;
    }

    public static void showList() {
        if (list.isEmpty()) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Your list is empty!");
            return;
        }
        for (String text : list) {
            Meowtils.addMessage(text);
        }
    }

    public static boolean hasEntry(String name) {
        name = name.toLowerCase().replace(" ", "");
        return list.contains(name);
    }

    public static void addName(String name) {
        name = name.toLowerCase().replace(" ", "");
        list.add(name);
        save();
    }

    public static void removeName(String name) {
        name = name.toLowerCase().replace(" ", "");
        list.remove(name);
        save();
    }

    private static void load() {
        if (!NICK_LIST.exists()) return;
        try (Reader reader = new FileReader(NICK_LIST)) {
            Set<String> loaded = gson.fromJson(reader, TYPE);
            if (loaded != null) {
                list = loaded;
            } else {
                list = new HashSet<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void save() {
        try (Writer writer = new FileWriter(NICK_LIST)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReset() {
        active = false;
        currentNick = "";
    }

    @Override
    public void onEnable() {
        Meowtils.addMessage(EnumChatFormatting.BLUE + "NickBot" + EnumChatFormatting.WHITE + " is still in beta, it may not be complete.");
    }
}