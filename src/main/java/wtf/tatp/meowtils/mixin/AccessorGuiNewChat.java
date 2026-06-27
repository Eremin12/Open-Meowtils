package wtf.tatp.meowtils.mixin;

import java.util.List;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiNewChat.class)
public interface AccessorGuiNewChat {

  @Accessor("drawnChatLines")
  List<ChatLine> getDrawnChatLines();

  @Accessor("scrollPos")
  int getScrollPos();
}