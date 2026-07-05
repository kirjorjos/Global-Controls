package net.globalcontrols.platform.legacy;

import net.globalcontrols.platform.api.InstalledMod;
import net.globalcontrols.platform.api.ModPlatform;

import java.util.*;

public class LegacyModProvider implements ModPlatform {
    private final List<InstalledMod> mods;

    public LegacyModProvider() {
        this.mods = discoverMods();
    }

    public LegacyModProvider(List<InstalledMod> mods) {
        this.mods = mods != null ? mods : List.of();
    }

    @Override
    public Collection<InstalledMod> getInstalledMods() {
        return mods;
    }

    private static List<InstalledMod> discoverMods() {
        List<InstalledMod> mods = new ArrayList<>();
        try {
            Class<?> loaderClass = Class.forName("cpw.mods.fml.common.Loader");
            Object loader = loaderClass.getMethod("instance").invoke(null);
            List<?> activeModList = (List<?>) loaderClass.getMethod("getActiveModList").invoke(loader);
            for (Object modContainer : activeModList) {
                String modId = (String) modContainer.getClass().getMethod("getModId").invoke(modContainer);
                String name = (String) modContainer.getClass().getMethod("getName").invoke(modContainer);
                mods.add(new InstalledMod(modId, name));
            }
            if (!mods.isEmpty()) return mods;
        } catch (Exception ignored) {}
        try {
            Class<?> loaderClass = Class.forName("net.minecraftforge.fml.common.Loader");
            Object loader = loaderClass.getMethod("instance").invoke(null);
            List<?> modList = (List<?>) loaderClass.getMethod("getActiveModList").invoke(loader);
            for (Object modContainer : modList) {
                String modId = (String) modContainer.getClass().getMethod("getModId").invoke(modContainer);
                String name = (String) modContainer.getClass().getMethod("getName").invoke(modContainer);
                mods.add(new InstalledMod(modId, name));
            }
        } catch (Exception ignored) {}
        return mods;
    }
}
