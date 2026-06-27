package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ShortcutCommands extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "---------------------------------------------------";
    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " » ";

    @Override
    public String getName() {
        return "shortcuts";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.addCleanMessage(LINE);

        text("/renick", "/nick reuse");
        text("/away", "/status away");
        text("/busy", "/status busy");
        text("/offline", "/status offline");
        text("/online", "/status online");
        text("/block", "/block add");
        text("/unblock", "/block remove");
        text("/ca", "/chat all");
        text("/cc", "/chat coop");
        text("/cg", "/chat guild");
        text("/co", "/chat officer");
        text("/cp", "/chat party");
        text("/gi", "/guild info");
        text("/glo", "/guild online");
        text("/gt", "/guild toggle");
        text("/pd", "/party demote");
        text("/disband", "/party disband");
        text("/poffline", "/party kickoffline");
        text("/pp", "/party promote");
        text("/psa", "/party settings allinvite");
        text("/pt", "/party transfer");
        text("/rp", "/replay");
        text("/sh", "/shout");
        text("/slb", "/swaplobby");
        text("/ta", "/tip all");
        text("/rj", "/rejoin");
        text("/psp", "/p settings private");

        Meowtils.addCleanMessage(LINE);
    }

    private static void text(String command, String replacement) {
        Meowtils.addCleanMessage(EnumChatFormatting.YELLOW + command + SEPARATOR + EnumChatFormatting.GRAY + replacement);
    }
}