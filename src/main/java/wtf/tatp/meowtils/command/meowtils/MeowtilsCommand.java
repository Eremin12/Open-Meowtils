package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class MeowtilsCommand extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------------------------------------------";
    private static final String SEPARATOR = " » ";
    private static final int PAGES = 7;

    private void sendPageContent(int page) {
        switch (page) {
            case 1:
                text("/meow <page>", "Meowtils help");
                text("/playcommands", "Show all short play commands");
                text("/shortcuts", "Show all other shortcut commands");
                text("/blacklist | /bl <player>", "Add to blacklist");
                text("/unblacklist | /ubl <player>", "Remove from blacklist");
                text("/safelist | /sl <player>", "Add to safelist");
                text("/unsafelist | /usl <player>", "Remove from safelist");
                break;
            case 2:
                text("/meowfriend | /mf <player>", "Add to friend list");
                text("/meowunfriend | /muf <player>", "Remove from friend list");
                text("/namemc | /nmc <ign>", "Link to NameMC");
                text("/plancke | /pla <ign>", "Link to Plancke");
                text("/laby <ign>", "Link to Laby");
                text("/rq", "Requeues last played Hypixel game");
                text("/s <bw|sw> <ign>", "Show stats for player");
                break;
            case 3:
                text("/theme", "Change custom prefix color");
                text("/itemblacklist <item>", "Add to item blacklist");
                text("/itemunblacklist <item>", "Remove from item blacklist");
                text("/itemsafelist <item>", "Add to item safelist");
                text("/itemunsafelist <item>", "Remove from item safelist");
                text("/capefolder", "Opens cape folder");
                text("/skinfolder", "Opens skin folder");
                break;
            case 4:
                text("/meowcolor", "Show all formatting codes");
                text("/urchin <player>", "Check a player in Urchin API");
                text("/urchintags", "Show Urchin tag colors");
                text("/meowdebug", "Toggle debug mode");
                text("/playerinfo <ign>", "Shows info about player");
                text("/send <msg|command>", "Send message directly to server");
                text("/resetgui", "Resets GUI positions");
                text("/meowtilsgui", "Opens GUI");
                break;
            case 5:
                text("/karma | /kar <ign>", "Link to 25karma");
                text("/shmeado | /shm <ign>", "Link to Shmeado");
                text("/recent <ign>", "Show recent Hypixel games");
                text("/playerstatus | /ps <ign>", "Show player status");
                text("/meowapi <key>", "Set Hypixel API key");
                text("/extension", "Opens extension folder");
                text("/reload", "Reloads all extensions");
                break;
            case 6:
                text("/anticheat", "Change anticheat alert color");
                text("/minemen | /mmc <ign>", "Link to Minemen Club");
                text("/bind <key>", "Set GUI bind");
                text("/fakemsg <msg>", "Display a fake message in chat");
                text("/nickbot", "Show NickBot commands");
                text("/meowping", "Show current ping");
                text("/customname <text>", "Set AccountHider name");
                break;
            case 7:
                text("/meowlog", "Opens log");
                text("/autogg", "Display Auto GG list options");
                text("/autogl", "Display Auto GL list options");
                text("/meowtilsfolder", "Opens Meowtils folder");
                text("/meowfilter", "Show filter commands");
                text("/gm <mode>", "Gamemode shortcut");
                break;
        }
    }

    private static void text(String command, String desc) {
        Meowtils.addCleanMessage(EnumChatFormatting.GREEN + command + EnumChatFormatting.YELLOW + " » " + EnumChatFormatting.DARK_GRAY + desc);
    }

    @Override
    public String getName() {
        return "meow";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        if (page < 1) page = 1;
        if (page > PAGES) page = PAGES;

        ChatComponentText header = new ChatComponentText("");

        if (page > 1) {
            ChatComponentText left = new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "                             «");
            left.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/meow " + (page - 1)));
            header.appendSibling(left);
        } else {
            header.appendText(EnumChatFormatting.DARK_PURPLE + "                             «");
        }

        ChatComponentText label = new ChatComponentText(EnumChatFormatting.DARK_PURPLE + " Meowtils " + page + " ");
        header.appendSibling(label);

        if (page < PAGES) {
            ChatComponentText right = new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "»");
            right.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/meow " + (page + 1)));
            header.appendSibling(right);
        } else {
            header.appendText(EnumChatFormatting.DARK_PURPLE + "»");
        }

        mc.thePlayer.addChatComponentMessage(header);
        Meowtils.addCleanMessage(LINE);
        sendPageContent(page);
        Meowtils.addCleanMessage(LINE);
    }
}