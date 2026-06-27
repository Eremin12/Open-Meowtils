package wtf.tatp.meowtils;

import java.awt.Font;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.command.RegisterCommand;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.event.RenderTickEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.font.FontManager;
import wtf.tatp.meowtils.font.FontRenderer;
import wtf.tatp.meowtils.gui.ClickGUI;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.LatencyHandler;
import wtf.tatp.meowtils.handler.PartyHandler;
import wtf.tatp.meowtils.handler.ResetHandler;
import wtf.tatp.meowtils.handler.command.CommandHandler;
import wtf.tatp.meowtils.manager.KeybindManager;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.SoundLoader;
import wtf.tatp.meowtils.manager.icons.RegisterIcon;
import wtf.tatp.meowtils.manager.session.SessionManager;
import wtf.tatp.meowtils.manager.slots.RegisterSlot;
import wtf.tatp.meowtils.manager.updater.UpdateManager;
import wtf.tatp.meowtils.module.bedwars.HeightOverlay;
import wtf.tatp.meowtils.module.hypixel.AutoGG;
import wtf.tatp.meowtils.module.hypixel.NickBot;
import wtf.tatp.meowtils.module.meowtils.GUI;
import wtf.tatp.meowtils.module.meowtils.Settings;
import wtf.tatp.meowtils.module.skywars.ItemHighlight;
import wtf.tatp.meowtils.module.utility.ChatFilter;
import wtf.tatp.meowtils.util.FileUtil;
import wtf.tatp.meowtils.util.Prefix;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;
import wtf.tatp.meowtils.util.anticheat.AntiCheatData;
import wtf.tatp.meowtils.util.fixes.ModConflictWarnings;

public class Meowtils {
    private static String shit  = "kiss my ass  @WindyTeam";
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static FontRenderer fontRenderer;
    private static boolean initialized = false;
    private static boolean preInitialized = false;
    public static boolean loadedExtensions = false;
    public static final Meowtils INSTANCE = new Meowtils();
    private static final Logger LOGGER = LogManager.getLogger("Meowtils" + shit);
    public static final String VERSION = "2.0.0";

    public static void info(String msg) {
        LOGGER.info("[Meowtils]: {}", msg);
    }

    public static void warn(String msg) {
        LOGGER.warn("[Meowtils]: {}", msg);
    }

    public static void error(String msg) {
        LOGGER.error("[Meowtils]: {}", msg);
    }

    public static void fatal(String msg) {
        LOGGER.fatal("[Meowtils]: {}", msg);
    }

    public static final File MEOWTILS_DIR = new File(mc.mcDataDir, "meowtils");
    public static final File MEOWTILS_CONFIG = new File(mc.mcDataDir, "meowtils/meowtils.json");
    public static final File MEOWTILS_LOG = new File(mc.mcDataDir, "meowtils/meowtils.log");
    public static final File EXTENSION_DIR = new File(mc.mcDataDir, "meowtils/extensions");
    public static final File CUSTOM_CAPE_DIR = new File(mc.mcDataDir, "meowtils/custom_cape");
    public static final File CUSTOM_SKIN_DIR = new File(mc.mcDataDir, "meowtils/custom_skins");
    public static final File ITEM_DIR = new File(mc.mcDataDir, "meowtils/items");
    public static final File ITEMHIGHLIGHT_BLACKLIST = new File(mc.mcDataDir, "meowtils/items/itemhighlightblacklist.json");
    public static final File ITEMHIGHLIGHT_SAFELIST = new File(mc.mcDataDir, "meowtils/items/itemhighlightsafelist.json");
    public static final File AUTO_UPDATE_DIR = new File(mc.mcDataDir, "meowtils/auto_update");
    public static final File MEOWTILS_SAFELIST = new File(mc.mcDataDir, "meowtils/meowtilssafelist.json");
    public static final File MEOWTILS_BLACKLIST = new File(mc.mcDataDir, "meowtils/meowtilsblacklist.json");
    public static final File MEOWTILS_FRIENDLIST = new File(mc.mcDataDir, "meowtils/meowtilsfriendlist.json");
    public static final File MEOWTILS_URCHIN_TAGS = new File(mc.mcDataDir, "meowtils/urchintags.json");
    public static final File NICKBOT_LIST = new File(mc.mcDataDir, "meowtils/nickbot_list.json");
    public static final File AUTOGG_LIST = new File(mc.mcDataDir, "meowtils/autogg_list.json");
    public static final File AUTOGL_LIST = new File(mc.mcDataDir, "meowtils/autogl_list.json");
    public static final File CHAT_FILTER_DIR = new File(mc.mcDataDir, "meowtils/chatfilters");
    public static final File DEFAULT_CHAT_FILTER = new File(mc.mcDataDir, "meowtils/chatfilters/default.txt");

    private static ClickGUI clickGUI;

