package wtf.tatp.meowtils.command.playcommands.playmurdermystery;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class MurderMysteryClassicCommand extends ClientCommand {

    @Override
    public String getName() {
        return "mm";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play murder_classic");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "murder mystery" + EnumChatFormatting.GREEN + " game.");
    }
}