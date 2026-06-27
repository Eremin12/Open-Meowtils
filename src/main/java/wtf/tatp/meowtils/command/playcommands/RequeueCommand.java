package wtf.tatp.meowtils.command.playcommands;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.hypixel.Requeue;

public class RequeueCommand extends ClientCommand {

    @Override
    public String getName() {
        return "rq";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Requeue.requeue();
    }
}