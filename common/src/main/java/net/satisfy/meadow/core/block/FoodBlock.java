package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class FoodBlock extends FacingBlock {

    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);

    private static final Map<Direction, VoxelShape[]> SHAPES = buildShapes();
    private final MobEffectInstance effect;
    private final int nutrition;
    private final float saturationMod;

    public FoodBlock(Properties settings, MobEffectInstance effect, int nutrition, float saturationMod) {
        super(settings);
        this.effect = effect;
        this.nutrition = nutrition;
        this.saturationMod = saturationMod;
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BITES);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        int bites = state.getValue(BITES);
        VoxelShape[] byBites = SHAPES.get(facing);
        if (byBites == null) {
            byBites = SHAPES.get(Direction.NORTH);
        }
        return byBites[Math.max(0, Math.min(3, bites))];
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = tryEat(level, pos, state, player);
        if (result.consumesAction()) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            InteractionResult result = tryEat(level, pos, state, player);
            if (result.consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return tryEat(level, pos, state, player);
    }

    private InteractionResult tryEat(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
        if (world instanceof Level level) {
            spawnEatParticles(level, pos, state, 10);
        }

        world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1, 1);
        player.getFoodData().eat(nutrition, saturationMod);
        if (effect != null) {
            player.addEffect(new MobEffectInstance(effect));
        }

        int bites = state.getValue(BITES);
        world.gameEvent(player, GameEvent.EAT, pos);

        if (bites < 3) {
            world.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
        } else {
            if (world instanceof Level level) {
                spawnEatParticles(level, pos, state, 30);
            }
            world.removeBlock(pos, false);
            world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static void spawnEatParticles(Level level, BlockPos pos, BlockState state, int count) {
        for (int i = 0; i < count; i++) {
            double velocityX = level.random.nextGaussian() * 0.02D;
            double velocityY = level.random.nextGaussian() * 0.02D;
            double velocityZ = level.random.nextGaussian() * 0.02D;
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, velocityX, velocityY, velocityZ);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos belowPos = pos.below();
        VoxelShape belowShape = world.getBlockState(belowPos).getShape(world, belowPos);
        return Block.isFaceFull(belowShape, Direction.UP);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    private static Map<Direction, VoxelShape[]> buildShapes() {
        VoxelShape north0 = makeShapeBites0();
        VoxelShape north1 = makeShapeBites1();
        VoxelShape north2 = makeShapeBites2();
        VoxelShape north3 = makeShapeBites3();

        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);
        shapes.put(Direction.NORTH, new VoxelShape[]{north0, north1, north2, north3});
        shapes.put(Direction.EAST, new VoxelShape[]{rotateHorizontal(north0, Direction.EAST), rotateHorizontal(north1, Direction.EAST), rotateHorizontal(north2, Direction.EAST), rotateHorizontal(north3, Direction.EAST)});
        shapes.put(Direction.SOUTH, new VoxelShape[]{rotateHorizontal(north0, Direction.SOUTH), rotateHorizontal(north1, Direction.SOUTH), rotateHorizontal(north2, Direction.SOUTH), rotateHorizontal(north3, Direction.SOUTH)});
        shapes.put(Direction.WEST, new VoxelShape[]{rotateHorizontal(north0, Direction.WEST), rotateHorizontal(north1, Direction.WEST), rotateHorizontal(north2, Direction.WEST), rotateHorizontal(north3, Direction.WEST)});
        return shapes;
    }

    private static VoxelShape makeShapeBites0() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.0625, 0.25, 0.5, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.0625, 0.25, 0.875, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.0625, 0.25, 0.6875, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.4375, 0.25, 0.4375, 0.5625), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeShapeBites1() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.0625, 0.25, 0.5, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.0625, 0.25, 0.6875, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.4375, 0.25, 0.4375, 0.5625), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeShapeBites2() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.0625, 0.25, 0.5, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.4375, 0.25, 0.4375, 0.5625), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeShapeBites3() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape rotateHorizontal(VoxelShape original, Direction targetFacing) {
        int turns = switch (targetFacing) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };

        VoxelShape rotated = original;
        for (int i = 0; i < turns; i++) {
            rotated = rotate90Y(rotated);
        }
        return rotated;
    }

    private static VoxelShape rotate90Y(VoxelShape original) {
        VoxelShape[] buffer = new VoxelShape[]{Shapes.empty()};
        original.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            VoxelShape rotatedBox = Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX);
            buffer[0] = Shapes.joinUnoptimized(buffer[0], rotatedBox, BooleanOp.OR);
        });
        return buffer[0].optimize();
    }
}