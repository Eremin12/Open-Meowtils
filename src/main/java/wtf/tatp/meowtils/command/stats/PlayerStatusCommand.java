package wtf.tatp.meowtils.command.stats;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.stats.util.ChatStats;

public class PlayerStatusCommand extends ClientCommand {

    @Override
    public String getName() {
        return "playerstatus";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ps", "pstatus");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        String player = (args.length == 0) ? mc.thePlayer.getName() : args[0];

        ChatStats.showStatus(player);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}