package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CameraBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final Supplier<VoxelShape> SHAPE_SUPPLIER = () -> {
        VoxelShape base = Shapes.box(0.25, 0.9375, 0.1875, 0.75, 1.4375, 0.8125);
        VoxelShape lens = Shapes.box(0.4375, 1.125, -0.125, 0.5625, 1.25, 0.1875);
        return Shapes.or(base, lens);
    };

    private static final Map<Direction, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        VoxelShape lens = SHAPE_SUPPLIER.get();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, lens));
        }
    });

    public CameraBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        world.playSound(null, pos, SoundEventRegistry.CLICK_CAMERA.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        if (world.isClientSide) {
            Direction direction = state.getValue(FACING);
            double xBase = pos.getX() + 0.5D;
            double yBase = pos.getY() + 0.7D;
            double zBase = pos.getZ() + 0.5D;
            double offset = 0.5D * direction.getStepX();
            double noise = (world.random.nextFloat() - 0.5D) * 0.5D;
            for (int i = 0; i < 5; i++) {
                double x = xBase + offset;
                double z = zBase + noise * direction.getStepZ();
                world.addParticle(ParticleTypes.SMOKE, x, yBase, z, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.FIREWORK, x, yBase, z, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.CRIT, x + 5.0D / 16.0D * direction.getStepX(), yBase, z, 0.0D, 0.0D, 0.0D);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
