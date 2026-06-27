package wtf.tatp.meowtils.command.playcommands.playblitz;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class BlitzTeams extends ClientCommand {

    @Override
    public String getName() {
        return "blitz2";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play blitz_teams_normal");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "blitz teams" + EnumChatFormatting.GREEN + " game.");
    }
}