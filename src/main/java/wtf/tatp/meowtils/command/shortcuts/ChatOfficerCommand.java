package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ChatOfficerCommand extends ClientCommand {

    @Override
    public String getName() {
        return "co";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/chat officer");
    }
}