package wtf.tatp.meowtils.extension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.gui.ClickGUI;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.ModuleManager;

public final class ExtensionManager {

    private static final List<ExtensionClassLoader> OPEN_LOADERS = new ArrayList<>();
    public static final List<Module> EXTENSION_MODULES = new ArrayList<>();
    public static final List<Object> EXTENSION_LISTENERS = new ArrayList<>();

    public static void load(File dir) {
        File[] files = dir.listFiles();

        if (files == null) return;
        for (File file : files) {
            if (file.getName().endsWith(".meowtils")) {
                try {
                    loadExtension(file);
                } catch (Throwable t) {
                    Meowtils.error("Failed to load extension: " + file.getName());
                    t.printStackTrace();
                }
            }
        }
    }

    public static void reload() {
        for (Object lst : EXTENSION_LISTENERS) {
            EventManager.unregister(lst);
        }
        EXTENSION_LISTENERS.clear();

        for (Module m : EXTENSION_MODULES) {
            if (m.getState()) m.setState(false);
            ModuleManager.unregister(m);
        }

        OPEN_LOADERS.clear();
        EXTENSION_MODULES.clear();
        EXTENSION_LISTENERS.clear();

        Meowtils.loadedExtensions = false;
        load(Meowtils.EXTENSION_DIR);
        ConfigManager.load();

        ClickGUI gui = Meowtils.getClickGUI();
        if (gui != null) gui.rebuildExtensionsFrame();
        Meowtils.addMessage(EnumChatFormatting.GREEN + "Extensions reloaded successfully.");
    }

    private static void loadExtension(File file) throws Exception {
        byte[] jarBytes;
        try (FileInputStream f = new FileInputStream(file);
             ByteArrayOutputStream b = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int n;
            while ((n = f.read(buffer)) != -1) {
                b.write(buffer, 0, n);
            }
            jarBytes = b.toByteArray();
        } catch (IOException e) {
            Meowtils.error("Error loading extension: " + e);
            throw e;
        }

        ExtensionClassLoader extensionLoader = new ExtensionClassLoader(jarBytes, ExtensionManager.class.getClassLoader());

        ClassLoader cl = Meowtils.isLunar() ? new MappingClassLoader(extensionLoader) : extensionLoader;

        Properties properties = getProperties(file, jarBytes);

        int moduleStart = EXTENSION_MODULES.size();
        int listenerStart = EXTENSION_LISTENERS.size();

        try {
            String main = properties.getProperty("main");
            Class<?> clazz = cl.loadClass(main);
            clazz.getMethod("init").invoke(null);

            OPEN_LOADERS.add(extensionLoader);
            Meowtils.warn("Loaded " + file.getName());
            Meowtils.loadedExtensions = true;
        } catch (Throwable t) {
            int i;
            for (i = EXTENSION_LISTENERS.size() - 1; i >= listenerStart; i--) {
                Object listener = EXTENSION_LISTENERS.remove(i);
                EventManager.unregister(listener);
            }
            for (i = EXTENSION_MODULES.size() - 1; i >= moduleStart; i--) {
                Module module = EXTENSION_MODULES.remove(i);
                if (module.getState()) module.setState(false);
                ModuleManager.unregister(module);
            }

            try {
                extensionLoader.close();
            } catch (Exception ignored) {}
            throw t;
        }
    }

    private static Properties getProperties(File file, byte[] jarBytes) throws IOException {
        Properties properties = new Properties();
        try (JarInputStream j = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            JarEntry entry;
            while ((entry = j.getNextJarEntry()) != null) {
                if (!entry.getName().equals("META-INF/meowtils.extension")) continue;
                properties.load(j);
            }
        }

        if (properties.isEmpty()) throw new IllegalStateException("Missing meowtils.extension in " + file.getName());
        return properties;
    }
}