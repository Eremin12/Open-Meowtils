package wtf.tatp.meowtils.command.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.stats.util.ChatStats;

public class InfoCommand extends ClientCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        String player = (args.length == 0) ? mc.thePlayer.getName() : args[0];

        ChatStats.showInfo(player);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}