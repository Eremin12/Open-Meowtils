package wtf.tatp.meowtils.command.extension;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.util.Util;

public class ExtensionFolderCommand extends ClientCommand {

    @Override
    public String getName() {
        return "extension";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("extensions", "extensionfolder");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.addMessage(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "ONLY USE EXTENSIONS FROM TRUSTED SOURCES");
        Util.openFolder(Meowtils.EXTENSION_DIR, "extension");
    }
}