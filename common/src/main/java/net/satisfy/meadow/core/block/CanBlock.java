package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.item.WoodenBucket;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CanBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0, 0.25, 0.75, 0.625, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.921875, 0.28125, 0.375, 0.984375, 0.28125, 0.4375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.984375, 0.28125, 0.375, 1.046875, 0.28125, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.921875, 0.28125, 0.5625, 0.984375, 0.28125, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 0.65625, 0.375, 0, 0.65625, 0.4375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.125, 0.65625, 0.375, -0.0625, 0.65625, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 0.65625, 0.5625, 0, 0.65625, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.875, 0.625, 0.75, 0.9375, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.875, 0.25, 0.75, 0.9375, 0.375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.625, 0.3125, 0.375, 0.875, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.625, 0.3125, 0.6875, 0.875, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0.625, 0.3125, 0.625, 0.875, 0.375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0.625, 0.625, 0.625, 0.875, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.875, 0.375, 0.375, 0.9375, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.875, 0.375, 0.75, 0.9375, 0.625), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    public static final IntegerProperty FLUID = IntegerProperty.create("fluid", 0, 2);


    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }


    public CanBlock(Properties settings) {
        super(settings);
        registerDefaultState(this.defaultBlockState().setValue(FLUID, 0));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        if (!(item instanceof BucketItem || item instanceof MilkBucketItem || item instanceof WoodenBucket)) {
            return InteractionResult.PASS;
        }

        boolean water;
        boolean wood = isWooden(item);
        if (state.getValue(FLUID) == 0 && ((water = isWater(item)) || isMilk(item))) {
            if(!world.isClientSide()){
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(wood ? ObjectRegistry.WOODEN_BUCKET.get() : Items.BUCKET)));
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, state.setValue(FLUID, water ? 2 : 1));
                world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.sidedSuccess(world.isClientSide());
        } else if ((state.getValue(FLUID) == 1 || state.getValue(FLUID) == 2) && (item.equals(Items.BUCKET) || item.equals(ObjectRegistry.WOODEN_BUCKET.get()))) {
            if(!world.isClientSide()){
                player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(state.getValue(FLUID) == 2 ? wood ? ObjectRegistry.WOODEN_WATER_BUCKET.get() : Items.WATER_BUCKET : wood ? ObjectRegistry.WOODEN_MILK_BUCKET.get() : Items.MILK_BUCKET)));
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, state.setValue(FLUID, 0));
                world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.sidedSuccess(world.isClientSide());
        }

        return InteractionResult.PASS;
    }

    public boolean isMilk(Item item){
        return item.equals(Items.MILK_BUCKET) || item.equals(ObjectRegistry.WOODEN_MILK_BUCKET.get());
    }

    public boolean isWooden(Item item){
        return item.equals(ObjectRegistry.WOODEN_WATER_BUCKET.get()) || item.equals(ObjectRegistry.WOODEN_MILK_BUCKET.get()) || item.equals(ObjectRegistry.WOODEN_BUCKET.get());
    }

    public boolean isWater(Item item) {
        return item.equals(Items.WATER_BUCKET) || item.equals(ObjectRegistry.WOODEN_WATER_BUCKET.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLUID, FACING);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }


    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}