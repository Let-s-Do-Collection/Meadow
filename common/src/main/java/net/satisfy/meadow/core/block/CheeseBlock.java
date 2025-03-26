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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class CheeseBlock extends FacingBlock {
    private static final VoxelShape SHAPE_GRAIN = Block.box(4, 0, 4, 12, 5, 12);
    private static final VoxelShape SHAPE_CHEESE = Block.box(2, 0, 2, 14, 6, 14);
    private static final VoxelShape SHAPE_WARPED = Block.box(3, 0, 3, 13, 7, 13);
    private static final VoxelShape SHAPE_BUFFALO = Block.box(4, 0, 4, 12, 4, 12);
    private static final VoxelShape SHAPE_SHEEP = Block.box(5, 0, 5, 11, 6, 11);
    private static final VoxelShape SHAPE_CAKE = Block.box(2, 0, 2, 14, 4, 14);

    public static final IntegerProperty CUTS = IntegerProperty.create("cuts", 0, 3);
    private final RegistrySupplier<Item> slice;
    private final CheeseType cheeseType;

    public CheeseBlock(Properties settings, RegistrySupplier<Item> slice, CheeseType cheeseType) {
        super(settings);
        this.slice = slice;
        this.cheeseType = cheeseType;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CUTS);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (cheeseType) {
            case GRAIN -> SHAPE_GRAIN;
            case WARPED -> SHAPE_WARPED;
            case BUFFALO, GOAT -> SHAPE_BUFFALO;
            case SHEEP -> SHAPE_SHEEP;
            case CAKE -> SHAPE_CAKE;
            default -> SHAPE_CHEESE;
        };
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide) {
            if (tryEat(world, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (itemStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return tryEat(world, pos, state, player);
    }

    private InteractionResult tryEat(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
        world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1, 1);
        CheeseBlock.popResourceFromFace((Level) world, pos, Direction.UP, new ItemStack(slice.get()));
        int i = state.getValue(CUTS);
        world.gameEvent(player, GameEvent.EAT, pos);
        if (i < 3) {
            world.setBlock(pos, state.setValue(CUTS, i + 1), Block.UPDATE_ALL);
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
        GRAIN, REGULAR, WARPED, BUFFALO, GOAT, SHEEP, CAKE
    }
}
