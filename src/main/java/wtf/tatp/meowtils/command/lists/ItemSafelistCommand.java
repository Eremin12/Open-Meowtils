package wtf.tatp.meowtils.command.lists;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.skywars.ItemHighlight;

public class ItemSafelistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "itemsafelist";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("itemsl");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /itemsafelist <example_item>");
            return;
        }

        String item = args[0];

        if (ItemHighlight.isSafelisted(item)) {
            Meowtils.addMessage(EnumChatFormatting.GRAY + "This item is already " + EnumChatFormatting.DARK_GREEN + "safelisted" + EnumChatFormatting.GRAY + "!");
            return;
        }

        ItemHighlight.addSafelistItem(item);
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Added " + EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC + item + EnumChatFormatting.GREEN + " to the item " + EnumChatFormatting.DARK_GREEN + "safelist" + EnumChatFormatting.GREEN + "!");
    }
}