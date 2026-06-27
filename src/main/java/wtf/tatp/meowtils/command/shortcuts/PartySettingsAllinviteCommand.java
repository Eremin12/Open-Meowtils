package wtf.tatp.meowtils.command.shortcuts;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartySettingsAllinviteCommand extends ClientCommand {

    @Override
    public String getName() {
        return "psa";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/party settings allinvite");
    }
}