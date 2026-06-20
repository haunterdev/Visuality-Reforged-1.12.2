package plus.dragons.visuality.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import plus.dragons.visuality.client.VisualityParticles;
import plus.dragons.visuality.config.VisualityConfig;
import plus.dragons.visuality.event.VisualityEvents;

/**
 * Client proxy. Loads config, registers particle sprites on the block atlas, and
 * wires the Forge event hub.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        VisualityConfig.init(event.getSuggestedConfigurationFile());
        VisualityParticles.init();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        VisualityEvents.register();
    }
}
