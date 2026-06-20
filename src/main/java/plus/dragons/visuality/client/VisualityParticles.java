package plus.dragons.visuality.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client-side particle sprite registry for Visuality.
 *
 * <p>Replaces the modern ParticleType/SpriteSet registry system (dropped in the
 * 1.12.2 port). Registers every Visuality particle sprite onto the vanilla block
 * atlas during {@link TextureStitchEvent.Pre}, resolves them in
 * {@link TextureStitchEvent.Post}, and exposes static getters for particle classes.
 *
 * <p>Particle classes spawn themselves via {@code Minecraft.getMinecraft().effectRenderer
 * .addEffect(...)}; this class only owns the textures.
 */
@SideOnly(Side.CLIENT)
public final class VisualityParticles {

    private static final String NS = "visuality";

    /* ---- resource locations (built in static init) ---- */
    private static final ResourceLocation[] SPARKLE_LOCS = new ResourceLocation[6];
    private static final ResourceLocation[] SMALL_SLIME_LOCS = new ResourceLocation[4];
    private static final ResourceLocation[] MEDIUM_SLIME_LOCS = new ResourceLocation[4];
    private static final ResourceLocation[] BIG_SLIME_LOCS = new ResourceLocation[4];
    private static final ResourceLocation[] CHARGE_LOCS = new ResourceLocation[7];
    private static final ResourceLocation[] WATER_CIRCLE_LOCS = new ResourceLocation[5];
    private static final ResourceLocation[] SOUL_LOCS = new ResourceLocation[11];
    private static ResourceLocation BONE_LOC;
    private static ResourceLocation WITHER_BONE_LOC;
    private static ResourceLocation FEATHER_LOC;
    private static ResourceLocation EMERALD_LOC;

    /* ---- resolved sprites (filled after stitch Post) ---- */
    private static TextureAtlasSprite[] sparkle;
    private static TextureAtlasSprite[] smallSlime;
    private static TextureAtlasSprite[] mediumSlime;
    private static TextureAtlasSprite[] bigSlime;
    private static TextureAtlasSprite[] charge;
    private static TextureAtlasSprite[] waterCircle;
    private static TextureAtlasSprite[] soul;
    private static TextureAtlasSprite bone;
    private static TextureAtlasSprite witherBone;
    private static TextureAtlasSprite feather;
    private static TextureAtlasSprite emerald;

    static {
        for (int i = 0; i < 6; i++) SPARKLE_LOCS[i] = loc("sparkle_" + i);
        for (int i = 0; i < 4; i++) SMALL_SLIME_LOCS[i] = loc("small_slime_blob_" + i);
        for (int i = 0; i < 4; i++) MEDIUM_SLIME_LOCS[i] = loc("medium_slime_blob_" + i);
        for (int i = 0; i < 4; i++) BIG_SLIME_LOCS[i] = loc("big_slime_blob_" + i);
        // charge animation interleaves frames with the blank "empty" sprite
        CHARGE_LOCS[0] = loc("charge_0");
        CHARGE_LOCS[1] = loc("empty");
        CHARGE_LOCS[2] = loc("charge_1");
        CHARGE_LOCS[3] = loc("empty");
        CHARGE_LOCS[4] = loc("charge_2");
        CHARGE_LOCS[5] = loc("empty");
        CHARGE_LOCS[6] = loc("charge_3");
        for (int i = 0; i < 5; i++) WATER_CIRCLE_LOCS[i] = loc("water_circle_" + i);
        for (int i = 0; i < 11; i++) SOUL_LOCS[i] = loc("soul_" + i);
        BONE_LOC = loc("bone");
        WITHER_BONE_LOC = loc("wither_bone");
        FEATHER_LOC = loc("feather");
        EMERALD_LOC = loc("emerald");
    }

    private static ResourceLocation loc(String name) {
        return new ResourceLocation(NS, "particle/" + name);
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new VisualityParticles());
    }

    @SubscribeEvent
    public void onStitchPre(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (ResourceLocation l : SPARKLE_LOCS) map.registerSprite(l);
        for (ResourceLocation l : SMALL_SLIME_LOCS) map.registerSprite(l);
        for (ResourceLocation l : MEDIUM_SLIME_LOCS) map.registerSprite(l);
        for (ResourceLocation l : BIG_SLIME_LOCS) map.registerSprite(l);
        for (ResourceLocation l : CHARGE_LOCS) map.registerSprite(l);
        for (ResourceLocation l : WATER_CIRCLE_LOCS) map.registerSprite(l);
        for (ResourceLocation l : SOUL_LOCS) map.registerSprite(l);
        map.registerSprite(BONE_LOC);
        map.registerSprite(WITHER_BONE_LOC);
        map.registerSprite(FEATHER_LOC);
        map.registerSprite(EMERALD_LOC);
    }

    @SubscribeEvent
    public void onStitchPost(TextureStitchEvent.Post event) {
        TextureMap map = event.getMap();
        sparkle = resolve(map, SPARKLE_LOCS);
        smallSlime = resolve(map, SMALL_SLIME_LOCS);
        mediumSlime = resolve(map, MEDIUM_SLIME_LOCS);
        bigSlime = resolve(map, BIG_SLIME_LOCS);
        charge = resolve(map, CHARGE_LOCS);
        waterCircle = resolve(map, WATER_CIRCLE_LOCS);
        soul = resolve(map, SOUL_LOCS);
        bone = map.getAtlasSprite(BONE_LOC.toString());
        witherBone = map.getAtlasSprite(WITHER_BONE_LOC.toString());
        feather = map.getAtlasSprite(FEATHER_LOC.toString());
        emerald = map.getAtlasSprite(EMERALD_LOC.toString());
    }

    private static TextureAtlasSprite[] resolve(TextureMap map, ResourceLocation[] locs) {
        TextureAtlasSprite[] out = new TextureAtlasSprite[locs.length];
        for (int i = 0; i < locs.length; i++) out[i] = map.getAtlasSprite(locs[i].toString());
        return out;
    }

    /* ---- getters (public API for particle classes) ---- */
    public static TextureAtlasSprite[] sparkle() { return sparkle; }
    public static TextureAtlasSprite[] smallSlime() { return smallSlime; }
    public static TextureAtlasSprite[] mediumSlime() { return mediumSlime; }
    public static TextureAtlasSprite[] bigSlime() { return bigSlime; }
    public static TextureAtlasSprite[] charge() { return charge; }
    public static TextureAtlasSprite[] waterCircle() { return waterCircle; }
    public static TextureAtlasSprite[] soul() { return soul; }
    public static TextureAtlasSprite bone() { return bone; }
    public static TextureAtlasSprite witherBone() { return witherBone; }
    public static TextureAtlasSprite feather() { return feather; }
    public static TextureAtlasSprite emerald() { return emerald; }
}
