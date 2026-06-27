package wtf.tatp.meowtils.command.playcommands.playduels;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class BlitzDuels extends ClientCommand {

    @Override
    public String getName() {
        return "blitz";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play duels_blitz_duel");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "blitz duels" + EnumChatFormatting.GREEN + " game.");
    }
}