    public static void addMessage(String message) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        mc.thePlayer.addChatMessage(new ChatComponentText(Prefix.getPrefix() + EnumChatFormatting.RESET + message));
    }

    public static void debugMessage(String message) {
        GUI gui = Module.get(GUI.class);
        if (gui == null || !gui.debugMode) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        mc.thePlayer.addChatMessage(new ChatComponentText(Prefix.getPrefix() + EnumChatFormatting.RESET + message));
    }

    public static void addCleanMessage(String message) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static void sendMessage(String message) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        mc.thePlayer.sendChatMessage("/pc Meow » " + message);
    }

    public static void sendCleanMessage(String message) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        mc.thePlayer.sendChatMessage(message);
    }

    public static void preInit() {
        if (preInitialized) return;
        preInitialized = true;

        warn("Attempting to load..");
        INSTANCE.onPreInit();
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        INSTANCE.onInit();
        warn("Loaded successfully!");
    }

    public void onPreInit() {
        FileUtil.createDir(MEOWTILS_DIR);
        FileUtil.createFile(MEOWTILS_CONFIG);
        FileUtil.createFile(MEOWTILS_LOG);
        LogManager.getLogger();
    }

    public void onInit() {
        FileUtil.createDir(EXTENSION_DIR);
        FileUtil.createDir(AUTO_UPDATE_DIR);
        FileUtil.createDir(CUSTOM_CAPE_DIR);
        FileUtil.createDir(CUSTOM_SKIN_DIR);
        FileUtil.createDir(ITEM_DIR);
        FileUtil.createDir(CHAT_FILTER_DIR);

        FileUtil.createFile(MEOWTILS_SAFELIST);
        FileUtil.createFile(MEOWTILS_BLACKLIST);
        FileUtil.createFile(MEOWTILS_FRIENDLIST);
        FileUtil.createFile(MEOWTILS_URCHIN_TAGS);
        FileUtil.createFile(ITEMHIGHLIGHT_BLACKLIST);
        FileUtil.createFile(ITEMHIGHLIGHT_SAFELIST);
        FileUtil.createFile(NICKBOT_LIST);
        FileUtil.createFile(AUTOGG_LIST);
        FileUtil.createFile(AUTOGL_LIST);
        FileUtil.createFile(DEFAULT_CHAT_FILTER);

        ConfigManager.init(MEOWTILS_CONFIG);
        ConfigManager.load();

        Font font = FontManager.load("font.ttf", 30.0F);
        fontRenderer = new FontRenderer(font);

        clickGUI = new ClickGUI();

        RegisterCommand.init();
        RegisterIcon.init();
        RegisterSlot.init();
        ItemHighlight.init();
        HeightOverlay.init();
        SoundLoader.init();
        NickBot.init();
        AutoGG.init();
        GUI.init();
        ChatFilter.init();

        EventManager.register(this);
        EventManager.register(new SessionManager());
        EventManager.register(new AntiCheatData());
        EventManager.register(new ResetHandler());
        EventManager.register(new UpdateManager());
        EventManager.register(new PartyHandler());
        EventManager.register(new MeowtilsAlert());
        EventManager.register(new TeamUtil());
        EventManager.register(new KeybindManager());
        EventManager.register(new ModConflictWarnings());
        EventManager.register(new ConfigManager());
        EventManager.register(new NotificationManager());
        EventManager.register(new CommandHandler());
        EventManager.register(new LatencyHandler());
    }

    public static ClickGUI getClickGUI() {
        return clickGUI;
    }

    public static void drawString(String text, int x, int y, float scale, int color) {
        Settings settings = Module.get(Settings.class);
        if (settings != null && settings.smoothFont) {
            fontRenderer.drawScaledStringWithShadow(text, x, y, color, scale * 10.0F);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            mc.fontRendererObj.drawString(text, (int) (x / scale), (int) (y / scale), color);
            GlStateManager.popMatrix();
        }
    }

    public static int offsetString(float scale) {
        int height = mc.fontRendererObj.FONT_HEIGHT;
        Settings settings = Module.get(Settings.class);
        int offset = (settings != null && settings.smoothFont) ? 2 : 3;
        return (int) (height * scale + offset);
    }

    @Deprecated
    public static void notify(String title, String message, NotificationManager.Type type, long time) {
        NotificationManager.show(title, message, type, time);
    }

    @EventTarget
    public void onRenderTick(RenderTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != RenderTickEvent.Phase.PRE) return;
        if (mc.currentScreen != null) return;

        GUI gui = Module.get(GUI.class);
        if (gui != null && Keyboard.isKeyDown(gui.key)) {
            mc.displayGuiScreen(getClickGUI());
            debugMessage("Attempted to open GUI");
        }
    }

    public static boolean isLunar() {
        return Util.isClassLoaded("com.moonsworth.lunar.genesis.Genesis");
    }
}