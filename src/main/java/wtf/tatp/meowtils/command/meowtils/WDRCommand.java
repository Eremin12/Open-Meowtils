package wtf.tatp.meowtils.command.meowtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.lists.BlacklistManager;
import wtf.tatp.meowtils.module.antisnipe.AutoBlacklist;

public class WDRCommand extends ClientCommand {

    @Override
    public String getName() {
        return "wdr";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("watchdogreport");
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length != 0) {
            AutoBlacklist a = Module.get(AutoBlacklist.class);

            String msg = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
            String reasons = (args.length > 1) ? BlacklistManager.formatReasons(Arrays.copyOfRange(args, 1, args.length)) : "cheating";
            String player = args[0];

            Meowtils.sendCleanMessage("/wdr " + msg);

            if (a != null && a.forReports && a.whenWdrCommand) {
                AutoBlacklist.blacklistPlayer(player, reasons);
            }
        } else {
            Meowtils.sendCleanMessage("/wdr");
        }
    }
}