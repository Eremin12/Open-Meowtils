package wtf.tatp.meowtils.command.stats;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.stats.util.ChatStats;

public class StatsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "s";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("meowstat", "meowstats");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();

        if (args.length == 0) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /s <sw|bw> <player>");
            return;
        }

        String player = (args.length == 1) ? mc.thePlayer.getName() : args[1];

        if (args[0].equalsIgnoreCase("sw") || args[0].equalsIgnoreCase("skywars")) {
            ChatStats.showSkywarsStats(player, true, false);
        }

        if (args[0].equalsIgnoreCase("bw") || args[0].equalsIgnoreCase("bedwars")) {
            ChatStats.showBedwarsStats(player, true, false);
        }
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}