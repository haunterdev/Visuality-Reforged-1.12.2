package plus.dragons.visuality.mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import plus.dragons.visuality.client.VisualityParticles;
import plus.dragons.visuality.config.VisualityConfig;
import plus.dragons.visuality.particle.SlimeParticle;

/**
 * Slime blob particles on landing. Ports the modern SlimeMixin
 * (spawnCustomParticles inject) - 1.12.2 EntitySlime exposes the identical
 * {@code spawnCustomParticles()} hook (called in onUpdate on landing). Returning
 * true suppresses the vanilla slime particles and spawns Visuality blobs instead.
 * Real slimes only (skips magma cubes via particle type). Client-side only.
 */
@Mixin(EntitySlime.class)
public abstract class MixinEntitySlime extends EntityLiving {

    private MixinEntitySlime(World world) {
        super(world);
    }

    @Shadow
    public abstract int getSlimeSize();

    @Shadow
    protected abstract EnumParticleTypes getParticleType();

    // remap = false: spawnCustomParticles is a Forge-added method (no SRG mapping)
    @Inject(method = "spawnCustomParticles", at = @At("RETURN"), cancellable = true, remap = false)
    private void visuality$slimeBlobs(CallbackInfoReturnable<Boolean> cir) {
        if (!VisualityConfig.slimeEnabled) {
            return;
        }
        // only ordinary slimes (magma cubes return LAVA)
        if (this.getParticleType() != EnumParticleTypes.SLIME) {
            return;
        }
        if (!this.world.isRemote) {
            return;
        }

        int size = this.getSlimeSize();
        TextureAtlasSprite[] set;
        float scale;
        if (size == 1) {
            set = VisualityParticles.smallSlime();
            scale = 1.0f;
        } else if (size == 2) {
            set = VisualityParticles.mediumSlime();
            scale = 1.0f;
        } else {
            set = VisualityParticles.bigSlime();
            scale = 2.0f;
        }
        if (set == null || set.length == 0) {
            cir.setReturnValue(true);
            return;
        }

        // slime green, modern 0x88FF79 (8978297); old 0x88FF39 read too dark/saturated
        float r = 0.53333336f;
        float g = 1.0f;
        float b = 0.47450981f;

        for (int j = 0; j < size * 8; j++) {
            float f = this.rand.nextFloat() * ((float) Math.PI * 2f);
            float f1 = this.rand.nextFloat() * 0.5f + 0.5f;
            float f2 = MathHelper.sin(f) * size * 0.5f * f1;
            float f3 = MathHelper.cos(f) * size * 0.5f * f1;
            TextureAtlasSprite sprite = set[this.rand.nextInt(set.length)];
            SlimeParticle.spawn(this.world,
                    this.posX + f2, this.posY, this.posZ + f3,
                    r, g, b, scale, sprite);
        }
        cir.setReturnValue(true);
    }
}
