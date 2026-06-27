package wtf.tatp.meowtils.command.stats.sites;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.util.Prefix;

public class NamemcCommand extends ClientCommand {

    @Override
    public String getName() {
        return "namemc";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("nmc");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        String player = (args.length == 0) ? mc.thePlayer.getName() : args[0];

        ChatComponentText message = new ChatComponentText(Prefix.getPrefix() + EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + player + "'s profile on NameMC");

        message.setChatStyle(message.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + player))
                .setUnderlined(true));

        mc.thePlayer.addChatMessage(message);
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}