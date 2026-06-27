package wtf.tatp.meowtils.gui.component;

import wtf.tatp.meowtils.gui.component.subcomponents.ExpandComponent;

public class Component {

    public boolean nested = false;
    public ExpandComponent expandParent = null;

    public void render() {}

    public void keyTyped(char typedChar, int key) {}

    public void updateComponent(int mouseX, int mouseY) {}

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    public void setOff(int newOff) {}

    public int getHeight() {
        return 12;
    }
}