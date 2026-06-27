package wtf.tatp.meowtils.extension;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappingClassLoader extends ClassLoader {
    private static final Map<String, String> METHOD_MAP = loadCsv("mappings/methods.csv");
    private static final Map<String, String> FIELD_MAP = loadCsv("mappings/fields.csv");
    private final ExtensionClassLoader source;

    public MappingClassLoader(ExtensionClassLoader source) {
        super(source.getParent());
        this.source = source;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/') + ".class";
        try (InputStream is = this.source.getResourceAsStream(path)) {
            if (is == null) throw new ClassNotFoundException(name);
            byte[] remapped = remap(readAllBytes(is));
            return defineClass(name, remapped, 0, remapped.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toByteArray();
    }

    private byte[] remap(byte[] originalBytes) {
        org.spongepowered.asm.lib.ClassReader classReader = new org.spongepowered.asm.lib.ClassReader(originalBytes);
        org.spongepowered.asm.lib.ClassWriter classWriter = new org.spongepowered.asm.lib.ClassWriter(classReader, org.spongepowered.asm.lib.ClassWriter.COMPUTE_MAXS);

        org.spongepowered.asm.lib.commons.Remapper remapper = new org.spongepowered.asm.lib.commons.Remapper() {
            @Override
            public String mapMethodName(String owner, String originalName, String descriptor) {
                String mappedMethod = MappingClassLoader.METHOD_MAP.get(originalName);
                return mappedMethod != null ? mappedMethod : originalName;
            }

            @Override
            public String mapFieldName(String owner, String originalName, String descriptor) {
                String mappedField = MappingClassLoader.FIELD_MAP.get(originalName);
                return mappedField != null ? mappedField : originalName;
            }
        };

        org.spongepowered.asm.lib.commons.ClassRemapper classRemapper = new org.spongepowered.asm.lib.commons.ClassRemapper(classWriter, remapper);
        classReader.accept(classRemapper, org.spongepowered.asm.lib.ClassReader.EXPAND_FRAMES);

        return classWriter.toByteArray();
    }

    private static Map<String, String> loadCsv(String resourcePath) {
        try (InputStream is = MappingClassLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return Collections.emptyMap();
            Map<String, String> map = new HashMap<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length >= 2) map.put(cols[0].trim(), cols[1].trim());
            }
            return map;
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}