package wtf.tatp.meowtils.manager.lists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import wtf.tatp.meowtils.Meowtils;

public class SafelistManager {

    private static final File FILE = Meowtils.MEOWTILS_SAFELIST;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Set<String>>() {}.getType();
    private static Set<String> safelist = new HashSet<>();

    static {
        loadSafelist();
    }

    public static void add(String uuidOrName) {
        safelist.add(uuidOrName);
        saveSafelist();
    }

    public static void remove(String uuidOrName) {
        safelist.remove(uuidOrName);
        saveSafelist();
    }

    public static boolean isSafelisted(String uuidOrName) {
        return safelist.contains(uuidOrName);
    }

    private static void loadSafelist() {
        if (!FILE.exists()) return;
        try (Reader reader = new FileReader(FILE)) {
            Set<String> loaded = gson.fromJson(reader, TYPE);
            if (loaded != null) {
                safelist = loaded;
            } else {
                safelist = new HashSet<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveSafelist() {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(safelist, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}