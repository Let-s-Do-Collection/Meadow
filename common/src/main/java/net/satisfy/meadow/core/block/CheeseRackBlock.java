package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

public class CheeseRackBlock extends MeadowStorageBlock {
    public static final MapCodec<CheeseRackBlock> CODEC = simpleCodec(CheeseRackBlock::new);

    public CheeseRackBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public ResourceLocation type() {
        return StorageTypeRegistry.CHEESE_RACK;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.UP, Direction.DOWN};
    }

    @Override
    public boolean canInsertStack(ItemStack itemStack) {
        return itemStack.is(TagRegistry.CHEESE_WHEELS);
    }

    @Override
    public int getSection(Float x, Float y) {
        int index = y > 0.5 ? 1 : 0;
        if (index >= size()) {
            return Integer.MIN_VALUE;
        }
        return index;
    }
}