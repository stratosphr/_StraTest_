package utilities;

import java.io.File;
import java.lang.reflect.Field;

import static java.util.regex.Pattern.quote;

/**
 * Created by gvoiron on 10/12/16.
 * Time : 17:30
 */
public final class LibraryLinker {

    public static void loadDirectoryLibraries(File file) {
        System.setProperty("java.library.path", System.getProperty("java.library.path") + System.getProperty("path.separator") + file.getAbsolutePath());
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
            fieldSysPath.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public static void unloadDirectoryLibraries(File file) {
        System.setProperty("java.library.path", System.getProperty("java.library.path").replaceAll(quote(file.getAbsolutePath()), ""));
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
            fieldSysPath.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

}
