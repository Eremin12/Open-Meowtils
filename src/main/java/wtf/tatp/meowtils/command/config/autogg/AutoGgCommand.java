package wtf.tatp.meowtils.command.config.autogg;

import java.util.Arrays;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.hypixel.AutoGG;

public class AutoGgCommand extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------------------------";
    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " » " + EnumChatFormatting.GRAY;

    @Override
    public String getName() {
        return "autogg";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length == 0) {
            Meowtils.addMessage(LINE);
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/autogg add <msg>" + SEPARATOR + "Add message to list");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/autogg remove <msg>" + SEPARATOR + "Remove message from list");
            Meowtils.addMessage(EnumChatFormatting.YELLOW + "/autogg list" + SEPARATOR + "List all messages");
            Meowtils.addMessage(LINE);
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /autogg add <msg>");
                return;
            }
            String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            if (AutoGG.hasGgMessage(msg)) {
                Meowtils.addMessage(msg + EnumChatFormatting.GREEN + " is already added.");
                return;
            }
            AutoGG.addGgMessage(msg);
            Meowtils.addMessage(EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.WHITE + msg + EnumChatFormatting.GREEN + " to the list.");
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /autogg remove <msg>");
                return;
            }
            String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            if (!AutoGG.hasGgMessage(msg)) {
                Meowtils.addMessage(msg + EnumChatFormatting.RED + " is not in the list.");
                return;
            }
            AutoGG.removeGgMessage(msg);
            Meowtils.addMessage(EnumChatFormatting.RED + "Removed " + EnumChatFormatting.WHITE + msg + EnumChatFormatting.RED + " from the list.");
        }

        if (args[0].equalsIgnoreCase("list")) {
            Meowtils.addMessage(LINE);
            AutoGG.showGgMessages();
            Meowtils.addMessage(LINE);
        }
    }
}