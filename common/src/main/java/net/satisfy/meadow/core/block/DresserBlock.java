package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.CabinetBlockEntity;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DresserBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    private static final Map<GeneralUtil.LineConnectingType, Supplier<VoxelShape>> SHAPES_SUPPLIERS = new HashMap<>();
    private static final Map<Direction, Map<GeneralUtil.LineConnectingType, VoxelShape>> SHAPES = new EnumMap<>(Direction.class);
    public static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;
    public static final EnumProperty<GeneralUtil.LineConnectingType> TYPE;
    public static final BooleanProperty OPEN;
    private final Supplier<SoundEvent> openSound;
    private final Supplier<SoundEvent> closeSound;

    public DresserBlock(Properties settings) {
        this(settings, null, null);
    }

    public DresserBlock(Properties settings, Supplier<SoundEvent> openSound, Supplier<SoundEvent> closeSound) {
        super(settings);
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(TYPE, GeneralUtil.LineConnectingType.NONE).setValue(OPEN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockState blockState = this.defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, world.getFluidState(clickedPos).getType() == Fluids.WATER);

        return switch (facing) {
            case EAST -> blockState.setValue(TYPE, getType(blockState, world.getBlockState(clickedPos.south()), world.getBlockState(clickedPos.north())));
            case SOUTH -> blockState.setValue(TYPE, getType(blockState, world.getBlockState(clickedPos.west()), world.getBlockState(clickedPos.east())));
            case WEST -> blockState.setValue(TYPE, getType(blockState, world.getBlockState(clickedPos.north()), world.getBlockState(clickedPos.south())));
            default -> blockState.setValue(TYPE, getType(blockState, world.getBlockState(clickedPos.east()), world.getBlockState(clickedPos.west())));
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, TYPE, OPEN);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container container) {
                Containers.dropContents(world, pos, container);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CabinetBlockEntity cabinetBlockEntity) {
            player.openMenu(cabinetBlockEntity);
        }
        return InteractionResult.CONSUME;
    }

    @SuppressWarnings("unused")
    public void playSound(Level world, BlockPos pos, boolean isOpen) {
        world.playSound(null, pos, isOpen ? openSound.get() : closeSound.get(), SoundSource.BLOCKS, 1.0f, 1.1f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CabinetBlockEntity(pos, state);
    }

    public static final MapCodec<DresserBlock> CODEC = simpleCodec(DresserBlock::new);

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.has(DataComponents.CUSTOM_NAME)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CabinetBlockEntity cabinetBlockEntity) {
                cabinetBlockEntity.setComponents(DataComponentMap.builder().set(DataComponents.CUSTOM_NAME, itemStack.getHoverName()).build());
            }
        }
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public GeneralUtil.LineConnectingType getType(BlockState state, BlockState left, BlockState right) {
        boolean leftConnects = isConnectable(left, state);
        boolean rightConnects = isConnectable(right, state);

        if (leftConnects && rightConnects) {
            return GeneralUtil.LineConnectingType.MIDDLE;
        }
        if (leftConnects) {
            return GeneralUtil.LineConnectingType.LEFT;
        }
        if (rightConnects) {
            return GeneralUtil.LineConnectingType.RIGHT;
        }
        return GeneralUtil.LineConnectingType.NONE;
    }

    protected boolean isConnectable(BlockState neighborState, BlockState selfState) {
        if (!neighborState.hasProperty(FACING) || !selfState.hasProperty(FACING)) {
            return false;
        }

        if (neighborState.getValue(FACING) != selfState.getValue(FACING)) {
            return false;
        }

        if (neighborState.getBlock() == selfState.getBlock()) {
            return true;
        }

        return (neighborState.getBlock() instanceof TableBlock && selfState.getBlock() instanceof DresserBlock)
                || (neighborState.getBlock() instanceof DresserBlock && selfState.getBlock() instanceof TableBlock);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        GeneralUtil.LineConnectingType type = switch (facing) {
            case EAST -> getType(state, world.getBlockState(pos.south()), world.getBlockState(pos.north()));
            case SOUTH -> getType(state, world.getBlockState(pos.west()), world.getBlockState(pos.east()));
            case WEST -> getType(state, world.getBlockState(pos.north()), world.getBlockState(pos.south()));
            default -> getType(state, world.getBlockState(pos.east()), world.getBlockState(pos.west()));
        };

        BlockState updatedState = state.getValue(TYPE) == type ? state : state.setValue(TYPE, type);
        if (updatedState != state) {
            world.setBlock(pos, updatedState, 3);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        GeneralUtil.LineConnectingType type = state.getValue(TYPE);
        return SHAPES.get(direction).get(type);
    }

    private static VoxelShape makeSingleShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.25, 0.1875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.0625, 0.9375, 0.1875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.75, 0.25, 0.1875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.75, 0.9375, 0.1875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeRightShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.0625, 0.9375, 0.1875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.75, 0.9375, 0.1875, 0.9375), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeLeftShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.0625, 1, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.75, 0.25, 0.1875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.25, 0.1875, 0.25), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeMiddleShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.0625, 1, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        return shape;
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        TYPE = GeneralUtil.LINE_CONNECTING_TYPE;
        OPEN = BlockStateProperties.OPEN;

        SHAPES_SUPPLIERS.put(GeneralUtil.LineConnectingType.NONE, DresserBlock::makeSingleShape);
        SHAPES_SUPPLIERS.put(GeneralUtil.LineConnectingType.MIDDLE, DresserBlock::makeMiddleShape);
        SHAPES_SUPPLIERS.put(GeneralUtil.LineConnectingType.RIGHT, DresserBlock::makeRightShape);
        SHAPES_SUPPLIERS.put(GeneralUtil.LineConnectingType.LEFT, DresserBlock::makeLeftShape);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPES.put(direction, new HashMap<>());
            for (Map.Entry<GeneralUtil.LineConnectingType, Supplier<VoxelShape>> entry : SHAPES_SUPPLIERS.entrySet()) {
                SHAPES.get(direction).put(entry.getKey(), GeneralUtil.rotateShape(Direction.NORTH, direction, entry.getValue().get()));
            }
        }
    }
}