package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class PineSignBlockEntity extends SignBlockEntity {

    public PineSignBlockEntity(BlockEntityType<? extends PineSignBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public PineSignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(EntityTypeRegistry.MOD_SIGN.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return EntityTypeRegistry.MOD_SIGN.get();
    }
}
