package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.LatencyHandler;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PingCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowping";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Ping command only works in multiplayer!");
            return;
        }

        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();

        LatencyHandler.ping(serverData, ping -> Meowtils.addMessage("Ping: " + LatencyHandler.getLatencyColor(ping) + ping + "ms"));
    }
}