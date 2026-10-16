package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.meadow.core.block.entity.MeadowStorageBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.Nullable;

public abstract class MeadowStorageBlock extends StorageBlock {
    public MeadowStorageBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockEntityType<?> blockEntityType() {
        return EntityTypeRegistry.STORAGE_ENTITY.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MeadowStorageBlockEntity(pos, state, size());
    }
}
