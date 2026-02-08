package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.CheeseFormBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheeseFormBlock extends BaseEntityBlock {
    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final BooleanProperty DONE = BooleanProperty.create("done");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<CheeseFormBlock> CODEC = simpleCodec(CheeseFormBlock::new);

    private static final VoxelShape BASE_SHAPE = Shapes.box(0.125, 0.0, 0.125, 0.875, 0.375, 0.875);

    public CheeseFormBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(DONE, false).setValue(WORKING, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return Block.canSupportCenter(world, pos.below(), Direction.UP);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (!world.isClientSide) {
            player.openMenu(state.getMenuProvider(world, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EntityTypeRegistry.CHEESE_FORM_BLOCK_ENTITY.get(), (level, pos, blockState, blockEntity) -> blockEntity.tick(level, pos, blockState));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CheeseFormBlockEntity cheeseFormBlockEntity) {
                Containers.dropContents(world, pos, cheeseFormBlockEntity);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheeseFormBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!state.getValue(WORKING) || random.nextInt(8) != 0) {
            return;
        }

        Direction dripSide = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        double halfSize = 0.375;
        double outward = 0.02;

        double alongSide = (random.nextDouble() - 0.5) * 0.6;

        double particleX = centerX + dripSide.getStepX() * (halfSize + outward);
        double particleZ = centerZ + dripSide.getStepZ() * (halfSize + outward);

        if (dripSide.getAxis() == Direction.Axis.X) {
            particleZ += alongSide;
        } else {
            particleX += alongSide;
        }

        double particleY = pos.getY() + 0.1;

        world.addParticle(ParticleTypes.DRIPPING_HONEY, particleX, particleY, particleZ, 0.0, -0.2, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WORKING, DONE, FACING);
    }
}