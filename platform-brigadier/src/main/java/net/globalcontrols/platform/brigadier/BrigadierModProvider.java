package net.globalcontrols.platform.brigadier;

import net.globalcontrols.platform.api.InstalledMod;
import net.globalcontrols.platform.api.ModPlatform;

import java.util.Collection;
import java.util.Collections;

public class BrigadierModProvider implements ModPlatform {
    @Override
    public Collection<InstalledMod> getInstalledMods() {
        // TODO: enumerate mods via Fabric/Forge/NeoForge mod listing API
        return Collections.emptyList();
    }
}
