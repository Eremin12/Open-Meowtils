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

public class UnFriendlistCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowunfriend";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("muf");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length < 1) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /meowunfriend <player>");
            return;
        }

        String playerName = args[0];

        MojangNameToUUID.lookup(playerName, uuid -> {
            if (uuid != null && FriendlistManager.isFriendlisted(uuid)) {
                FriendlistManager.remove(uuid);
                Meowtils.addMessage(EnumChatFormatting.GRAY + "Removed " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GRAY + " from your friend list.");
            } else if (FriendlistManager.isFriendlisted(playerName)) {
                FriendlistManager.remove(playerName);
                Meowtils.addMessage(EnumChatFormatting.GRAY + "Removed " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GRAY + " from your friend list.");
            } else {
                Meowtils.addMessage(NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.RED + " is not in your friend list.");
            }
        });
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }
}