package wtf.tatp.meowtils.command.debug;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class TestCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowtest";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.addMessage("Ran test command.");
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}