package wtf.tatp.meowtils.command.playcommands.playblitz;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class BlitzSolo extends ClientCommand {

    @Override
    public String getName() {
        return "blitz1";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play blitz_solo_normal");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "blitz solo" + EnumChatFormatting.GREEN + " game.");
    }
}