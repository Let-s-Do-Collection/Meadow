package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.block.WardrobeBlockEntity;
import net.satisfy.foundation.util.ImplementedInventory;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;

public class MeadowWardrobeBlockEntity extends WardrobeBlockEntity implements ImplementedInventory {
    public MeadowWardrobeBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.WARDROBE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return getInventory();
    }
}
