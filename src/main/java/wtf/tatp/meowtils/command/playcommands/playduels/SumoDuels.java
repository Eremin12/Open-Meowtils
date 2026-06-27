package wtf.tatp.meowtils.command.playcommands.playduels;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class SumoDuels extends ClientCommand {

    @Override
    public String getName() {
        return "sumo";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play duels_sumo_duel");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "sumo duels" + EnumChatFormatting.GREEN + " game.");
    }
}