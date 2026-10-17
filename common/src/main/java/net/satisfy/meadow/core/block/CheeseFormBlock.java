package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.util.ShapeUtil;
import net.satisfy.meadow.core.block.entity.CheeseFormBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class CheeseFormBlock extends FacingBlock implements EntityBlock {
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.box(2.0, 6.0, 7.0, 14.0, 12.0, 9.0)
    );

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_NORTH));
        }
    });

    public CheeseFormBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE.get(state.getValue(FACING));
        return shape == null ? SHAPE_NORTH : shape;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty() || !(level.getBlockEntity(pos) instanceof CheeseFormBlockEntity press)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (press.canAddMilk(level, stack)) {
            if (!level.isClientSide) {
                press.addMilk(stack);
                ItemStack remainder = stack.getItem().hasCraftingRemainingItem() ? new ItemStack(stack.getItem().getCraftingRemainingItem()) : ItemStack.EMPTY;
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                player.setItemInHand(hand, remainder.isEmpty() ? consume(stack, player) : ItemUtils.createFilledResult(stack, player, remainder));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.8F, 1.0F);
                level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (press.canAddRennet(level, stack)) {
            if (!level.isClientSide) {
                press.addRennet(stack);
                ItemStack remainder = stack.getItem().hasCraftingRemainingItem() ? new ItemStack(stack.getItem().getCraftingRemainingItem()) : ItemStack.EMPTY;
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                player.setItemInHand(hand, remainder.isEmpty() ? consume(stack, player) : ItemUtils.createFilledResult(stack, player, remainder));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static ItemStack consume(ItemStack stack, Player player) {
        stack.consume(1, player);
        return stack;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !(level.getBlockEntity(pos) instanceof CheeseFormBlockEntity press)) {
            return InteractionResult.PASS;
        }
        if (press.getResultCount() > 0) {
            if (!level.isClientSide) {
                for (ItemStack cheese : press.takeResult()) {
                    if (!player.getInventory().add(cheese)) {
                        player.drop(cheese, false);
                    }
                }
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (press.crank()) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CheeseFormBlockEntity press) {
            Containers.dropContents(level, pos, press.drops());
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheeseFormBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == EntityTypeRegistry.CHEESE_FORM_BLOCK_ENTITY.get() ? (BlockEntityTicker<T>) (BlockEntityTicker<CheeseFormBlockEntity>) CheeseFormBlockEntity::tick : null;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof CheeseFormBlockEntity press) || !press.isPressed() || press.isReady() || random.nextInt(6) != 0) {
            return;
        }
        Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        double along = (random.nextDouble() - 0.5) * 0.6;
        double x = pos.getX() + 0.5 + side.getStepX() * 0.395 + (side.getAxis() == Direction.Axis.Z ? along : 0.0);
        double z = pos.getZ() + 0.5 + side.getStepZ() * 0.395 + (side.getAxis() == Direction.Axis.X ? along : 0.0);
        level.addParticle(ParticleTypes.DRIPPING_HONEY, x, pos.getY() + 0.1, z, 0.0, -0.2, 0.0);
    }
}
