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

public class SafelistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "safelist";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sl");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length == 0) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /safelist <player>");
            return;
        }

        // /safelist info <player> - 查询玩家安全名单状态
        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /safelist info <player>");
                return;
            }
            String playerName = args[1];

            MojangNameToUUID.lookup(playerName, uuid -> {
                if (uuid != null && SafelistManager.isSafelisted(uuid)) {
                    Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + " is safelisted.");
                } else if (SafelistManager.isSafelisted(playerName)) {
                    Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + " is safelisted.");
                } else {
                    Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.RED + " is not in the safelist.");
                }
            });
            return;
        }

        // /safelist <player> - 添加安全名单
        String playerName = args[0];

        MojangNameToUUID.lookup(playerName, uuid -> {
            if (uuid != null && SafelistManager.isSafelisted(uuid)) {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + " is already safelisted.");
            } else if (SafelistManager.isSafelisted(playerName)) {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + " is already safelisted by name.");
            } else if (uuid != null) {
                SafelistManager.add(uuid);
                Meowtils.addMessage(EnumChatFormatting.GREEN + "Safelisted " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + ".");
            } else {
                SafelistManager.add(playerName);
                Meowtils.addMessage(EnumChatFormatting.GREEN + "Safelisted " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GREEN + " by name.");
            }
        });
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}