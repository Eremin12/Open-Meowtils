package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.util.DelayedTask;

public class GuiCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowtilsgui";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();

        new DelayedTask(() -> {
            mc.displayGuiScreen(Meowtils.getClickGUI());
            Meowtils.addMessage("Opened GUI! " + EnumChatFormatting.GREEN + "Make sure to bind GUI by middle clicking the \"" + EnumChatFormatting.YELLOW + "GUI" + EnumChatFormatting.GREEN + "\" module.");
        }, 5);
    }
}