package wtf.tatp.meowtils.command.shortcuts.nickcommands;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class NickReuseCommand extends ClientCommand {

    @Override
    public String getName() {
        return "renick";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/nick reuse");
    }
}