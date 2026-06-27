package wtf.tatp.meowtils.command.shortcuts.statuscommands;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class StatusAwayCommand extends ClientCommand {

    @Override
    public String getName() {
        return "away";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/status away");
    }
}