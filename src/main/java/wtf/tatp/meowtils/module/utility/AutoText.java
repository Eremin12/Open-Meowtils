package wtf.tatp.meowtils.module.utility;

import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class AutoText extends Module {

    @Config
    public boolean enabled = false;

    @Config
    public int key = 0;

    @Config
    public String autoText1 = "";
    @Config
    public String autoText2 = "";
    @Config
    public String autoText3 = "";
    @Config
    public String autoText4 = "";
    @Config
    public String autoText5 = "";
    @Config
    public String autoText6 = "";
    @Config
    public String autoText7 = "";
    @Config
    public String autoText8 = "";
    @Config
    public String autoText9 = "";
    @Config
    public String autoText10 = "";

    @Config
    public boolean repeat = false;

    @Config
    public int autoText1Bind = 0;
    @Config
    public int autoText2Bind = 0;
    @Config
    public int autoText3Bind = 0;
    @Config
    public int autoText4Bind = 0;
    @Config
    public int autoText5Bind = 0;
    @Config
    public int autoText6Bind = 0;
    @Config
    public int autoText7Bind = 0;
    @Config
    public int autoText8Bind = 0;
    @Config
    public int autoText9Bind = 0;
    @Config
    public int autoText10Bind = 0;

    private static boolean pressed = false;
    private static boolean held = false;

    public AutoText() {
        super("AutoText", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Automatically send a message on key press.");
        addToggle(new ToggleValue("Repeat while held", "repeat", this));
        addText(new TextValue("1", "autoText1", this));
        addBind(new BindValue("Bind", "autoText1Bind", this));
        addText(new TextValue("2", "autoText2", this));
        addBind(new BindValue("Bind", "autoText2Bind", this));
        addText(new TextValue("3", "autoText3", this));
        addBind(new BindValue("Bind", "autoText3Bind", this));
        addText(new TextValue("4", "autoText4", this));
        addBind(new BindValue("Bind", "autoText4Bind", this));
        addText(new TextValue("5", "autoText5", this));
        addBind(new BindValue("Bind", "autoText5Bind", this));
        addText(new TextValue("6", "autoText6", this));
        addBind(new BindValue("Bind", "autoText6Bind", this));
        addText(new TextValue("7", "autoText7", this));
        addBind(new BindValue("Bind", "autoText7Bind", this));
        addText(new TextValue("8", "autoText8", this));
        addBind(new BindValue("Bind", "autoText8Bind", this));
        addText(new TextValue("9", "autoText9", this));
        addBind(new BindValue("Bind", "autoText9Bind", this));
        addText(new TextValue("10", "autoText10", this));
        addBind(new BindValue("Bind", "autoText10Bind", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (this.mc.currentScreen != null) return;

        held = isBindDown();

        if (((held && !pressed) || this.repeat) && !Keyboard.isKeyDown(0)) {
            if (Keyboard.isKeyDown(this.autoText1Bind)) {
                alert(this.autoText1);
            }

            if (Keyboard.isKeyDown(this.autoText2Bind)) {
                alert(this.autoText2);
            }

            if (Keyboard.isKeyDown(this.autoText3Bind)) {
                alert(this.autoText3);
            }

            if (Keyboard.isKeyDown(this.autoText4Bind)) {
                alert(this.autoText4);
            }

            if (Keyboard.isKeyDown(this.autoText5Bind)) {
                alert(this.autoText5);
            }

            if (Keyboard.isKeyDown(this.autoText6Bind)) {
                alert(this.autoText6);
            }

            if (Keyboard.isKeyDown(this.autoText7Bind)) {
                alert(this.autoText7);
            }

            if (Keyboard.isKeyDown(this.autoText8Bind)) {
                alert(this.autoText8);
            }

            if (Keyboard.isKeyDown(this.autoText9Bind)) {
                alert(this.autoText9);
            }

            if (Keyboard.isKeyDown(this.autoText10Bind)) {
                alert(this.autoText10);
            }
        }
        pressed = held;
    }

    private boolean isBindDown() {
        if (Keyboard.isKeyDown(0)) return false;
        return (Keyboard.isKeyDown(this.autoText1Bind) ||
                Keyboard.isKeyDown(this.autoText2Bind) ||
                Keyboard.isKeyDown(this.autoText3Bind) ||
                Keyboard.isKeyDown(this.autoText4Bind) ||
                Keyboard.isKeyDown(this.autoText5Bind) ||
                Keyboard.isKeyDown(this.autoText6Bind) ||
                Keyboard.isKeyDown(this.autoText7Bind) ||
                Keyboard.isKeyDown(this.autoText8Bind) ||
                Keyboard.isKeyDown(this.autoText9Bind) ||
                Keyboard.isKeyDown(this.autoText10Bind));
    }

    private static void alert(String msg) {
        if (msg.isEmpty()) {
            Meowtils.addMessage("No message set!");
        } else {
            Meowtils.sendCleanMessage(msg);
        }
    }
}