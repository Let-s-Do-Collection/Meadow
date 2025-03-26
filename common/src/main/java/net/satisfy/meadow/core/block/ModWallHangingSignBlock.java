package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.satisfy.meadow.core.block.entity.ModHangingSignBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ModWallHangingSignBlock extends WallHangingSignBlock {
    public ModWallHangingSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModHangingSignBlockEntity(pos, state);
    }
}
