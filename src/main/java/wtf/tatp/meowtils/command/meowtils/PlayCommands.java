package wtf.tatp.meowtils.command.meowtils;

import net.minecraft.command.CommandException;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.handler.command.ClientCommand;

public class PlayCommands extends ClientCommand {

    private static final String LINE = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "---------------------------------------------------";
    private static final String SEPARATOR = EnumChatFormatting.DARK_GRAY + " » ";

    @Override
    public String getName() {
        return "playcommands";
    }

    @Override
    public void process(String[] args) throws CommandException {
        Meowtils.addCleanMessage(LINE);

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Bedwars:");
        text("/1s or /bw1", "/play bedwars_eight_one");
        text("/2s or /bw2", "/play bedwars_eight_two");
        text("/3s or /bw3", "/play bedwars_four_three");
        text("/4s or /bw4", "/play bedwars_four_four");
        text("/4v4 or /bw4v4", "/play bedwars_four_two");

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Skywars:");
        text("/sw", "/play solo_normal");
        text("/sw2", "/play teams_normal");
        text("/si", "/play solo_insane");
        text("/sm", "/play mini_normal");

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Mega Walls:");
        text("/mw", "/play mw_standard");
        text("/mwfaceoff", "/play mw_face_off");

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Blitz:");
        text("/blitz1", "/play blitz_solo_normal");
        text("/blitz2", "/play blitz_teams_normal");

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "TNT Games:");
        text("/tntrun", "/play tnt_tntrun");
        text("/tnttag", "/play tnt_tntag");

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Duels:");
        text("/classic", "/play duels_classic_duel");
        text("/swduel", "/play duels_sw_duel");
        text("/uhcduel", "/play duels_uhc_duel");
        text("/sumo", "/play duels_sumo_duel");
        text("/bridge1", "/play duels_bridge_duel");
        text("/bridge2", "/play duels_bridge_doubles");
        text("/bridge3", "/play duels_bridge_threes");
        text("/bridge4", "/play duels_bridge_four");
        text("/blitz", "/play duels_blitz_duel");

        Meowtils.addCleanMessage(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "Murder Mystery:");
        text("/mm", "/play murder_classic");
        text("/mmd", "/play murder_double_up");
        text("/mma", "/play murder_assasins");
        text("/mmi", "/play murder_infection");

        Meowtils.addCleanMessage(LINE);
    }

    private static void text(String command, String replacement) {
        Meowtils.addCleanMessage(EnumChatFormatting.YELLOW + command + SEPARATOR + EnumChatFormatting.GRAY + replacement);
    }
}