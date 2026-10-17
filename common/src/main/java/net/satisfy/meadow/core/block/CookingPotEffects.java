package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.meadow.core.block.entity.CookingCauldronBlockEntity;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;

import java.util.function.ToIntFunction;

public final class CookingPotEffects {
    public static ToIntFunction<CookingCauldronBlockEntity> colorProvider = CookingCauldronBlockEntity::getLiquidColor;

    private CookingPotEffects() {
    }

    public static void animate(Level level, BlockPos pos, RandomSource random, CookingCauldronBlockEntity pot) {
        if (!pot.isHeated()) {
            return;
        }
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        double surfaceY = pos.getY() + pot.surfaceY();
        boolean hasLiquid = pot.getLiquid() != null;

        if (pot.isPotEmpty()) {
            if (random.nextInt(100) < 18) {
                level.addParticle(ParticleTypes.SMOKE, centerX, surfaceY + 0.5, centerZ, 0.0, 0.05, 0.0);
            }
            return;
        }

        int color = 0xFF000000 | colorProvider.applyAsInt(pot);

        if (pot.isCooking()) {
            if (hasLiquid && random.nextInt(100) < 95) {
                int bubbles = 2 + random.nextInt(3);
                for (int i = 0; i < bubbles; i++) {
                    double x = centerX + (random.nextDouble() - 0.5) * 0.35;
                    double z = centerZ + (random.nextDouble() - 0.5) * 0.35;
                    level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_SOUP_BUBBLE.get(), color), x, surfaceY, z, 0.0, 0.0, 0.0);
                    level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_SOUP_COOKING_BUBBLE.get(), color), x, surfaceY, z, 0.0, 0.0, 0.0);
                }
            }
            if (pot.hasFoam()) {
                for (int i = 0; i < 2; i++) {
                    double x = centerX + (random.nextDouble() - 0.5) * 0.3;
                    double z = centerZ + (random.nextDouble() - 0.5) * 0.3;
                    level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_SOUP_BUBBLE.get(), 0xFFF4F1E8), x, surfaceY + 0.02, z, 0.0, 0.0, 0.0);
                }
                if (random.nextInt(4) == 0) {
                    level.playLocalSound(centerX, surfaceY, centerZ, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.6F, 0.7F + random.nextFloat() * 0.3F, false);
                }
            }
            if (random.nextInt(100) < 80) {
                int steam = 2 + random.nextInt(3);
                for (int i = 0; i < steam; i++) {
                    double x = centerX + (random.nextDouble() - 0.5) * 0.35;
                    double z = centerZ + (random.nextDouble() - 0.5) * 0.35;
                    steam(level, pot, random, x, surfaceY + 0.3, z);
                }
            }
            if (random.nextInt(100) < 15) {
                level.addParticle(ParticleTypes.SMOKE, centerX, surfaceY + 0.45, centerZ, 0.0, 0.06, 0.0);
            }
            if (random.nextInt(100) < 6) {
                level.playLocalSound(centerX, surfaceY, centerZ, SoundEventRegistry.COOKING_POT_BOILING.get(), SoundSource.BLOCKS, 0.75F, 0.75F, false);
            }
            return;
        }

        if (random.nextInt(100) < 24) {
            level.addParticle(ParticleTypes.SMOKE, centerX, surfaceY + 0.45, centerZ, 0.0, 0.05, 0.0);
        }
        if (hasLiquid && random.nextInt(100) < 38) {
            double x = centerX + (random.nextDouble() - 0.5) * 0.3;
            double z = centerZ + (random.nextDouble() - 0.5) * 0.3;
            steam(level, pot, random, x, surfaceY + 0.3, z);
        }
    }

    private static void steam(Level level, CookingCauldronBlockEntity pot, RandomSource random, double x, double y, double z) {
        BlockState state = pot.getBlockState();
        if (!state.is(ObjectRegistry.COOKING_FRAME.get())) {
            level.addParticle(FoundationParticles.SOUP_STEAM.get(), x, y, z, 0.0, 0.07, 0.0);
            return;
        }
        Direction drift = random.nextBoolean() ? state.getValue(CookingFrameBlock.FACING) : state.getValue(CookingFrameBlock.FACING).getOpposite();
        double offset = 0.08 + random.nextDouble() * 0.04;
        double speed = 0.04 + random.nextDouble() * 0.02;
        level.addParticle(FoundationParticles.SOUP_STEAM.get(), x + drift.getStepX() * offset, y, z + drift.getStepZ() * offset, drift.getStepX() * speed, 0.07, drift.getStepZ() * speed);
    }
}
