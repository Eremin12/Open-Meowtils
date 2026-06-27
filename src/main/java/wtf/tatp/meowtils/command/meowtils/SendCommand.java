package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class SendCommand extends ClientCommand {

    @Override
    public String getName() {
        return "send";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /send <msg | command>");
            return;
        }

        String msg = String.join(" ", args);

        Meowtils.sendCleanMessage(msg);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}