package wtf.tatp.meowtils.command.playcommands.playskywars;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class DoublesNormalSkywarsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "sw2";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play teams_normal");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "team skywars" + EnumChatFormatting.GREEN + " game.");
    }
}