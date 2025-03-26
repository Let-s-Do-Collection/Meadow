package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClimbingRopeTopmountBlock extends Block {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(7, 0, 7, 9, 16, 9), Block.box(6, 8, 6, 10, 12, 10));

    public ClimbingRopeTopmountBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide) {
            for (int i = 1; i <= 12; i++) {
                BlockPos blockPos = pos.relative(Direction.DOWN, i);
                if (world.isLoaded(blockPos) && world.getBlockState(blockPos).isAir()) {
                    world.setBlockAndUpdate(blockPos, ObjectRegistry.CLIMBING_ROPE.get().defaultBlockState());
                }
            }
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClientSide()) {
            if (Block.canSupportCenter(world, pos.above(), Direction.DOWN)) {
                return state;
            }
            world.destroyBlock(pos, true);
            return world.getBlockState(pos);
        }
        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return Block.canSupportCenter(world, pos.above(), Direction.DOWN) && world.isEmptyBlock(pos.below());
    }
}
