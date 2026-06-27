package wtf.tatp.meowtils.command.lists;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.skywars.ItemHighlight;

public class ItemBlacklistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "itemblacklist";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("itembl");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /itemblacklist <example_item>");
            return;
        }

        String item = args[0];

        if (ItemHighlight.isBlacklisted(item)) {
            Meowtils.addMessage(EnumChatFormatting.GRAY + "This item is already " + EnumChatFormatting.DARK_RED + "blacklisted" + EnumChatFormatting.GRAY + "!");
            return;
        }

        ItemHighlight.addBlacklistItem(item);
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC + item + EnumChatFormatting.GREEN + " to the item " + EnumChatFormatting.DARK_RED + "blacklist" + EnumChatFormatting.GREEN + "!");
    }
}