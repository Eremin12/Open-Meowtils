package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartyPromoteCommand extends ClientCommand {

    @Override
    public String getName() {
        return "pp";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /pp <player>");
            return;
        }

        String playerName = args[0];

        Meowtils.sendCleanMessage("/party promote " + playerName);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}