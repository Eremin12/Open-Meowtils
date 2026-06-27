package wtf.tatp.meowtils.command.playcommands.playbedwars;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class FourTwosBedwarsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "4v4";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("bw4v4");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play bedwars_two_four");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "4v4 bedwars" + EnumChatFormatting.GREEN + " game.");
    }
}