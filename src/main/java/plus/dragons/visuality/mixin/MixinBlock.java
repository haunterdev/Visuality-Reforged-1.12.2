package plus.dragons.visuality.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import plus.dragons.visuality.config.ParticleEmitters;

import java.util.Random;

/**
 * Block step / fall / ambient particles. Ports the modern BlockMixin
 * (fallOn / stepOn / animateTick) to the 1.12.2 vanilla method names
 * onFallenUpon / onEntityWalk / randomDisplayTick. Client-side only.
 */
@Mixin(Block.class)
public class MixinBlock {

    @Inject(method = "onFallenUpon", at = @At("TAIL"))
    private void visuality$fallOn(World world, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (world.isRemote) {
            int amount = MathHelper.abs(MathHelper.ceil(fallDistance)) + 1;
            ParticleEmitters.blockStep(amount, world, world.getBlockState(pos), pos, entity);
        }
    }

    @Inject(method = "onEntityWalk", at = @At("TAIL"))
    private void visuality$stepOn(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (world.isRemote
                && (entity.ticksExisted - entity.getEntityId()) % ParticleEmitters.getStepInterval() == 0) {
            ParticleEmitters.blockStep(1, world, world.getBlockState(pos), pos, entity);
        }
    }

    @Inject(method = "randomDisplayTick", at = @At("TAIL"))
    private void visuality$animateTick(IBlockState state, World world, BlockPos pos, Random rand, CallbackInfo ci) {
        ParticleEmitters.blockAmbient(state, world, pos, rand);
    }
}
