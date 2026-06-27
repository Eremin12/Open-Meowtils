package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class BlockRemoveCommand extends ClientCommand {

    @Override
    public String getName() {
        return "unblock";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /unblock <player>");
            return;
        }

        String playerName = args[0];

        Meowtils.sendCleanMessage("/block remove " + playerName);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}