package wtf.tatp.meowtils.command.shortcuts;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ReplayCommand extends ClientCommand {

    @Override
    public String getName() {
        return "rp";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rpl");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/replay");
    }
}