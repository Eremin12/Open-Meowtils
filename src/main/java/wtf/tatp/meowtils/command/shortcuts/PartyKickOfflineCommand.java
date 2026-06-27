package wtf.tatp.meowtils.command.shortcuts;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PartyKickOfflineCommand extends ClientCommand {

    @Override
    public String getName() {
        return "poffline";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pko");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/party kickoffline");
    }
}