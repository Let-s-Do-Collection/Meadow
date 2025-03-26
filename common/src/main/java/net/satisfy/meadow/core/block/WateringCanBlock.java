package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WateringCanBlock extends FacingBlock {
    public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 0, 5);

    public WateringCanBlock(Properties settings) {
        super(settings);
    }

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.4375, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.6875, 0.125, 0.4375, 0.8125, 0.125, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.125, 0.4375, 0.8125, 0.375, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.6875, 0.375, 0.4375, 0.8125, 0.375, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0.5625, 0.4375, 0.625, 0.5625, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.4375, 0.4375, 0.625, 0.5625, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0.4375, 0.4375, 0.375, 0.5625, 0.5625), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(blockState, builder);
        ItemStack stack = new ItemStack(this);
        stack.setDamageValue(blockState.getValue(DAMAGE));
        drops.add(stack);
        return drops;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DAMAGE);
    }
}