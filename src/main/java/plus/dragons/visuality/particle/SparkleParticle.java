package plus.dragons.visuality.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import plus.dragons.visuality.client.VisualityParticles;

@SideOnly(Side.CLIENT)
public class SparkleParticle extends Particle {

    private final TextureAtlasSprite[] frames;

    public SparkleParticle(World world, double x, double y, double z,
                           float r, float g, float b,
                           TextureAtlasSprite[] frames) {
        super(world, x, y, z, 0, 0, 0);
        this.frames = frames;
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
        this.particleMaxAge = 5 + this.rand.nextInt(4);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.multipleParticleScaleBy(1.1f); // mirrors 1.20 m_6569_(1.1) on the random default
        this.setParticleTexture(frames[0]);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        int idx = this.particleAge * this.frames.length / this.particleMaxAge;
        if (idx >= this.frames.length) {
            idx = this.frames.length - 1;
        }
        this.setParticleTexture(this.frames[idx]);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    public static void spawn(World world, double x, double y, double z,
                             float r, float g, float b) {
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new SparkleParticle(world, x, y, z, r, g, b,
                        VisualityParticles.sparkle()));
    }
}
