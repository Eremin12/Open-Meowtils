package wtf.tatp.meowtils.command.lists;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.lists.SafelistManager;
import wtf.tatp.meowtils.util.MojangNameToUUID;
import wtf.tatp.meowtils.util.NameUtil;

public class UnSafelistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "unsafelist";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("usl", "slr", "safelistremove", "unsl");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /unsafelist <player>");
            return;
        }

        String playerName = args[0];

        MojangNameToUUID.lookup(playerName, uuid -> {
            if (uuid != null && SafelistManager.isSafelisted(uuid)) {
                SafelistManager.remove(uuid);
                Meowtils.addMessage(EnumChatFormatting.YELLOW + "Removed " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.YELLOW + " from the safelist.");
            } else if (SafelistManager.isSafelisted(playerName)) {
                SafelistManager.remove(playerName);
                Meowtils.addMessage(EnumChatFormatting.YELLOW + "Removed " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.YELLOW + " from the safelist.");
            } else {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.RED + " is not in the safelist.");
            }
        });
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}