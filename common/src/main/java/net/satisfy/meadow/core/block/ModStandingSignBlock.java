package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.satisfy.meadow.core.block.entity.ModSignBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ModStandingSignBlock extends StandingSignBlock {
    public ModStandingSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModSignBlockEntity(pos, state);
    }
}
