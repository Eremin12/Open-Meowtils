package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartyTransferCommand extends ClientCommand {

    @Override
    public String getName() {
        return "pt";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /pt <player>");
            return;
        }

        String playerName = args[0];

        Meowtils.sendCleanMessage("/party transfer " + playerName);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}