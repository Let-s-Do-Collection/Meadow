package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.satisfy.meadow.core.block.entity.PineHangingSignBlockEntity;
import org.jetbrains.annotations.NotNull;

public class PineCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public PineCeilingHangingSignBlock(Properties properties, WoodType type) {
        super(type, properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PineHangingSignBlockEntity(pos, state);
    }
}