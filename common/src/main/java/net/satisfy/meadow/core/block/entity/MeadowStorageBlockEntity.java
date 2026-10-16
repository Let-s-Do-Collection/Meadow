package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.util.ImplementedInventory;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;

public class MeadowStorageBlockEntity extends StorageBlockEntity implements ImplementedInventory {
    public MeadowStorageBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.STORAGE_ENTITY.get(), pos, state);
    }

    public MeadowStorageBlockEntity(BlockPos pos, BlockState state, int size) {
        super(EntityTypeRegistry.STORAGE_ENTITY.get(), pos, state, size);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return getInventory();
    }
}
