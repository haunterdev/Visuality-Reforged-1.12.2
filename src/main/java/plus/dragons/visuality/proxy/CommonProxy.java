package plus.dragons.visuality.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Server/common proxy. Visuality is client-only, so this is a no-op skeleton;
 * the dedicated-server side simply does nothing.
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
    }
}
