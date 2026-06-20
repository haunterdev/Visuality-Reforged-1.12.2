package plus.dragons.visuality.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

import plus.dragons.visuality.Visuality;

import java.io.File;

/**
 * Visuality config - replaces the modern ForgeConfigSpec (TOML) + ReloadableJsonConfig
 * (codec/reload-listener) system with a single 1.12.2 Forge {@link Configuration}.
 *
 * <p>Per-emitter entry data (which blocks/entities emit which particle) lives in
 * {@link ParticleEmitters} as hardcoded defaults; these toggles only gate the features.
 */
public final class VisualityConfig {

    public static boolean slimeEnabled = true;
    public static boolean chargeEnabled = true;
    public static boolean soulEnabled = true;
    public static boolean hitParticlesEnabled = true;
    public static boolean shinyArmorEnabled = true;
    public static boolean shinyBlocksEnabled = true;

    public static boolean waterCircleEnabled = true;
    public static boolean waterCircleColored = true;
    public static boolean waterCircleForce = false;
    public static int waterCircleDensity = 16;

    /**
     * Shared, mod-neutral JVM property used by multiple mods to cooperatively claim the
     * "rain ripple on water" effect so only one renders it. First mod to claim wins;
     * others defer (unless they force-enable). No shared library / hard dependency needed -
     * any mod can participate by reading/writing this exact key.
     */
    public static final String RAIN_RIPPLE_OWNER_KEY = "minecraft.rainRipple.owner";

    private static Configuration config;

    public static void init(File file) {
        config = new Configuration(file);
        load();
        MinecraftForge.EVENT_BUS.register(new VisualityConfig());
    }

    private static void load() {
        final String general = "general";
        slimeEnabled = config.getBoolean("slimeEnabled", general, true, "Slime blob particles on landing");
        chargeEnabled = config.getBoolean("chargeEnabled", general, true, "Charge particles on charged creepers");
        soulEnabled = config.getBoolean("soulEnabled", general, true, "Soul particles when walking on soul sand/soil");
        hitParticlesEnabled = config.getBoolean("hitParticlesEnabled", general, true, "Extra particles when entities are hit");
        shinyArmorEnabled = config.getBoolean("shinyArmorEnabled", general, true, "Sparkles around entities wearing configured armor");
        shinyBlocksEnabled = config.getBoolean("shinyBlocksEnabled", general, true, "Ambient sparkles around configured blocks");

        final String water = "waterCircle";
        waterCircleEnabled = config.getBoolean("enabled", water, true, "Water circle particles in rain");
        waterCircleColored = config.getBoolean("colored", water, true, "Tint water circles by biome water color");
        waterCircleForce = config.getBoolean("force", water, false,
                "Render water circles even if another mod already claimed the rain-ripple effect");
        waterCircleDensity = config.getInt("density", water, 16, 0, 64, "Water circle spawn density");

        if (waterCircleEnabled) {
            waterCircleEnabled = claimRainRipple();
        }

        if (config.hasChanged()) {
            config.save();
        }
    }

    /**
     * Cooperative claim of the shared rain-ripple effect. Returns whether Visuality
     * should render water circles: true if we claimed it (or forced), false if another
     * mod already owns it.
     */
    private static boolean claimRainRipple() {
        if (waterCircleForce) {
            return true;
        }
        synchronized (VisualityConfig.class) {
            String owner = System.getProperty(RAIN_RIPPLE_OWNER_KEY);
            if (owner == null || owner.isEmpty()) {
                System.setProperty(RAIN_RIPPLE_OWNER_KEY, Visuality.ID);
                return true;
            }
            if (Visuality.ID.equals(owner)) {
                return true;
            }
            Visuality.LOGGER.info("Rain-ripple effect already provided by '{}' - Visuality water circles deferred "
                    + "(set waterCircle.force=true to override)", owner);
            return false;
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Visuality.ID.equals(event.getModID())) {
            load();
        }
    }
}
