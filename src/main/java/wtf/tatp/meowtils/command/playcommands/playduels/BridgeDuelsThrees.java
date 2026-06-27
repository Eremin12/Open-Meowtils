package wtf.tatp.meowtils.command.playcommands.playduels;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class BridgeDuelsThrees extends ClientCommand {

    @Override
    public String getName() {
        return "bridge3";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play duels_bridge_threes");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "bridge 3v3 duels" + EnumChatFormatting.GREEN + " game.");
    }
}