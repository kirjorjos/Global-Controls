package net.globalcontrols.common.service;

import net.globalcontrols.platform.api.ModPlatform;
import net.globalcontrols.platform.api.InstalledMod;

import java.util.Collection;

public class ModService {
    private final ModPlatform modPlatform;

    public ModService(ModPlatform modPlatform) {
        this.modPlatform = modPlatform;
    }

    public Collection<InstalledMod> getInstalledMods() {
        return modPlatform.getInstalledMods();
    }
}
