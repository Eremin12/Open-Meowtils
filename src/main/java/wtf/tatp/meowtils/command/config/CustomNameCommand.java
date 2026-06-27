package wtf.tatp.meowtils.command.config;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.hypixel.AccountHider;

public class CustomNameCommand extends ClientCommand {

    @Override
    public String getName() {
        return "customname";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length == 0) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /customname <text>");
            return;
        }

        AccountHider a = Module.get(AccountHider.class);

        if (a == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "AccountHider module not found.");
            return;
        }

        a.customName = String.join(" ", args);
        ConfigManager.save();
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Set custom name to: " + EnumChatFormatting.RESET + a.customName);
    }
}