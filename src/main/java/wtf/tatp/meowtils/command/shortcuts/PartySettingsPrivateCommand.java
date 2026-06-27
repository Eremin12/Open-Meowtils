package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartySettingsPrivateCommand extends ClientCommand {

    @Override
    public String getName() {
        return "psp";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/party settings private");
    }
}