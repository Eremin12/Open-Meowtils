package wtf.tatp.meowtils.util.anticheat;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.util.anticheat.checks.AutoBlockCheck;
import wtf.tatp.meowtils.util.anticheat.checks.KillauraCheck;
import wtf.tatp.meowtils.util.anticheat.checks.LegitScaffoldCheck;
import wtf.tatp.meowtils.util.anticheat.checks.NoSlowCheck;

public class AntiCheatData {

    public AutoBlockCheck autoBlockCheck = new AutoBlockCheck();
    public NoSlowCheck noSlowCheck = new NoSlowCheck();
    public LegitScaffoldCheck legitScaffoldCheck = new LegitScaffoldCheck();
    public KillauraCheck killauraCheck = new KillauraCheck();

    private UUID lastUuid;

    public void anticheatCheck(EntityPlayer player) {
        this.lastUuid = player.getUniqueID();

        this.autoBlockCheck.anticheatCheck(player);
        this.noSlowCheck.anticheatCheck(player);
        this.legitScaffoldCheck.anticheatCheck(player);
        this.killauraCheck.anticheatCheck(player);
    }

    public boolean failedAutoBlock() {
        return this.autoBlockCheck.failedAutoBlock();
    }

    public boolean failedNoSlow() {
        return this.noSlowCheck.failedNoSlow();
    }

    public boolean failedLegitScaffold() {
        return this.legitScaffoldCheck.failedLegitScaffold(this.lastUuid);
    }

    public boolean failedKillauraB() {
        return this.killauraCheck.failedKillauraB();
    }
}