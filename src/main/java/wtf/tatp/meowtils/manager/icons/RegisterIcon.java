package wtf.tatp.meowtils.manager.icons;

import wtf.tatp.meowtils.manager.icons.impl.BlacklistIcon;
import wtf.tatp.meowtils.manager.icons.impl.FriendlistIcon;
import wtf.tatp.meowtils.manager.icons.impl.MurdererFinderIcon;
import wtf.tatp.meowtils.manager.icons.impl.NickIcon;
import wtf.tatp.meowtils.manager.icons.impl.SafelistIcon;
import wtf.tatp.meowtils.manager.icons.impl.SkywarsIcon;
import wtf.tatp.meowtils.manager.icons.impl.StatsIcon;
import wtf.tatp.meowtils.manager.icons.impl.UrchinIcon;

public class RegisterIcon {

    public static void init() {
        IconManager.register(new FriendlistIcon());
        IconManager.register(new NickIcon());
        IconManager.register(new SafelistIcon());
        IconManager.register(new BlacklistIcon());
        IconManager.register(new UrchinIcon());
        IconManager.register(new StatsIcon());
        IconManager.register(new SkywarsIcon());
        IconManager.register(new MurdererFinderIcon());
    }
}