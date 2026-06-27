package wtf.tatp.meowtils.command.config;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.hypixel.Stats;

public class ApiKeyCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowapi";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("meowapikey", "meowtilsapi");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length == 0) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /meowapi <key>");
            return;
        }

        Stats stats = Module.get(Stats.class);
        if (stats == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Stats module not found.");
            return;
        }

        stats.apiKey = String.join(" ", args);
        ConfigManager.save();
        Meowtils.addMessage("Set API key.");
    }
}