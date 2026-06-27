package wtf.tatp.meowtils.command.config.customthemecolor;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.module.meowtils.Settings;

public class SetThemeCommand extends ClientCommand {

    @Override
    public String getName() {
        return "settheme";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length != 2) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /settheme <m|e|o|w|[|]> <COLOR_NAME>");
            return;
        }

        String letter = args[0].toLowerCase();
        String colorName = args[1].toUpperCase();

        try {
            EnumChatFormatting.valueOf(colorName);
            Settings s = Module.get(Settings.class);

            if (s == null) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Settings module not found.");
                return;
            }

            switch (letter) {
                case "m":
                    s.themeM = colorName;
                    break;
                case "e":
                    s.themeE = colorName;
                    break;
                case "o":
                    s.themeO = colorName;
                    break;
                case "w":
                    s.themeW = colorName;
                    break;
                case "[":
                    s.themeFirstBracket = colorName;
                    break;
                case "]":
                    s.themeSecondBracket = colorName;
                    break;
                default:
                    Meowtils.addMessage(EnumChatFormatting.RED + "Invalid letter. Use: m, e, o, w, [, ]");
                    return;
            }

            ConfigManager.save();
            Meowtils.addMessage(EnumChatFormatting.GREEN + "Set color for '" + letter.toUpperCase() + "' to " + colorName);
        } catch (IllegalArgumentException e) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Invalid color name.");
        }
    }
}