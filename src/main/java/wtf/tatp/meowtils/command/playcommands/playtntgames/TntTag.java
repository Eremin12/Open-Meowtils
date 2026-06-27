package wtf.tatp.meowtils.command.playcommands.playtntgames;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class TntTag extends ClientCommand {

    @Override
    public String getName() {
        return "tnttag";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play tnt_tntag");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "tnt tag" + EnumChatFormatting.GREEN + " game.");
    }
}