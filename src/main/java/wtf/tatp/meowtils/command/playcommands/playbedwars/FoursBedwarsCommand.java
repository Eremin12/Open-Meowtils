package wtf.tatp.meowtils.command.playcommands.playbedwars;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class FoursBedwarsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "4s";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("bw4");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play bedwars_four_four");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "fours bedwars" + EnumChatFormatting.GREEN + " game.");
    }
}