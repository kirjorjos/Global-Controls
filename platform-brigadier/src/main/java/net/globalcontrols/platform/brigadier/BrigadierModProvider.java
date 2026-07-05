package net.globalcontrols.platform.brigadier;

import net.globalcontrols.platform.api.InstalledMod;
import net.globalcontrols.platform.api.ModPlatform;

import java.lang.reflect.Method;
import java.util.*;

public class BrigadierModProvider implements ModPlatform {
    private final List<InstalledMod> mods;

    public BrigadierModProvider() {
        this.mods = discoverMods();
    }

    public BrigadierModProvider(List<InstalledMod> mods) {
        this.mods = mods != null ? mods : List.of();
    }

    @Override
    public Collection<InstalledMod> getInstalledMods() {
        return mods;
    }

    private static List<InstalledMod> discoverMods() {
        List<InstalledMod> result = tryFabric();
        if (!result.isEmpty()) return result;
        result = tryForge();
        if (!result.isEmpty()) return result;
        result = tryQuilt();
        return result;
    }

    private static List<InstalledMod> tryFabric() {
        List<InstalledMod> mods = new ArrayList<>();
        try {
            Class<?> loaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object loader = loaderClass.getMethod("getInstance").invoke(null);
            Iterable<?> allMods = (Iterable<?>) loader.getClass().getMethod("getAllMods").invoke(loader);
            for (Object mod : allMods) {
                Object meta = mod.getClass().getMethod("getMetadata").invoke(mod);
                String id = (String) meta.getClass().getMethod("getId").invoke(meta);
                String name = (String) meta.getClass().getMethod("getName").invoke(meta);
                mods.add(new InstalledMod(id, name));
            }
        } catch (Exception ignored) {}
        return mods;
    }

    private static List<InstalledMod> tryForge() {
        List<InstalledMod> mods = new ArrayList<>();
        try {
            Class<?> modListClass = Class.forName("net.minecraftforge.fml.ModList");
            Object modList = modListClass.getMethod("get").invoke(null);
            List<?> forgeMods = (List<?>) modListClass.getMethod("getMods").invoke(modList);
            for (Object mod : forgeMods) {
                String id = (String) mod.getClass().getMethod("getModId").invoke(mod);
                String name = (String) mod.getClass().getMethod("getDisplayName").invoke(mod);
                mods.add(new InstalledMod(id, name));
            }
        } catch (Exception ignored) {}
        if (mods.isEmpty()) {
            try {
                Class<?> modListClass = Class.forName("net.neoforged.fml.ModList");
                Object modList = modListClass.getMethod("get").invoke(null);
                List<?> neoMods = (List<?>) modListClass.getMethod("getMods").invoke(modList);
                for (Object mod : neoMods) {
                    String id = (String) mod.getClass().getMethod("getModId").invoke(mod);
                    String name = (String) mod.getClass().getMethod("getDisplayName").invoke(mod);
                    mods.add(new InstalledMod(id, name));
                }
            } catch (Exception ignored) {}
        }
        return mods;
    }

    private static List<InstalledMod> tryQuilt() {
        List<InstalledMod> mods = new ArrayList<>();
        try {
            Class<?> loaderClass = Class.forName("org.quiltmc.loader.api.QuiltLoader");
            Iterable<?> allMods = (Iterable<?>) loaderClass.getMethod("getAllMods").invoke(null);
            for (Object mod : allMods) {
                Object meta = mod.getClass().getMethod("getMetadata").invoke(mod);
                String id = (String) meta.getClass().getMethod("getId").invoke(meta);
                String name = (String) meta.getClass().getMethod("getName").invoke(meta);
                mods.add(new InstalledMod(id, name));
            }
        } catch (Exception ignored) {}
        return mods;
    }
}
