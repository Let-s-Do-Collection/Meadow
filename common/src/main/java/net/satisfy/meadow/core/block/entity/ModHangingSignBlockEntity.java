package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class ModHangingSignBlockEntity extends ModSignBlockEntity {

    public ModHangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EntityTypeRegistry.MOD_HANGING_SIGN.get(), blockPos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return EntityTypeRegistry.MOD_HANGING_SIGN.get();
    }
}
