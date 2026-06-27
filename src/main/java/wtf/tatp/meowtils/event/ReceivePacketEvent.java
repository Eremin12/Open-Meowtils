package wtf.tatp.meowtils.event;

import net.minecraft.network.Packet;
import wtf.tatp.meowtils.event.api.Event;

public class ReceivePacketEvent extends Event {

    private final Packet<?> packet;

    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}