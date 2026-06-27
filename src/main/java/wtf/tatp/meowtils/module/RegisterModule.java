package wtf.tatp.meowtils.module;


import wtf.tatp.meowtils.gui.ModuleManager;
import wtf.tatp.meowtils.module.advanced.*;
import wtf.tatp.meowtils.module.antisnipe.*;
import wtf.tatp.meowtils.module.bedwars.*;
import wtf.tatp.meowtils.module.hypixel.*;
import wtf.tatp.meowtils.module.meowtils.*;
import wtf.tatp.meowtils.module.render.*;
import wtf.tatp.meowtils.module.skywars.*;
import wtf.tatp.meowtils.module.utility.*;

public class RegisterModule {

    public static void init() {
        ModuleManager.register(
                new GUI(),
                new AntiCheat(),
                new AntiObfuscate(),
                new ArmorAlerts(),
                new AutoChannel(),
                new AutoSafelist(),
                new AutoTip(),
                new AutoWho(),
                new BreakProgress(),
                new Cape(),
                new ConsumeTimer(),
                new Denicker(),
                new HealthDisplay(),
                new ItemAlerts(),
                new ItemHighlight(),
                new LatencyAlerts(),
                new Notifications(),
                new NoTitles(),
                new NullMove(),
                new ConsumeAlerts(),
                new ResourceTracker(),
                new ShinyPots(),
                new Sprint(),
                new Stats(),
                new StrengthESP(),
                new UpgradeAlerts(),
                new ViewClip(),
                new SkywarsAlerts(),
                new MiningAlerts(),
                new CooldownHUD(),
                new AutoText(),
                new AutoChest(),
                new NoArmorDye(),
                new EquipAlerts(),
                new ChestESP(),
                new Settings(),
                new PotionHUD(),
                new HotbarLock(),
                new PearlDetector(),
                new AutoSwap(),
                new BlockCount(),
                new BedTracker(),
                new InstantHurt(),
                new DelayRemover(),
                new AutoStairs(),
                new AntiInvis(),
                new TrapNotifier(),
                new ActionSounds(),
                new Teams(),
                new PartyNotifier(),
                new Icons(),
                new SniperWarning(),
                new Animations(),
                new AntiMisplace(),
                new AutoGG(),
                new Requeue(),
                new ItemESP(),
                new Indicators(),
                new AccountHider(),
                new SessionStats(),
                new EventTimers(),
                new TimeChanger(),
                new BedESP(),
                new HeightOverlay(),
                new ShopHelper(),
                new NoParticles(),
                new UpgradeHUD(),
                new DamageTags(),
                new ItemScale(),
                new MurdererFinder(),
                new Freelook(),
                new HealthESP(),
                new PartyDetector(),
                new NickBot(),
                new PingHUD(),
                new GhostHand(),
                new ChatFilter(),
                new AutoBlacklist()
        );
    }
}