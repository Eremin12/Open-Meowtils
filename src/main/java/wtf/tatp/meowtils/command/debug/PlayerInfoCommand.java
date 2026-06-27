package wtf.tatp.meowtils.command.debug;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PlayerInfoCommand extends ClientCommand {

    @Override
    public String getName() {
        return "playerinfo";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("info", "pi");
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        String playerName = (args.length == 0) ? mc.thePlayer.getName() : args[0];
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(playerName);

        if (player == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Player not found in world.");
            return;
        }

        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
        if (info == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Could not retrieve tablist info for " + playerName);
            return;
        }

        Meowtils.addMessage(EnumChatFormatting.GOLD + "Player Info: " + player.getName());

        // Basic Info
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Name: " + EnumChatFormatting.WHITE + player.getName());
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "UUID: " + EnumChatFormatting.WHITE + player.getUniqueID());
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Existed: " + EnumChatFormatting.WHITE + player.ticksExisted + " ticks");

        // Skin/Cape
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Skin Texture: " + EnumChatFormatting.WHITE + info.getSkinType());
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Cape Texture: " + EnumChatFormatting.WHITE + info.hasLocationSkin());

        // Position/Rotation
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Position: " + EnumChatFormatting.WHITE +
                String.format("%.2f, %.2f, %.2f", player.posX, player.posY, player.posZ));
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Rotation: " + EnumChatFormatting.WHITE +
                String.format("%.2f, %.2f", player.rotationYaw, player.rotationPitch));

        // State
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "On Ground: " + state(player.onGround));
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Sneaking: " + state(player.isSneaking()));

        // Health
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Health: " + EnumChatFormatting.WHITE + player.getHealth());
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Absorption: " + EnumChatFormatting.WHITE + player.getAbsorptionAmount());
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Dead: " + state(player.isDead));

        // Held Item
        ItemStack held = player.getHeldItem();
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Held Item: " + EnumChatFormatting.WHITE + ((held != null) ? held.getDisplayName() : "None"));
        if (held != null && held.hasTagCompound()) {
            Meowtils.addMessage(EnumChatFormatting.GRAY + "  NBT: " + held.getTagCompound().toString());
        }

        // Armor
        for (ItemStack armorPiece : player.inventory.armorInventory) {
            if (armorPiece != null) {
                Meowtils.addMessage(EnumChatFormatting.YELLOW + "Armor: " + EnumChatFormatting.WHITE + armorPiece.getDisplayName());
                if (armorPiece.hasTagCompound()) {
                    Meowtils.addMessage(EnumChatFormatting.GRAY + "  NBT: " + armorPiece.getTagCompound().toString());
                }
            }
        }

        // Network Info
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Ping: " + EnumChatFormatting.WHITE + info.getResponseTime() + "ms");

        // Display Name
        IChatComponent displayName = info.getDisplayName();
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Scoreboard Name: " + EnumChatFormatting.WHITE + ((displayName != null) ? displayName.getUnformattedText() : player.getName()));

        // Team
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Team: " + EnumChatFormatting.WHITE + player.getTeam());

        // Gamemode
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Gamemode: " + EnumChatFormatting.WHITE + info.getGameType());

        // Tablist Visible
        Meowtils.addMessage(EnumChatFormatting.YELLOW + "Tablist Visible: " + state(info.getGameProfile().isComplete()));
    }

    @Override
    public boolean tabCompleteNames() {
        return true;
    }

    private String state(boolean value) {
        return value ? (EnumChatFormatting.GREEN + "true") : (EnumChatFormatting.RED + "false");
    }
}