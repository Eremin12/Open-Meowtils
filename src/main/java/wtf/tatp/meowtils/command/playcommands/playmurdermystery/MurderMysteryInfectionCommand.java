package wtf.tatp.meowtils.command.playcommands.playmurdermystery;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class MurderMysteryInfectionCommand extends ClientCommand {

    @Override
    public String getName() {
        return "mmi";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play murder_infection");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "murder mystery infection" + EnumChatFormatting.GREEN + " game.");
    }
}