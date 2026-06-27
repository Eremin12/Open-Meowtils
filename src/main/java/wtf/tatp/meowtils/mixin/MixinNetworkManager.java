package wtf.tatp.meowtils.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ReceivePacketEvent;
import wtf.tatp.meowtils.event.SendPacketEvent;
import wtf.tatp.meowtils.event.api.EventManager;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void meowtils$channelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        ReceivePacketEvent receiveEvent = new ReceivePacketEvent(packet);
        EventManager.post(receiveEvent);

        if (receiveEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        if (packet instanceof S02PacketChat) {
            S02PacketChat chat = (S02PacketChat) packet;
            ChatReceivedEvent chatEvent = new ChatReceivedEvent(chat.getChatComponent(), chat.isChat());
            EventManager.post(chatEvent);

            if (chatEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void meowtils$sendPacket(Packet<?> packet, CallbackInfo ci) {
        SendPacketEvent event = new SendPacketEvent(packet);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}