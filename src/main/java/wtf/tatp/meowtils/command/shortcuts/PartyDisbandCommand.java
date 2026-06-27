package wtf.tatp.meowtils.command.shortcuts;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartyDisbandCommand extends ClientCommand {

    @Override
    public String getName() {
        return "disband";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pdis");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/party disband");
    }
}