package net.satisfy.meadow.core.block;

import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.util.ShapeUtil;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class DoormatBlock extends LineConnectingBlock {
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
}
