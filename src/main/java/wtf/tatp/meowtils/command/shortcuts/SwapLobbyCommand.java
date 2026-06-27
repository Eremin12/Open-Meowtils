package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class SwapLobbyCommand extends ClientCommand {

    @Override
    public String getName() {
        return "slb";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /slb <Lobby number>");
            return;
        }

        String lobby = args[0];

        Meowtils.sendCleanMessage("/swaplobby " + lobby);
    }
}