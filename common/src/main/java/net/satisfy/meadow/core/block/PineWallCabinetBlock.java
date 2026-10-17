package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.CabinetBlockEntity;
import net.satisfy.foundation.block.CabinetWallBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class PineWallCabinetBlock extends CabinetWallBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        VoxelShape north = Shapes.box(0, 0, 0.5, 1, 1, 1);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPES.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, north));
        }
    }

    public PineWallCabinetBlock(Properties properties, Supplier<? extends BlockEntityType<? extends CabinetBlockEntity>> blockEntityType, SoundEvent openSound, SoundEvent closeSound) {
        super(properties, blockEntityType, openSound, closeSound);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }
}
