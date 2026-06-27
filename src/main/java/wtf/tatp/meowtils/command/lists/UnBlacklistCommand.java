package wtf.tatp.meowtils.command.lists;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.lists.BlacklistManager;
import wtf.tatp.meowtils.util.MojangNameToUUID;
import wtf.tatp.meowtils.util.NameUtil;

public class UnBlacklistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "unblacklist";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ubl", "blr", "blacklistremove", "unbl");
    }
    //这里是黑名单的列表查询指令 backlist <PlayerName>
    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /unblacklist <player>");
            return;
        }

        String playerName = args[0];

        MojangNameToUUID.lookup(playerName, uuid -> {
            if (uuid != null && BlacklistManager.isBlacklisted(uuid)) {
                BlacklistManager.remove(uuid);
                Meowtils.addMessage(EnumChatFormatting.YELLOW + "Removed " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.YELLOW + " from the blacklist.");
            } else if (BlacklistManager.isBlacklisted(playerName)) {
                BlacklistManager.remove(playerName);
                Meowtils.addMessage(EnumChatFormatting.YELLOW + "Removed " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.YELLOW + " from the blacklist.");
            } else {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + " is not blacklisted.");
            }
        });
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}