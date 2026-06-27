package wtf.tatp.meowtils.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.mixin.AccessorSoundHandler;

public class SoundLoader {

    private static final Random RANDOM = new Random();
    private static URL[] critSounds;
    private static SoundSystem soundSystem;
    private static int soundCounter = 0;

    public static void init() {
        try {
            soundSystem = getSoundSystem();

            critSounds = new URL[] {
                    getSoundFile("/meowtils/sounds/crit/Crit1.ogg", "Crit1"),
                    getSoundFile("/meowtils/sounds/crit/Crit2.ogg", "Crit2"),
                    getSoundFile("/meowtils/sounds/crit/Crit3.ogg", "Crit3")
            };

            Meowtils.info("Sounds loaded!");
        } catch (Exception e) {
            Meowtils.error("Failed to load sounds: " + e);
        }
    }

    public static void playSound(int volume) {
        URL critSound = critSounds[RANDOM.nextInt(critSounds.length)];
        String sourceId = "meowtils_crit_" + soundCounter++;
        float vol = volume / 100.0F;

        soundSystem.newSource(false, sourceId, critSound, "crit.ogg", false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
        soundSystem.setVolume(sourceId, vol);
        soundSystem.play(sourceId);

        if (soundCounter > 50) soundCounter = 0;
    }

    private static URL getSoundFile(String jarPath, String name) throws IOException {
        InputStream inputStream = SoundLoader.class.getResourceAsStream(jarPath);
        if (inputStream == null) throw new FileNotFoundException("Not in jar: " + jarPath);

        File temp = File.createTempFile("meowtils_" + name + "_", ".ogg");
        temp.deleteOnExit();

        try (InputStream is = inputStream; FileOutputStream fileOutputStream = new FileOutputStream(temp)) {
            byte[] byteBuffer = new byte[4096];
            int n;
            while ((n = is.read(byteBuffer)) != -1) {
                fileOutputStream.write(byteBuffer, 0, n);
            }
        }
        return temp.toURI().toURL();
    }

    private static SoundSystem getSoundSystem() throws Exception {
        SoundManager sm = ((AccessorSoundHandler) Minecraft.getMinecraft().getSoundHandler()).getSndManager();

        for (String name : new String[] { "sndSystem", "field_148620_e" }) {
            try {
                Field f = SoundManager.class.getDeclaredField(name);
                f.setAccessible(true);
                return (SoundSystem) f.get(sm);
            } catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException("sndSystem/field_148620_e not found in SoundManager");
    }
}