package wtf.tatp.meowtils.extension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ExtensionClassLoader extends ClassLoader implements Closeable {

    private final Map<String, byte[]> resources = new HashMap<>();

    public ExtensionClassLoader(byte[] jarBytes, ClassLoader cl) throws IOException {
        super(cl);
        try (JarInputStream j = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            JarEntry entry;
            while ((entry = j.getNextJarEntry()) != null) {
                if (entry.isDirectory()) continue;

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n;
                while ((n = j.read(buffer)) != -1) {
                    b.write(buffer, 0, n);
                }

                this.resources.put(entry.getName(), b.toByteArray());
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/') + ".class";
        byte[] classBytes = this.resources.get(path);

        if (classBytes == null) throw new ClassNotFoundException(name);

        return defineClass(name, classBytes, 0, classBytes.length);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] data = this.resources.get(name);
        return (data != null) ? new ByteArrayInputStream(data) : super.getResourceAsStream(name);
    }

    @Override
    public URL getResource(String name) {
        byte[] data = this.resources.get(name);
        if (data == null) return super.getResource(name);
        try {
            return new URL("data:application/octet-stream;base64," + Base64.getEncoder().encodeToString(data));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public void close() {
        this.resources.clear();
    }
}