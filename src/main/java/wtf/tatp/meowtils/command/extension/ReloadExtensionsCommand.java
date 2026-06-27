package wtf.tatp.meowtils.command.extension;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.extension.ExtensionManager;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ReloadExtensionsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reloadextensions", "reloadextension");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.addMessage("Reloading extensions...");
        try {
            ExtensionManager.reload();
        } catch (Throwable t) {
            Meowtils.error("Extension reload failed: " + t.getMessage());
            Meowtils.addMessage(EnumChatFormatting.RED + "An error occurred while reloading extensions. Check logs for details.");
            t.printStackTrace();
        }
    }
}