package net.globalcontrols.platform.legacy;

import net.globalcontrols.platform.api.InstalledMod;
import net.globalcontrols.platform.api.ModPlatform;

import java.util.Collection;
import java.util.Collections;

public class LegacyModProvider implements ModPlatform {
    @Override
    public Collection<InstalledMod> getInstalledMods() {
        // TODO: enumerate mods via Forge's Loader / ModList API
        return Collections.emptyList();
    }
}
