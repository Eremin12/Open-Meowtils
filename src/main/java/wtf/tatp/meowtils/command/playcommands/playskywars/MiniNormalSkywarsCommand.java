package wtf.tatp.meowtils.command.playcommands.playskywars;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class MiniNormalSkywarsCommand extends ClientCommand {

    @Override
    public String getName() {
        return "sm";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("mini");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.sendCleanMessage("/play mini_normal");
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Sending you to a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.ITALIC + "mini skywars" + EnumChatFormatting.GREEN + " game.");
    }
}