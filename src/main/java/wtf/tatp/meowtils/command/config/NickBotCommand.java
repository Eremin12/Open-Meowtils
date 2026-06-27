package wtf.tatp.meowtils.command.config;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.hypixel.NickBot;

public class NickBotCommand extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------------------------";
    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " » " + EnumChatFormatting.GRAY;

    @Override
    public String getName() {
        return "nickbot";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length == 0) {
            Meowtils.addMessage(LINE);
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/nickbot start" + SEPARATOR + "Start the NickBot");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/nickbot add" + SEPARATOR + "Add nick entry");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/nickbot remove" + SEPARATOR + "Remove nick entry");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/nickbot list" + SEPARATOR + "Show word list");
            Meowtils.addMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Syntax:");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "?meow" + SEPARATOR + "Contains meow");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "=meow" + SEPARATOR + "Equals meow");
            Meowtils.addMessage(EnumChatFormatting.GREEN + "<meow" + SEPARATOR + "Starts with meow");
            Meowtils.addMessage(EnumChatFormatting.GREEN + ">meow" + SEPARATOR + "Ends with meow");
            Meowtils.addMessage("Example: " + EnumChatFormatting.GREEN + "/nickbot add ?Ninja");
            Meowtils.addMessage(LINE);
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            NickBot nickBot = Module.get(NickBot.class);
            if (nickBot == null || !nickBot.enabled) {
                Meowtils.addMessage("Enable " + EnumChatFormatting.BLUE + "NickBot" + EnumChatFormatting.WHITE + " module to use this!");
                return;
            }
            NickBot.start();
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /nickbot add <text>");
                return;
            }
            if (NickBot.hasEntry(args[1])) {
                Meowtils.addMessage(EnumChatFormatting.RED + "This is already an entry.");
                return;
            }
            NickBot.addName(args[1]);
            Meowtils.addMessage(EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.WHITE + args[1] + EnumChatFormatting.GREEN + " to the list.");
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /nickbot remove <text>");
                return;
            }
            if (!NickBot.hasEntry(args[1])) {
                Meowtils.addMessage(EnumChatFormatting.RED + "This is not an entry.");
                return;
            }
            NickBot.removeName(args[1]);
            Meowtils.addMessage(EnumChatFormatting.RED + "Removed " + EnumChatFormatting.WHITE + args[1] + EnumChatFormatting.RED + " from the list.");
        }

        if (args[0].equalsIgnoreCase("list")) {
            Meowtils.addMessage(LINE);
            NickBot.showList();
            Meowtils.addMessage(LINE);
        }
    }
}