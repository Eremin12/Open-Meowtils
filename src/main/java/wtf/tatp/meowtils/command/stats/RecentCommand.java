package wtf.tatp.meowtils.command.stats;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.stats.util.ChatStats;

public class RecentCommand extends ClientCommand {

    @Override
    public String getName() {
        return "recent";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("recentgames", "recentgame");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        String player = (args.length == 0) ? mc.thePlayer.getName() : args[0];

        ChatStats.showRecentGames(player);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}