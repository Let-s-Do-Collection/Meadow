package net.satisfy.meadow.core.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class CheeseWheelBlock extends FacingBlock {
    public static final IntegerProperty CUTS = IntegerProperty.create("cuts", 0, 3);

    private static final VoxelShape SHAPE_GRAIN = Block.box(4, 0, 4, 12, 5, 12);

    private static final VoxelShape SHAPE_WARPED_0 = Shapes.or(
            Block.box(8, 0, 8, 13, 7, 13),
            Block.box(3, 0, 8, 8, 7, 13),
            Block.box(8, 0, 3, 13, 7, 8),
            Block.box(3, 0, 3, 8, 7, 8)
    );

    private static final VoxelShape SHAPE_WARPED_1 = Shapes.or(
            Block.box(8, 0, 8, 13, 7, 13),
            Block.box(3, 0, 8, 8, 7, 13),
            Block.box(8, 0, 3, 13, 7, 8)
    );

    private static final VoxelShape SHAPE_WARPED_2 = Shapes.or(
            Block.box(8, 0, 8, 13, 7, 13),
            Block.box(3, 0, 8, 8, 7, 13)
    );

    private static final VoxelShape SHAPE_WARPED_3 = Block.box(8, 0, 8, 13, 7, 13);

    private static final VoxelShape[] WARPED_SHAPES_BY_CUTS = new VoxelShape[]{
            SHAPE_WARPED_0,
            SHAPE_WARPED_1,
            SHAPE_WARPED_2,
            SHAPE_WARPED_3
    };

    private static final VoxelShape SHAPE_BUFFALO_GOAT_0 = Shapes.or(
            Block.box(8, 0, 4, 12, 4, 8),
            Block.box(4, 0, 4, 8, 4, 8),
            Block.box(4, 0, 8, 8, 4, 12),
            Block.box(8, 0, 8, 12, 4, 12)
    );

    private static final VoxelShape SHAPE_BUFFALO_GOAT_1 = Shapes.or(
            Block.box(8, 0, 4, 12, 4, 8),
            Block.box(4, 0, 8, 8, 4, 12),
            Block.box(8, 0, 8, 12, 4, 12)
    );

    private static final VoxelShape SHAPE_BUFFALO_GOAT_2 = Shapes.or(
            Block.box(4, 0, 8, 8, 4, 12),
            Block.box(8, 0, 8, 12, 4, 12)
    );

    private static final VoxelShape SHAPE_BUFFALO_GOAT_3 = Block.box(8, 0, 8, 12, 4, 12);

    private static final VoxelShape[] BUFFALO_GOAT_SHAPES_BY_CUTS = new VoxelShape[]{
            SHAPE_BUFFALO_GOAT_0,
            SHAPE_BUFFALO_GOAT_1,
            SHAPE_BUFFALO_GOAT_2,
            SHAPE_BUFFALO_GOAT_3
    };

    private static final VoxelShape SHAPE_CHEESE_AMETHYST_0 = Shapes.or(
            Block.box(2, 0, 2, 8, 6, 8),
            Block.box(2, 0, 8, 8, 6, 14),
            Block.box(8, 0, 8, 14, 6, 14),
            Block.box(8, 0, 2, 14, 6, 8)
    );

    private static final VoxelShape SHAPE_CHEESE_AMETHYST_1 = Shapes.or(
            Block.box(2, 0, 8, 8, 6, 14),
            Block.box(8, 0, 8, 14, 6, 14),
            Block.box(8, 0, 2, 14, 6, 8)
    );

    private static final VoxelShape SHAPE_CHEESE_AMETHYST_2 = Shapes.or(
            Block.box(2, 0, 8, 8, 6, 14),
            Block.box(8, 0, 8, 14, 6, 14)
    );

    private static final VoxelShape SHAPE_CHEESE_AMETHYST_3 = Block.box(8, 0, 8, 14, 6, 14);

    private static final VoxelShape[] CHEESE_AMETHYST_SHAPES_BY_CUTS = new VoxelShape[]{
            SHAPE_CHEESE_AMETHYST_0,
            SHAPE_CHEESE_AMETHYST_1,
            SHAPE_CHEESE_AMETHYST_2,
            SHAPE_CHEESE_AMETHYST_3
    };

    private static final VoxelShape SHAPE_SHEEP_0 = Shapes.or(
            Block.box(5, 0, 5, 8, 6, 8),
            Block.box(8, 0, 5, 11, 6, 8),
            Block.box(8, 0, 8, 11, 6, 11),
            Block.box(5, 0, 8, 8, 6, 11)
    );

    private static final VoxelShape SHAPE_SHEEP_1 = Shapes.or(
            Block.box(8, 0, 5, 11, 6, 8),
            Block.box(8, 0, 8, 11, 6, 11),
            Block.box(5, 0, 8, 8, 6, 11)
    );

    private static final VoxelShape SHAPE_SHEEP_2 = Shapes.or(
            Block.box(8, 0, 8, 11, 6, 11),
            Block.box(5, 0, 8, 8, 6, 11)
    );

    private static final VoxelShape SHAPE_SHEEP_3 = Block.box(8, 0, 8, 11, 6, 11);

    private static final VoxelShape[] SHEEP_SHAPES_BY_CUTS = new VoxelShape[]{
            SHAPE_SHEEP_0,
            SHAPE_SHEEP_1,
            SHAPE_SHEEP_2,
            SHAPE_SHEEP_3
    };

    private static final VoxelShape[][] WARPED_SHAPES_BY_FACING_AND_CUTS = new VoxelShape[4][4];
    private static final VoxelShape[][] BUFFALO_GOAT_SHAPES_BY_FACING_AND_CUTS = new VoxelShape[4][4];
    private static final VoxelShape[][] CHEESE_AMETHYST_SHAPES_BY_FACING_AND_CUTS = new VoxelShape[4][4];
    private static final VoxelShape[][] SHEEP_SHAPES_BY_FACING_AND_CUTS = new VoxelShape[4][4];

    static {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int facingIndex = facing.get2DDataValue();
            for (int cuts = 0; cuts <= 3; cuts++) {
                WARPED_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts] = GeneralUtil.rotateShape(Direction.NORTH, facing, WARPED_SHAPES_BY_CUTS[cuts]);
                BUFFALO_GOAT_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts] = GeneralUtil.rotateShape(Direction.NORTH, facing, BUFFALO_GOAT_SHAPES_BY_CUTS[cuts]);
                CHEESE_AMETHYST_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts] = GeneralUtil.rotateShape(Direction.NORTH, facing, CHEESE_AMETHYST_SHAPES_BY_CUTS[cuts]);
                SHEEP_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts] = GeneralUtil.rotateShape(Direction.NORTH, facing, SHEEP_SHAPES_BY_CUTS[cuts]);
            }
        }
    }

    private final RegistrySupplier<Item> slice;
    private final CheeseType cheeseType;

    public CheeseWheelBlock(Properties settings, RegistrySupplier<Item> slice, CheeseType cheeseType) {
        super(settings);
        this.slice = slice;
        this.cheeseType = cheeseType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CUTS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CUTS);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        int cuts = state.getValue(CUTS);
        int facingIndex = facing.get2DDataValue();

        return switch (cheeseType) {
            case WARPED -> WARPED_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts];
            case BUFFALO, GOAT -> BUFFALO_GOAT_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts];
            case SHEEP -> SHEEP_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts];
            case REGULAR, AMETHYST -> CHEESE_AMETHYST_SHAPES_BY_FACING_AND_CUTS[facingIndex][cuts];
            case GRAIN -> GeneralUtil.rotateShape(Direction.NORTH, facing, SHAPE_GRAIN);
            case CAKE -> GeneralUtil.rotateShape(Direction.NORTH, facing, Block.box(2, 0, 2, 14, 4, 14));
        };
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        InteractionHand usedHand = player.getUsedItemHand();
        ItemStack heldStack = player.getItemInHand(usedHand);

        if (world.isClientSide) {
            if (tryEat(world, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (heldStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return tryEat(world, pos, state, player);
    }

    private InteractionResult tryEat(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
        world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1, 1);
        Block.popResourceFromFace((Level) world, pos, Direction.UP, new ItemStack(slice.get()));

        int cuts = state.getValue(CUTS);
        world.gameEvent(player, GameEvent.EAT, pos);

        if (cuts < 3) {
            world.setBlock(pos, state.setValue(CUTS, cuts + 1), Block.UPDATE_ALL);
        } else {
            world.removeBlock(pos, false);
            world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isSolid();
    }

    public enum CheeseType {
        GRAIN, REGULAR, AMETHYST, WARPED, BUFFALO, GOAT, SHEEP, CAKE
    }
}