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

public class FriendlistManager {

    private static final File FILE = Meowtils.MEOWTILS_FRIENDLIST;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Set<String>>() {}.getType();
    private static Set<String> friendlist = new HashSet<>();

    static {
        loadFriendlist();
    }

    public static void add(String uuidOrName) {
        friendlist.add(uuidOrName);
        saveFriendlist();
    }

    public static void remove(String uuidOrName) {
        friendlist.remove(uuidOrName);
        saveFriendlist();
    }

    public static boolean isFriendlisted(String uuidOrName) {
        return friendlist.contains(uuidOrName);
    }

    private static void loadFriendlist() {
        if (!FILE.exists()) return;
        try (Reader reader = new FileReader(FILE)) {
            Set<String> loaded = gson.fromJson(reader, TYPE);
            if (loaded != null) {
                friendlist = loaded;
            } else {
                friendlist = new HashSet<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveFriendlist() {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(friendlist, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}