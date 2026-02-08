package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClimbingRopeBlock extends Block {

    public static final EnumProperty<RopePart> PART = EnumProperty.create("part", RopePart.class);

    private static final int MAX_LENGTH = 12;

    private static final VoxelShape SEGMENT_SHAPE = Block.box(7, 0, 7, 9, 16, 9);
    private static final VoxelShape TOP_SHAPE = Shapes.or(
            Block.box(7, 0, 7, 9, 16, 9),
            Block.box(6, 8, 6, 10, 14, 10)
    );
    public ClimbingRopeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, RopePart.TOP));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(PART, RopePart.TOP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }

        if (state.getValue(PART) != RopePart.TOP) {
            return;
        }

        BlockState segmentState = this.defaultBlockState().setValue(PART, RopePart.SEGMENT);

        for (int i = 1; i <= MAX_LENGTH; i++) {
            BlockPos ropePos = pos.below(i);
            if (!level.isLoaded(ropePos)) {
                break;
            }
            if (!level.getBlockState(ropePos).isAir()) {
                break;
            }
            level.setBlock(ropePos, segmentState, Block.UPDATE_ALL);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && !state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return level.getBlockState(pos);
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        RopePart part = state.getValue(PART);

        if (part == RopePart.SEGMENT) {
            BlockState aboveState = level.getBlockState(pos.above());
            return aboveState.is(this);
        }

        if (!Block.canSupportCenter(level, pos.above(), Direction.DOWN)) {
            return false;
        }

        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.isAir()) {
            return true;
        }

        return belowState.is(this) && belowState.getValue(PART) == RopePart.SEGMENT;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == RopePart.TOP ? TOP_SHAPE : SEGMENT_SHAPE;
    }

    public enum RopePart implements StringRepresentable {
        TOP("top"),
        SEGMENT("segment");

        private final String name;

        RopePart(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}