package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FlowerPotBigBlock extends StorageBlock {
    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> Shapes.box(0.25, 0.0, 0.25, 0.75, 0.5625, 0.75);
    private static final VoxelShape SHAPE;

    public FlowerPotBigBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public int size() {
        return 1;
    }

    public ResourceLocation type() {
        return StorageTypeRegistry.FLOWER_POT_BIG;
    }

    public Direction[] unAllowedDirections() {
        return new Direction[0];
    }

    public boolean canInsertStack(ItemStack stack) {
        return stack.is(ItemTags.TALL_FLOWERS);
    }

    public int getSection(Float x, Float y) {
        return 0;
    }

    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public SoundEvent getAddSound(Level level, BlockPos blockPos, Player player, int i) {
        return SoundEvents.GRASS_PLACE;
    }

    public SoundEvent getRemoveSound(Level level, BlockPos blockPos, Player player, int i) {
        return SoundEvents.GRASS_BREAK;
    }

    static {
        SHAPE = voxelShapeSupplier.get();
    }
}

