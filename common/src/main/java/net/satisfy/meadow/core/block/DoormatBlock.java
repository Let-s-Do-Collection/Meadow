package net.satisfy.meadow.core.block;

import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.util.ShapeUtil;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.satisfy.foundation.registry.FoundationParticles;
import net.minecraft.sounds.SoundSource;
import net.satisfy.meadow.client.MeadowClient;
import net.satisfy.meadow.core.block.entity.DoormatBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class DoormatBlock extends LineConnectingBlock implements EntityBlock {
    private static final VoxelShape SHAPE_MIDDLE = Block.box(0, 0, 3, 16, 1, 13);
    private static final VoxelShape SHAPE_SINGLE = Block.box(1, 0, 3, 15, 1, 13);
    private static final VoxelShape SHAPE_RIGHT = Block.box(0, 0, 3, 15, 1, 13);
    private static final VoxelShape SHAPE_LEFT = Block.box(1, 0, 3, 16, 1, 13);

    private static final Map<Direction, Map<LineConnectingType, VoxelShape>> SHAPES = new EnumMap<>(Direction.class);

    static {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Map<LineConnectingType, VoxelShape> shapesForDirection = new EnumMap<>(LineConnectingType.class);
            shapesForDirection.put(LineConnectingType.MIDDLE, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_MIDDLE));
            shapesForDirection.put(LineConnectingType.LEFT, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_LEFT));
            shapesForDirection.put(LineConnectingType.RIGHT, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_RIGHT));
            shapesForDirection.put(LineConnectingType.NONE, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_SINGLE));
            SHAPES.put(direction, shapesForDirection);
        }
    }

    public DoormatBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        LineConnectingType type = state.getValue(TYPE);
        return SHAPES.get(facing).get(type);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DoormatBlockEntity(pos, state);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DoormatBlockEntity doormat)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean changed;
        if (stack.getItem() instanceof DyeItem dye) {
            changed = level instanceof ServerLevel serverLevel && doormat.setColor(dye.getDyeColor());
            if (changed) {
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                ColorParticleOption splash = ColorParticleOption.create(FoundationParticles.DYE_SPLASH.get(), FastColor.ARGB32.opaque(dye.getDyeColor().getTextureDiffuseColor()));
                ((ServerLevel) level).sendParticles(splash, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 12, 0.3, 0.02, 0.3, 0.06);
            }
        } else if (stack.is(Items.GLOW_INK_SAC)) {
            changed = !level.isClientSide && doormat.setGlowing(true);
            if (changed) {
                level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else if (stack.is(Items.INK_SAC)) {
            changed = !level.isClientSide && doormat.setGlowing(false);
            if (changed) {
                level.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (changed && !player.isCreative()) {
            stack.shrink(1);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DoormatBlockEntity doormat)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            MeadowClient.openDoormatScreen(doormat);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
