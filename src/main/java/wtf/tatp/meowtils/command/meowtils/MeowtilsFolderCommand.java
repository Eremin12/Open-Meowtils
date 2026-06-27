package wtf.tatp.meowtils.command.meowtils;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.util.Util;

public class MeowtilsFolderCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowtilsfolder";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("meowfolder");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Util.openFolder(Meowtils.MEOWTILS_DIR, "Meowtils");
    }
}