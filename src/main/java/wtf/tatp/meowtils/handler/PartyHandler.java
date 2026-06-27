package wtf.tatp.meowtils.handler;

import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.module.hypixel.AutoChannel;

public class PartyHandler {

    private static boolean inParty;

    public static boolean inParty() {
        return inParty;
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();
        if (Server.HYPIXEL.isNotActive()) return;
        if (msg.contains(":")) return;

        if (msg.endsWith("has disbanded the party!")) {
            inParty = false;
            AutoChannel.swapToAll();
        }

        if (msg.equals("The party was disbanded because all invites expired and the party was empty.")) {
            inParty = false;
            AutoChannel.swapToAll();
        }

        if (msg.startsWith("You have joined") && msg.endsWith("party!")) {
            inParty = true;
            AutoChannel.swapToParty();
        }

        if (msg.endsWith("joined the party.")) {
            inParty = true;
            AutoChannel.swapToParty();
        }

        if (msg.equals("You left the party.")) {
            inParty = false;
            AutoChannel.swapToAll();
        }

        if (msg.startsWith("You have been kicked from the party")) {
            inParty = false;
            AutoChannel.swapToAll();
        }

        if (msg.equals("You are not in a party right now.")) {
            inParty = false;
            AutoChannel.swapToAll();
        }
    }
}