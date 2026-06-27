package wtf.tatp.meowtils.util;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import wtf.tatp.meowtils.mixin.AccessorGuiNewChat;

public class ChatCopyUtil {

    public static IChatComponent getHoveredChatLine() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.ingameGUI == null) return null;

        GuiNewChat chat = mc.ingameGUI.getChatGUI();
        if (!chat.getChatOpen()) return null;

        List<ChatLine> drawnChatLines = ((AccessorGuiNewChat) chat).getDrawnChatLines();
        if (drawnChatLines.isEmpty()) return null;

        int scaleFactor = new ScaledResolution(mc).getScaleFactor();
        float chatScale = chat.getChatScale();
        int chatX = MathHelper.floor_double((Mouse.getX() / scaleFactor - 3) / chatScale);
        int chatY = MathHelper.floor_double((Mouse.getY() / scaleFactor - 27) / chatScale);

        if (chatX < 0 || chatY < 0) return null;

        int visibleLines = Math.min(chat.getLineCount(), drawnChatLines.size());
        int maxWidth = MathHelper.floor_double(chat.getChatWidth() / chatScale);
        int maxHeight = mc.fontRendererObj.FONT_HEIGHT * visibleLines + visibleLines;

        if (chatX > maxWidth || chatY >= maxHeight) return null;

        int lineIndex = chatY / mc.fontRendererObj.FONT_HEIGHT + ((AccessorGuiNewChat) chat).getScrollPos();
        if (lineIndex < 0 || lineIndex >= drawnChatLines.size()) return null;

        ChatLine chatLine = drawnChatLines.get(lineIndex);
        return (chatLine == null) ? null : chatLine.getChatComponent();
    }
}