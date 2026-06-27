package wtf.tatp.meowtils.command.meowtils;

import java.util.Arrays;
import net.minecraft.command.CommandException;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.handler.command.ClientCommand;
import wtf.tatp.meowtils.manager.lists.BlacklistManager;
import wtf.tatp.meowtils.module.antisnipe.AutoBlacklist;

public class ReportCommand extends ClientCommand {

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public void process(String[] args) throws CommandException {
        if (args.length != 0) {
            AutoBlacklist a = Module.get(AutoBlacklist.class);

            String msg = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
            String reasons = (args.length > 1) ? BlacklistManager.formatReasons(Arrays.copyOfRange(args, 1, args.length)) : "cheating";
            String player = args[0];

            Meowtils.sendCleanMessage("/report " + msg);

            if (a != null && a.forReports && a.whenReportCommand) {
                AutoBlacklist.blacklistPlayer(player, reasons);
            }
        } else {
            Meowtils.sendCleanMessage("/report");
        }
    }
}