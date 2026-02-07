package net.satisfy.meadow.core.block;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class MilkCanBlock extends FacingBlock {
    public static final IntegerProperty FILL_LEVEL = IntegerProperty.create("fill_level", 0, 3);

    private static final VoxelShape BASE_SHAPE = Util.make(() -> {
        VoxelShape combinedShape = Shapes.empty();
        combinedShape = Shapes.joinUnoptimized(combinedShape, Shapes.box(0.25, 0.0, 0.25, 0.75, 0.625, 0.75), BooleanOp.OR);
        combinedShape = Shapes.joinUnoptimized(combinedShape, Shapes.box(0.3125, 0.625, 0.3125, 0.6875, 0.875, 0.6875), BooleanOp.OR);
        combinedShape = Shapes.joinUnoptimized(combinedShape, Shapes.box(0.25, 0.875, 0.25, 0.75, 1.0, 0.75), BooleanOp.OR);
        combinedShape = Shapes.joinUnoptimized(combinedShape, Shapes.box(0.1875, 0.5, 0.3125, 0.25, 0.625, 0.6875), BooleanOp.OR);
        combinedShape = Shapes.joinUnoptimized(combinedShape, Shapes.box(0.75, 0.5, 0.3125, 0.8125, 0.625, 0.6875), BooleanOp.OR);
        return combinedShape;
    });

    private static final Map<Direction, VoxelShape> SHAPES = Util.make(() -> {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        shapes.put(Direction.NORTH, BASE_SHAPE);
        shapes.put(Direction.EAST, GeneralUtil.rotateShape(Direction.NORTH, Direction.EAST, BASE_SHAPE));
        shapes.put(Direction.SOUTH, GeneralUtil.rotateShape(Direction.NORTH, Direction.SOUTH, BASE_SHAPE));
        shapes.put(Direction.WEST, GeneralUtil.rotateShape(Direction.NORTH, Direction.WEST, BASE_SHAPE));
        return shapes;
    });

    public MilkCanBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FILL_LEVEL, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        VoxelShape shape = SHAPES.get(state.getValue(FACING));
        return shape == null ? BASE_SHAPE : shape;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Item heldItem = itemStack.getItem();
        int fillLevel = state.getValue(FILL_LEVEL);

        boolean isMilkBucket = heldItem.equals(Items.MILK_BUCKET) || heldItem.equals(ObjectRegistry.WOODEN_MILK_BUCKET.get());
        boolean isEmptyBucket = heldItem.equals(Items.BUCKET) || heldItem.equals(ObjectRegistry.WOODEN_BUCKET.get());

        if (isMilkBucket && fillLevel < 3) {
            if (!level.isClientSide()) {
                boolean isWoodenMilkBucket = heldItem.equals(ObjectRegistry.WOODEN_MILK_BUCKET.get());
                ItemStack returnedBucket = new ItemStack(isWoodenMilkBucket ? ObjectRegistry.WOODEN_BUCKET.get() : Items.BUCKET);
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, returnedBucket));
                player.awardStat(Stats.ITEM_USED.get(heldItem));
                level.setBlockAndUpdate(blockPos, state.setValue(FILL_LEVEL, fillLevel + 1));
                level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        if (isEmptyBucket && fillLevel > 0) {
            if (!level.isClientSide()) {
                boolean isWoodenEmptyBucket = heldItem.equals(ObjectRegistry.WOODEN_BUCKET.get());
                ItemStack filledMilkBucket = new ItemStack(isWoodenEmptyBucket ? ObjectRegistry.WOODEN_MILK_BUCKET.get() : Items.MILK_BUCKET);
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, filledMilkBucket));
                player.awardStat(Stats.ITEM_USED.get(heldItem));
                level.setBlockAndUpdate(blockPos, state.setValue(FILL_LEVEL, fillLevel - 1));
                level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILL_LEVEL);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(FILL_LEVEL) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random) {
        int fillLevel = state.getValue(FILL_LEVEL);
        if (fillLevel >= 3) {
            return;
        }

        AABB searchBox = new AABB(blockPos).inflate(3.0);
        if (level.getEntities(EntityType.COW, searchBox, cow -> true).isEmpty()) {
            return;
        }

        if (random.nextFloat() < 0.02f) {
            level.setBlockAndUpdate(blockPos, state.setValue(FILL_LEVEL, fillLevel + 1));
            level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
        }
    }
}