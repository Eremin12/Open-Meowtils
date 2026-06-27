package wtf.tatp.meowtils.command.debug;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.session.SessionManager;
import wtf.tatp.meowtils.module.meowtils.GUI;

public class DebugModeCommand extends ClientCommand {

    @Override
    public String getName() {
        return "meowdebug";
    }

    @Override
    public void process(String[] args) throws CommandException {
        GUI s = Module.get(GUI.class);

        if (s == null) {
            Meowtils.addMessage(EnumChatFormatting.RED + "GUI module not found.");
            return;
        }

        s.debugMode = !s.debugMode;

        String toggleMessage = s.debugMode ? (EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD + "ON") : (EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "OFF");

        if (s.debugMode) {
            SessionManager.hypixel = true;
            SessionManager.bedwarsGame = true;
            SessionManager.skywarsGame = true;
        } else {
            SessionManager.hypixel = false;
            SessionManager.bedwarsGame = false;
            SessionManager.skywarsGame = false;
        }

        ConfigManager.save();
        Meowtils.addMessage("Debug Mode: " + toggleMessage);
    }
}