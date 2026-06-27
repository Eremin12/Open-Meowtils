package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartyDemoteCommand extends ClientCommand {

    @Override
    public String getName() {
        return "pd";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /pd <player>");
            return;
        }

        String playerName = args[0];

        Meowtils.sendCleanMessage("/party demote " + playerName);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}