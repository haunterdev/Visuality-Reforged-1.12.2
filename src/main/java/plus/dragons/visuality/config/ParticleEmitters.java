package plus.dragons.visuality.config;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import plus.dragons.visuality.client.VisualityParticles;
import plus.dragons.visuality.particle.SoulParticle;
import plus.dragons.visuality.particle.SparkleParticle;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Block-driven particle emitters (step / fall / ambient). Replaces the modern
 * BlockStepParticleConfig + BlockAmbientParticleConfig (codec/reload-listener
 * JSON system, dropped) with hardcoded 1.12.2 defaults faithful to the originals:
 *
 * <ul>
 *   <li>Soul particles when walking on / standing over soul sand.</li>
 *   <li>Sparkles drifting off ore blocks (shiny blocks).</li>
 * </ul>
 *
 * The original 1.20 default ambient ore set referenced deepslate variants that do
 * not exist in 1.12.2; this is the equivalent 1.12.2 ore set with gem-matched tints.
 */
@SideOnly(Side.CLIENT)
public final class ParticleEmitters {

    private static final int STEP_INTERVAL = 10;
    private static final int AMBIENT_INTERVAL = 10;

    /** Ore block -> sparkle tint (packed RGB). */
    private static final Map<Block, Integer> ORE_SPARKLE = new IdentityHashMap<Block, Integer>();

    static {
        // Modern per-ore sparkle tints (gem-matched). Resolved from the 1.20.1 default
        // BlockAmbientParticleConfig via mappings:
        //   GOLD ores    -> 16711613 = 0xFEFFBD (pale yellow)
        //   DIAMOND ores -> 11861486 = 0xB4FDEE (pale aqua)
        //   EMERALD ores -> 14286827 = 0xD9FFEB (pale green)
        //   amethyst     -> n/a in 1.12.2
        // Lapis/redstone aren't in the modern set (no 1.12.2 deepslate equivalents needed);
        // kept as 1.12.2-only extras with gem-matched pale tints.
        ORE_SPARKLE.put(Blocks.GOLD_ORE, 0xFEFFBD);          // modern gold (16711613)
        ORE_SPARKLE.put(Blocks.DIAMOND_ORE, 0xB4FDEE);       // modern diamond (11861486)
        ORE_SPARKLE.put(Blocks.EMERALD_ORE, 0xD9FFEB);       // modern emerald (14286827)
        ORE_SPARKLE.put(Blocks.LAPIS_ORE, 0x8FAEF0);         // lapis blue (1.12.2-only), slightly darker
        ORE_SPARKLE.put(Blocks.REDSTONE_ORE, 0xFFC0C0);      // pale red (1.12.2-only)
        ORE_SPARKLE.put(Blocks.LIT_REDSTONE_ORE, 0xFFC0C0);
    }

    private ParticleEmitters() {
    }

    public static int getStepInterval() {
        return STEP_INTERVAL;
    }

    /** Walking on / landing on a block (called from MixinBlock fallOn/stepOn). */
    public static void blockStep(int amount, World world, IBlockState state, BlockPos pos, Entity entity) {
        if (!VisualityConfig.soulEnabled || entity.isSneaking()) {
            return;
        }
        if (state.getBlock() != Blocks.SOUL_SAND) {
            return;
        }
        double y = entity.posY + 0.0625;
        for (int i = 0; i < amount; i++) {
            double x = pos.getX() + world.rand.nextDouble();
            double z = pos.getZ() + world.rand.nextDouble();
            SoulParticle.spawn(world, x, y, z);
        }
    }

    /** Random client display tick on a block (called from MixinBlock animateTick). */
    public static void blockAmbient(IBlockState state, World world, BlockPos pos, Random rand) {
        Block block = state.getBlock();

        if (VisualityConfig.shinyBlocksEnabled && VisualityParticles.sparkle() != null) {
            Integer color = ORE_SPARKLE.get(block);
            if (color != null) {
                // Modern BlockAmbientParticleConfig: interval=10 with all 6 directions, so
                // i = rand(10); spawn when i < 6 (~60% per display tick) on face[i]. The old
                // port used rand(10)==0 on ONE random face (~10%, often occlusion-skipped),
                // making sparkles ~6x too rare. This restores the dense 1.20 look.
                EnumFacing[] faces = EnumFacing.values(); // DOWN,UP,NORTH,SOUTH,WEST,EAST
                int i = rand.nextInt(AMBIENT_INTERVAL);
                if (i < faces.length) {
                    float r = ((color >> 16) & 0xFF) / 255.0f;
                    float g = ((color >> 8) & 0xFF) / 255.0f;
                    float b = (color & 0xFF) / 255.0f;
                    EnumFacing face = faces[i];
                    if (state.isOpaqueCube()) {
                        BlockPos facePos = pos.offset(face);
                        if (!world.getBlockState(facePos).isOpaqueCube()) {
                            EnumFacing.Axis axis = face.getAxis();
                            double x = pos.getX() + (axis == EnumFacing.Axis.X
                                    ? 0.5 + 0.5625 * face.getXOffset() : rand.nextDouble());
                            double y = pos.getY() + (axis == EnumFacing.Axis.Y
                                    ? 0.5 + 0.5625 * face.getYOffset() : rand.nextDouble());
                            double z = pos.getZ() + (axis == EnumFacing.Axis.Z
                                    ? 0.5 + 0.5625 * face.getZOffset() : rand.nextDouble());
                            SparkleParticle.spawn(world, x, y, z, r, g, b);
                        }
                    } else {
                        double x = pos.getX() + rand.nextDouble();
                        double y = pos.getY() + rand.nextDouble();
                        double z = pos.getZ() + rand.nextDouble();
                        SparkleParticle.spawn(world, x, y, z, r, g, b);
                    }
                }
            }
        }

        if (VisualityConfig.soulEnabled && block == Blocks.SOUL_SAND
                && VisualityParticles.soul() != null && rand.nextInt(AMBIENT_INTERVAL) == 0) {
            double x = pos.getX() + rand.nextDouble();
            double z = pos.getZ() + rand.nextDouble();
            SoulParticle.spawn(world, x, pos.getY() + 1.0625, z);
        }
    }
}
