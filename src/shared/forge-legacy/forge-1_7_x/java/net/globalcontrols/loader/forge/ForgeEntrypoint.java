package net.globalcontrols.loader.forge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Loader;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;

@Mod(modid = "globalcontrols", version = "1.0.0")
public class ForgeEntrypoint {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LegacyForgeEntrypoint.init(
            new LegacyCommandAdapter(),
            event.getModConfigurationDirectory().toPath(),
            Loader.instance().getMCVersionString()
        );
    }
}
