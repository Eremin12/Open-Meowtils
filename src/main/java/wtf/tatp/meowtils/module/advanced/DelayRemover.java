package wtf.tatp.meowtils.module.advanced;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.mixin.AccessorEntityLivingBase;
import wtf.tatp.meowtils.mixin.AccessorMinecraft;
import wtf.tatp.meowtils.mixin.AccessorPlayerControllerMP;

public class DelayRemover extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int breakDelay = 0;
    @Config
    public int useDelay = 1;
    @Config
    public int jumpDelay = 0;
    @Config
    public boolean noBreakDelay = true;
    @Config
    public boolean noUseDelay = true;
    @Config
    public boolean noHitDelay = true;
    @Config
    public boolean noJumpDelay = true;

    private static int useTicks = 0;

    public DelayRemover() {
        super("DelayRemover", Module.Category.Advanced);
        tag(Module.ModuleTag.BLATANT);
        tooltip("Remove or reduce certain delays.\n§6NoBreakDelay §f- Remove or reduce delay between block breaks\n§6NoUseDelay §f- Releases right click after required use duration\n§6NoHitDelay §f- Removes delay if you miss an attack\n§6NoJumpDelay §f- Removes delay between jumps\n§cWARNING: §cSome §cof §cthese §cfeatures §cmay §cbe §cdetectable.");
        addExpand(new ExpandValue("NoBreakDelay", e -> {
            e.addToggle(new ToggleValue("Enabled", "noBreakDelay", this));
            e.addSlider(new SliderValue("Delay", 0.0D, 5.0D, 1.0D, "ticks", "breakDelay", this, int.class));
        }));
        addExpand(new ExpandValue("NoUseDelay", e -> {
            e.addToggle(new ToggleValue("Enabled", "noUseDelay", this));
            e.addSlider(new SliderValue("Delay", 0.0D, 4.0D, 1.0D, "ticks", "useDelay", this, int.class));
        }));
        addExpand(new ExpandValue("NoHitDelay", e -> e.addToggle(new ToggleValue("Enabled", "noHitDelay", this))));
        addExpand(new ExpandValue("NoJumpDelay", e -> {
            e.addToggle(new ToggleValue("Enabled", "noJumpDelay", this));
            e.addSlider(new SliderValue("Delay", 0.0D, 10.0D, 1.0D, "ticks", "jumpDelay", this, int.class));
        }));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) {
            return;
        }

        if (this.mc.playerController != null && this.noBreakDelay) {
            AccessorPlayerControllerMP accessor = (AccessorPlayerControllerMP) this.mc.playerController;
            int delay = this.breakDelay;
            if (accessor.getBlockHitDelay() > delay) {
                accessor.setBlockHitDelay(delay);
            }
        }

        if (this.noUseDelay) {
            ItemStack held = this.mc.thePlayer.getHeldItem();
            boolean validItem = (held != null && (held.getItem() instanceof net.minecraft.item.ItemFood || held.getItem() instanceof net.minecraft.item.ItemPotion));

            boolean holdingUse = this.mc.gameSettings.keyBindUseItem.isKeyDown();

            if (holdingUse && validItem) {
                useTicks++;

                int consumeTicks = 32 + this.useDelay;

                if (useTicks >= consumeTicks) {
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    useTicks = 0;
                }
            } else {
                useTicks = 0;
            }
        }

        if (this.noHitDelay) {
            ((AccessorMinecraft) this.mc).setLeftClickCounter(0);
        }

        if (this.noJumpDelay) {
            ((AccessorEntityLivingBase) this.mc.thePlayer).setJumpTicks(Math.min(((AccessorEntityLivingBase) this.mc.thePlayer).getJumpTicks(), this.jumpDelay));
        }
    }
}