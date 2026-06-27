package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class GuildToggleCommand extends ClientCommand {

    @Override
    public String getName() {
        return "gt";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/guild toggle");
    }
}