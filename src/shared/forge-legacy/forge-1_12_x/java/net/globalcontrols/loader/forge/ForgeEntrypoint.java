package net.globalcontrols.loader.forge;

import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "globalcontrols", version = "1.0.0")
public class ForgeEntrypoint {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LegacyForgeEntrypoint.init(
            new LegacyCommandAdapter(),
            event.getModConfigurationDirectory().toPath(),
            net.minecraftforge.fml.common.Loader.instance().getMCVersionString()
        );
    }
}
