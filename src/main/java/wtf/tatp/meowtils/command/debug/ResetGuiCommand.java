package wtf.tatp.meowtils.command.debug;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.config.GuiConfig;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class ResetGuiCommand extends ClientCommand {

    @Override
    public String getName() {
        return "resetgui";
    }

    @Override
    public void process(String[] args) throws CommandException {
        GuiConfig s = ConfigManager.guiConfig;

        if (args.length == 0) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Usage: /resetgui <normal|side>");
            return;
        }

        if (args[0].equalsIgnoreCase("normal")) {
            s.meowtilsCategoryX = 5;
            s.meowtilsCategoryY = 5;
            s.hypixelCategoryX = 90;
            s.hypixelCategoryY = 5;
            s.skywarsCategoryX = 175;
            s.skywarsCategoryY = 5;
            s.bedwarsCategoryX = 260;
            s.bedwarsCategoryY = 5;
            s.renderCategoryX = 345;
            s.renderCategoryY = 5;
            s.antisnipeCategoryX = 430;
            s.antisnipeCategoryY = 5;
            s.utilityCategoryX = 515;
            s.utilityCategoryY = 5;
            s.advancedCategoryX = 5;
            s.advancedCategoryY = 200;
            s.extensionCategoryX = 90;
            s.extensionCategoryY = 200;
        } else if (args[0].equalsIgnoreCase("side")) {
            s.meowtilsCategoryX = 5;
            s.meowtilsCategoryY = 5;
            s.hypixelCategoryX = 5;
            s.hypixelCategoryY = 25;
            s.skywarsCategoryX = 5;
            s.skywarsCategoryY = 45;
            s.bedwarsCategoryX = 5;
            s.bedwarsCategoryY = 65;
            s.renderCategoryX = 5;
            s.renderCategoryY = 85;
            s.antisnipeCategoryX = 5;
            s.antisnipeCategoryY = 105;
            s.utilityCategoryX = 5;
            s.utilityCategoryY = 125;
            s.advancedCategoryX = 5;
            s.advancedCategoryY = 145;
            s.extensionCategoryX = 5;
            s.extensionCategoryY = 165;
        } else {
            Meowtils.addMessage(EnumChatFormatting.RED + "Unknown type.");
            return;
        }

        s.meowtilsCategoryExpanded = false;
        s.hypixelCategoryExpanded = false;
        s.skywarsCategoryExpanded = false;
        s.bedwarsCategoryExpanded = false;
        s.renderCategoryExpanded = false;
        s.antisnipeCategoryExpanded = false;
        s.utilityCategoryExpanded = false;
        s.advancedCategoryExpanded = false;
        s.extensionCategoryExpanded = false;

        ConfigManager.save();

        Meowtils.getClickGUI().rebuildFrames();
        Meowtils.addMessage("Reset GUI positions.");
    }
}