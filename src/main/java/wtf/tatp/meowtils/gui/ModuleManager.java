package wtf.tatp.meowtils.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.extension.ExtensionManager;
import wtf.tatp.meowtils.module.RegisterModule;

public class ModuleManager {

    private static final List<Module> MODULES = new ArrayList<>();
    private static final Map<Class<? extends Module>, Module> BY_CLASS = new HashMap<>();

    static {
        RegisterModule.init();
        ExtensionManager.load(Meowtils.EXTENSION_DIR);
    }

    public static void register(Module... mods) {
        for (Module module : mods) {
            if (!BY_CLASS.containsKey(module.getClass())) {
                MODULES.add(module);
                BY_CLASS.put(module.getClass(), module);
            }
        }
        MODULES.sort(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public static void unregister(Module module) {
        MODULES.remove(module);
        BY_CLASS.remove(module.getClass());
    }

    public static List<Module> getModules() {
        return MODULES;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> clazz) {
        if (BY_CLASS.get(clazz) == null) {
            throw new IllegalStateException("Module is not registered: " + clazz.getSimpleName());
        }
        return (T) BY_CLASS.get(clazz);
    }
}