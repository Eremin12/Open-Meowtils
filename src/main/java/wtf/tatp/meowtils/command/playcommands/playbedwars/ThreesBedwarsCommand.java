package wtf.tatp.meowtils.command.playcommands.playbedwars;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ThreesBedwarsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "3s";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("bw3");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play bedwars_four_three");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "threes bedwars" + EnumChatFormatting.GREEN + " game.");
    }
}