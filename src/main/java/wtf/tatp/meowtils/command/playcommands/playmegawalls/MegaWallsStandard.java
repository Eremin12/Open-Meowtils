package wtf.tatp.meowtils.command.playcommands.playmegawalls;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class MegaWallsStandard extends ClientCommand {

    @Override
    public String getName() {
        return "mw";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play mw_standard");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "mega walls" + EnumChatFormatting.GREEN + " game.");
    }
}