package wtf.tatp.meowtils.command.playcommands.playmurdermystery;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class MurderMysteryDoubleCommand extends ClientCommand {

    @Override
    public String getName() {
        return "mmd";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play murder_double_up");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "murder mystery double up" + EnumChatFormatting.GREEN + " game.");
    }
}