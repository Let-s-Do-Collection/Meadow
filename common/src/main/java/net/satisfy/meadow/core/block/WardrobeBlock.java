package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.WardrobeBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WardrobeBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<WardrobeBlock> CODEC = simpleCodec(WardrobeBlock::new);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public WardrobeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected @NotNull MapCodec<? extends WardrobeBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(ctx)) {
            return defaultBlockState()
                    .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos basePos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockEntity blockEntity = level.getBlockEntity(basePos);

            if (blockEntity instanceof WardrobeBlockEntity wardrobe) {
                for (ItemStack storedStack : wardrobe.getInventory()) {
                    if (!storedStack.isEmpty()) {
                        Block.popResource(level, basePos, storedStack.copy());
                    }
                }
                for (int slotIndex = 0; slotIndex < wardrobe.getInventory().size(); slotIndex++) {
                    wardrobe.setStack(slotIndex, ItemStack.EMPTY);
                }
            }

            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(HALF) != half) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HALF);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos bePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(bePos);
        if (!(be instanceof WardrobeBlockEntity wardrobe)) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) {
            for (int i = 0; i < wardrobe.getInventory().size(); i++) {
                ItemStack stored = wardrobe.getItem(i);
                if (!stored.isEmpty()) {
                    player.addItem(stored.copy());
                    wardrobe.setStack(i, ItemStack.EMPTY);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.PASS;
        }
        toggleOpen(level, bePos);
        return InteractionResult.CONSUME;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        BlockPos bePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(bePos);
        if (!(be instanceof WardrobeBlockEntity wardrobe)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.getItem() instanceof ArmorItem armor) {
            int index = switch (armor.getEquipmentSlot()) {
                case HEAD -> WardrobeBlockEntity.SLOT_HEAD;
                case CHEST -> WardrobeBlockEntity.SLOT_CHEST;
                case LEGS -> WardrobeBlockEntity.SLOT_LEGS;
                case FEET -> WardrobeBlockEntity.SLOT_FEET;
                default -> -1;
            };
            if (index >= 0) {
                ItemStack existing = wardrobe.getItem(index);
                wardrobe.setStack(index, stack.copyWithCount(1));
                player.getItemInHand(hand).shrink(1);
                if (!existing.isEmpty()) player.addItem(existing);
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void toggleOpen(Level level, BlockPos basePos) {
        BlockState state = level.getBlockState(basePos);
        if (!state.is(this)) return;

        boolean open = !state.getValue(OPEN);
        level.setBlock(basePos, state.setValue(OPEN, open), 3);

        BlockPos otherPos = basePos.above();
        BlockState other = level.getBlockState(otherPos);
        if (other.is(this)) {
            level.setBlock(otherPos, other.setValue(OPEN, open), 3);
        }

        level.playSound(null, basePos, open ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        } else {
            VoxelShape legs = Shapes.or(
                    Block.box(0.0D, 0.0D, 0.0D, 4.0D, 2.0D, 4.0D),
                    Block.box(12.0D, 0.0D, 0.0D, 16.0D, 2.0D, 4.0D),
                    Block.box(0.0D, 0.0D, 12.0D, 4.0D, 2.0D, 16.0D),
                    Block.box(12.0D, 0.0D, 12.0D, 16.0D, 2.0D, 16.0D)
            );
            VoxelShape body = Block.box(0.0D, 2.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            return Shapes.or(legs, body);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WardrobeBlockEntity(pos, state);
    }
}
