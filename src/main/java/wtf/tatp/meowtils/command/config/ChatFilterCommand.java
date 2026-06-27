package wtf.tatp.meowtils.command.config;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.utility.ChatFilter;
import wtf.tatp.meowtils.util.Util;

public class ChatFilterCommand extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------------------------";
    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " » " + EnumChatFormatting.GRAY;

    @Override
    public String getName() {
        return "meowfilter";
    }

    @Override
    public void process(String[] args) throws CommandException {
        ChatFilter c = Module.get(ChatFilter.class);

        if (c == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "ChatFilter module not found.");
            return;
        }

        if (args.length == 0) {
            Meowtils.addMessage(LINE);
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/meowfilter reload" + SEPARATOR + "Reload current filter.");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/meowfilter folder" + SEPARATOR + "Open filter folder.");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/meowfilter select <filter name>" + SEPARATOR + "Selects filter.");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/meowfilter syntax" + SEPARATOR + "Shows list syntax.");
            Meowtils.addMessage(LINE);
            return;
        }

        if (args[0].equalsIgnoreCase("syntax")) {
            Meowtils.addMessage(LINE);
            Meowtils.addMessage(EnumChatFormatting.GREEN + "# <text>" + SEPARATOR + "Comment (text after is ignored)");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "-ServerName" + SEPARATOR + "Limit to server");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "&" + SEPARATOR + "Chain conditions together");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "?" + SEPARATOR + "Contains");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "=" + SEPARATOR + "Equals");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "<" + SEPARATOR + "Starts with");
            Meowtils.addMessage(EnumChatFormatting.GREEN + ">" + SEPARATOR + "Ends with");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "!?" + SEPARATOR + "Does not contain");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "!=" + SEPARATOR + "Does not equal");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "!<" + SEPARATOR + "Does not start with");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "!>" + SEPARATOR + "Does not end with");
            Meowtils.addMessage(LINE);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ChatFilter.reloadFilter();
            Meowtils.addMessage("Reloaded filter: " + EnumChatFormatting.GREEN.toString() + EnumChatFormatting.ITALIC + c.selectedFilter);
            return;
        }

        if (args[0].equalsIgnoreCase("folder")) {
            Util.openFolder(Meowtils.CHAT_FILTER_DIR, "filter");
            return;
        }

        if (args[0].equalsIgnoreCase("select")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /meowfilter select <filter name>");
                return;
            }
            c.selectedFilter = args[1];
            ChatFilter.reloadFilter();
            ConfigManager.save();
        }
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}