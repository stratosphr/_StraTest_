package utilities;

import static utilities.OS.EOperatingSystem.*;

/**
 * Created by gvoiron on 03/04/17.
 * Time : 10:21
 */
public final class OS {

    public static EOperatingSystem getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return LINUX;
        } else if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("mac")) {
            return MAC;
        } else {
            throw new Error("Operating system \"" + System.getProperty("os.name") + "\" is unsupported.");
        }
    }

    public enum EOperatingSystem {
        LINUX,
        MAC,
        WINDOWS
    }

}
