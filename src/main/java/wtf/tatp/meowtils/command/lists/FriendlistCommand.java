package wtf.tatp.meowtils.command.lists;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.lists.FriendlistManager;
import wtf.tatp.meowtils.util.MojangNameToUUID;
import wtf.tatp.meowtils.util.NameUtil;

public class FriendlistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowfriend";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("mf");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length == 0) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /meowfriend <player>");
            return;
        }

        // /meowfriend info <player> - 查询玩家好友状态
        if (args[0].equalsIgnoreCase("info")) {
            if (args.length < 2) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /meowfriend info <player>");
                return;
            }
            String playerName = args[1];

            MojangNameToUUID.lookup(playerName, uuid -> {
                if (uuid != null && FriendlistManager.isFriendlisted(uuid)) {
                    Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GOLD + " is in your friend list.");
                } else if (FriendlistManager.isFriendlisted(playerName)) {
                    Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GOLD + " is in your friend list.");
                } else {
                    Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.RED + " is not in your friend list.");
                }
            });
            return;
        }

        // /meowfriend <player> - 添加好友
        String playerName = args[0];

        MojangNameToUUID.lookup(playerName, uuid -> {
            if (uuid != null && FriendlistManager.isFriendlisted(uuid)) {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GOLD + " is already in your friend list.");
            } else if (FriendlistManager.isFriendlisted(playerName)) {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GOLD + " is already in your friend list by name.");
            } else if (uuid != null) {
                FriendlistManager.add(uuid);
                Meowtils.addMessage(EnumChatFormatting.GOLD + "Added " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GOLD + " to your friend list.");
            } else {
                FriendlistManager.add(playerName);
                Meowtils.addMessage(EnumChatFormatting.GOLD + "Added " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GOLD + " to your friend list by name.");
            }
        });
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}