package wtf.tatp.meowtils.command.playcommands.playduels;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ClassicDuels extends ClientCommand {

    @Override
    public String getName() {
        return "classic";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play duels_classic_duel");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "classic duels" + EnumChatFormatting.GREEN + " game.");
    }
}