package net.globalcontrols.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public final class ModVersion {
    private static final Logger LOG = Logger.getLogger(ModVersion.class.getName());
    public static final String VERSION;

    static {
        String v = "unknown";
        try (InputStream is = ModVersion.class.getResourceAsStream("/version.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                v = props.getProperty("version", "unknown");
            }
        } catch (IOException e) {
            LOG.warning("Could not load version: " + e.getMessage());
        }
        VERSION = v;
    }

    private ModVersion() {}
}
