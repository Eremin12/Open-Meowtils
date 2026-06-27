package wtf.tatp.meowtils.command.playcommands.playduels;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class BridgeDuelsTwos extends ClientCommand {

    @Override
    public String getName() {
        return "bridge2";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play duels_bridge_doubles");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "bridge 2v2 duels" + EnumChatFormatting.GREEN + " game.");
    }
}