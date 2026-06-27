package wtf.tatp.meowtils.command.stats;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class UrchinTagsCommand extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "---------------------------------------------------";
    private static final String ARROW = EnumChatFormatting.BLUE + " » ";

    @Override
    public String getName() {
        return "urchintags";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("utags", "utag");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.addCleanMessage(LINE);
        Meowtils.addCleanMessage(EnumChatFormatting.DARK_RED + "✹" + ARROW + EnumChatFormatting.DARK_RED + "Blatant Cheater");
        Meowtils.addCleanMessage(EnumChatFormatting.DARK_PURPLE + "✹" + ARROW + EnumChatFormatting.DARK_PURPLE + "Confirmed Cheater");
        Meowtils.addCleanMessage(EnumChatFormatting.YELLOW + "✴" + ARROW + EnumChatFormatting.YELLOW + "Closet Cheater");
        Meowtils.addCleanMessage(EnumChatFormatting.RED + "✹" + ARROW + EnumChatFormatting.RED + "Sniper");
        Meowtils.addCleanMessage(EnumChatFormatting.YELLOW + "ⓘ" + ARROW + EnumChatFormatting.YELLOW + "Caution");
        Meowtils.addCleanMessage(EnumChatFormatting.GRAY + "✹" + ARROW + EnumChatFormatting.GRAY + "Info");
        Meowtils.addCleanMessage(EnumChatFormatting.DARK_GRAY + "✹" + ARROW + EnumChatFormatting.DARK_GRAY + "Account");
        Meowtils.addCleanMessage(LINE);
    }
}