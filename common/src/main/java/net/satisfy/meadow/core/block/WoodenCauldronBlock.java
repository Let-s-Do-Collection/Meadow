package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class WoodenCauldronBlock
        extends AbstractCauldronBlock {
    public static final MapCodec<WoodenCauldronBlock> CODEC = simpleCodec(WoodenCauldronBlock::new);

    public WoodenCauldronBlock(Properties properties) {
        super(properties, GeneralUtil.WoodenCauldronBehavior.EMPTY);
    }

    @Override
    protected @NotNull MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isFull(BlockState state) {
        return false;
    }

    protected static boolean canFillWithPrecipitation(Level world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.05f;
        }
        if (precipitation == Biome.Precipitation.SNOW) {
            return world.getRandom().nextFloat() < 0.1f;
        }
        return false;
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if (!WoodenCauldronBlock.canFillWithPrecipitation(world, precipitation)) {
            return;
        }
        if (precipitation == Biome.Precipitation.RAIN) {
            world.setBlockAndUpdate(pos, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState());
            world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        } else if (precipitation == Biome.Precipitation.SNOW) {
            world.setBlockAndUpdate(pos, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
            world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid fluid) {
        return true;
    }

    @Override
    protected void receiveStalactiteDrip(BlockState state, Level world, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER) {
            BlockState blockState = ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState();
            world.setBlockAndUpdate(pos, blockState);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
            world.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, pos, 0);
        }
    }
}