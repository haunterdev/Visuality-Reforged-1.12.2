package plus.dragons.visuality.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import plus.dragons.visuality.client.VisualityParticles;

@SideOnly(Side.CLIENT)
public class SoulParticle extends Particle {

    private final TextureAtlasSprite[] frames;

    protected SoulParticle(World world, double x, double y, double z) {
        super(world, x, y, z, 0, 0, 0);
        this.frames = VisualityParticles.soul();
        this.motionX = (this.rand.nextDouble() * 2.0D - 1.0D) / 10.0D;
        this.motionY = 0.1D + this.rand.nextDouble() / 10.0D;
        this.motionZ = (this.rand.nextDouble() * 2.0D - 1.0D) / 10.0D;
        this.particleMaxAge = 16 + this.rand.nextInt(5);
        this.multipleParticleScaleBy(3.0F + this.rand.nextFloat()); // mirrors 1.20 m_6569_
        this.setParticleTexture(frames[0]);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.particleAge >= this.particleMaxAge) {
            return;
        }
        int idx = this.particleAge * frames.length / this.particleMaxAge;
        if (idx >= frames.length) {
            idx = frames.length - 1;
        }
        this.setParticleTexture(frames[idx]);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    public static void spawn(World world, double x, double y, double z) {
        Minecraft.getMinecraft().effectRenderer.addEffect(new SoulParticle(world, x, y, z));
    }
}
