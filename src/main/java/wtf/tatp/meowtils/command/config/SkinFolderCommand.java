package wtf.tatp.meowtils.command.config;

import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.util.Util;

public class SkinFolderCommand extends ClientCommand {

    @Override
    public String getName() {
        return "skinfolder";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Util.openFolder(Meowtils.CUSTOM_SKIN_DIR, "skin");
    }
}