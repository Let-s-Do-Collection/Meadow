package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.util.ShapeUtil;
import net.satisfy.meadow.client.photo.CameraFlash;
import net.satisfy.meadow.core.block.entity.CameraBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import net.satisfy.meadow.platform.PlatformHelper;

public class CameraBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final int FLASH_EVENT = 1;
    private static final double LENS_OFFSET = 0.65D;
    private static final double LENS_HEIGHT = 1.19D;

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Shapes.box(0.25, 0.9375, 0.1875, 0.75, 1.4375, 0.8125),
            Shapes.box(0.4375, 1.125, -0.125, 0.5625, 1.25, 0.1875)
    );

    private static final Map<Direction, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_NORTH));
        }
    });

    public CameraBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(TRIGGERED, false));
    }

    public static Vec3 lens(BlockPos pos, Direction facing) {
        return new Vec3(pos.getX() + 0.5D + LENS_OFFSET * facing.getStepX(), pos.getY() + LENS_HEIGHT, pos.getZ() + 0.5D + LENS_OFFSET * facing.getStepZ());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.PAPER) || !(level.getBlockEntity(pos) instanceof CameraBlockEntity camera)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (camera.getPaper() >= PlatformHelper.getCameraMaxPaper()) {
            return ItemInteractionResult.CONSUME;
        }
        if (!level.isClientSide) {
            camera.addPaper();
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 0.8F, 1.1F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CameraBlockEntity camera)) {
            return InteractionResult.PASS;
        }
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide) {
                camera.toggleTimer(player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (state.getValue(POWERED) || camera.isCounting()) {
            return InteractionResult.CONSUME;
        }
        if (level instanceof ServerLevel serverLevel) {
            camera.shoot(serverLevel, player.getUUID());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moved) {
        boolean powered = level.hasNeighborSignal(pos);
        boolean triggered = state.getValue(TRIGGERED);
        if (powered == triggered) {
            return;
        }
        level.setBlock(pos, state.setValue(TRIGGERED, powered), Block.UPDATE_CLIENTS);
        if (powered && !state.getValue(POWERED) && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof CameraBlockEntity camera && !camera.isCounting()) {
            camera.shoot(serverLevel, null);
        }
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        if (id != FLASH_EVENT) {
            return super.triggerEvent(state, level, pos, id, param);
        }
        if (level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Vec3 lens = lens(pos, facing);
            level.addParticle(ParticleTypes.FLASH, lens.x + facing.getStepX() * 0.15, lens.y, lens.z + facing.getStepZ() * 0.15, 0.0, 0.0, 0.0);
            for (int i = 0; i < 5; i++) {
                level.addParticle(ParticleTypes.SMOKE, lens.x + (level.random.nextDouble() - 0.5) * 0.2, lens.y + 0.1, lens.z + (level.random.nextDouble() - 0.5) * 0.2, 0.0, 0.01, 0.0);
                level.addParticle(ParticleTypes.FIREWORK, lens.x, lens.y, lens.z, facing.getStepX() * 0.05 + (level.random.nextDouble() - 0.5) * 0.04, (level.random.nextDouble() - 0.5) * 0.04, facing.getStepZ() * 0.05 + (level.random.nextDouble() - 0.5) * 0.04);
            }
            if (param == 1) {
                CameraFlash.onFlash(level, lens, facing);
            }
        }
        return true;
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            world.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_ALL);
            world.updateNeighborsAt(pos, this);
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof CameraBlockEntity camera && camera.getPaper() > 0) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.PAPER, camera.getPaper()));
        }
        if (!moved && !state.is(newState.getBlock()) && state.getValue(POWERED)) {
            world.updateNeighborsAt(pos, this);
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CameraBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == EntityTypeRegistry.CAMERA_BLOCK_ENTITY.get() ? (BlockEntityTicker<T>) (BlockEntityTicker<CameraBlockEntity>) CameraBlockEntity::tick : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, TRIGGERED);
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
