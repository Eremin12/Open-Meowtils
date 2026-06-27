package wtf.tatp.meowtils.command.shortcuts.statuscommands;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class StatusBusyCommand extends ClientCommand {

    @Override
    public String getName() {
        return "busy";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/status busy");
    }
}