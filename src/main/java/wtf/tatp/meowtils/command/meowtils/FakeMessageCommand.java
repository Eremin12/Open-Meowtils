package wtf.tatp.meowtils.command.meowtils;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.util.ColorUtil;

public class FakeMessageCommand extends ClientCommand {

    @Override
    public String getName() {
        return "fakemessage";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("fakemsg");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /fakemessage <msg>");
            return;
        }

        String message = String.join(" ", args);
        message = ColorUtil.convertFormatting(message);

        Meowtils.addCleanMessage(message);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}