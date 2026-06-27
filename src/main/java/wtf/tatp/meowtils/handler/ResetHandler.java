package wtf.tatp.meowtils.handler;

import wtf.tatp.meowtils.event.WorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.ModuleManager;
import wtf.tatp.meowtils.manager.session.SessionManager;
import wtf.tatp.meowtils.util.TeamUtil;

public class ResetHandler {

    @EventTarget
    public void onWorldLoad(WorldEvent event) {
        if (event.getType() != WorldEvent.Type.LOAD) return;
        if (event.getWorld().isRemote) {
            TeamUtil.reset();
            SessionManager.resetStates();

            for (Module m : ModuleManager.getModules())
                m.reset();
        }
    }
}