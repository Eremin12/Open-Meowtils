package wtf.tatp.meowtils.command.shortcuts;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.meowtils.Notifications;

public class ShoutCommand extends ClientCommand {

    @Override
    public String getName() {
        return "sh";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("shout");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /sh <msg>");
            return;
        }

        String shoutMessage = String.join(" ", args);

        if (System.currentTimeMillis() - Notifications.lastShout >= 60000L) {
            Meowtils.sendCleanMessage("/shout " + shoutMessage);
        } else {
            Meowtils.addMessage(EnumChatFormatting.BOLD + "Shout cooldown ends in " + EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + Notifications.shoutTimeLeft + EnumChatFormatting.WHITE.toString() + EnumChatFormatting.BOLD + " seconds!");
        }
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}