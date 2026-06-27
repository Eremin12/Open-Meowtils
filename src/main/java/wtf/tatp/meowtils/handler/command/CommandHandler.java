package wtf.tatp.meowtils.handler.command;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.command.CommandException;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.tatp.meowtils.event.SendPacketEvent;
import wtf.tatp.meowtils.event.api.EventTarget;

public class CommandHandler {

    private static final Map<String, ClientCommand> COMMANDS = new HashMap<>();
    private static boolean processingCommand = false;

    public static void register(ClientCommand command) {
        COMMANDS.put(command.getName(), command);

        for (String alias : command.getAliases()) {
            COMMANDS.put(alias, command);
        }
    }

    public static ClientCommand getCommand(String name) {
        return COMMANDS.get(name);
    }

    @EventTarget
    public void onPacketSend(SendPacketEvent event) {
        if (processingCommand) return;

        if (event.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
            String msg = packet.getMessage();

            if (!msg.startsWith("/")) return;

            String[] parts = msg.substring(1).split(" ", 2);

            String commandName = parts[0].toLowerCase();
            ClientCommand command = getCommand(commandName);
            if (command == null) return;

            event.setCancelled(true);
            processingCommand = true;

            try {
                command.process((parts.length > 1) ? parts[1].split(" ") : new String[0]);
            } catch (CommandException e) {
                e.printStackTrace();
            } finally {
                processingCommand = false;
            }
        }
    }
}