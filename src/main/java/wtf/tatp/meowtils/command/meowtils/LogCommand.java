package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.log.LogFrame;

public class LogCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowlog";
    }

    @Override
    public void process(String[] args) throws CommandException {
        LogFrame.open();
        Meowtils.info("Opened log.");
        Meowtils.addMessage("Opened log.");
    }
}