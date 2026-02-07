package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.CookingCauldronBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.ParticleTypeRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CookingCauldronBlock extends BaseEntityBlock {

    private static final VoxelShape POT_BASE = Block.box(3, 0, 3, 13, 6, 13);
    private static final VoxelShape POT_RIM = Block.box(2, 6, 2, 14, 9, 14);
    private static final VoxelShape HANDLE_LEFT = Block.box(1, 7, 5, 2, 9, 11);
    private static final VoxelShape HANDLE_RIGHT = Block.box(14, 7, 5, 15, 9, 11);

    private static final VoxelShape NORMAL_TOP = Block.box(3, 9, 3, 13, 10, 13);
    private static final VoxelShape NORMAL_KNOB = Block.box(7, 10, 7, 9, 11, 9);

    private static final VoxelShape BASE_POT_SHAPE = Shapes.or(
            POT_BASE,
            POT_RIM,
            HANDLE_LEFT,
            HANDLE_RIGHT
    );

    private static final VoxelShape NORMAL_POT_SHAPE = Shapes.or(
            BASE_POT_SHAPE,
            NORMAL_TOP,
            NORMAL_KNOB
    );

    private static final VoxelShape HANGING_SHAPE = Shapes.box(0.1875, 0, 0.1875, 0.8125, 1.25, 0.8125);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    public static final EnumProperty<CookpotStage> STAGE = EnumProperty.create("stage", CookpotStage.class);

    public static final MapCodec<CookingCauldronBlock> CODEC = simpleCodec(CookingCauldronBlock::new);

    public CookingCauldronBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HANGING, false)
                .setValue(STAGE, CookpotStage.NORMAL)
                .setValue(COOKING, false)
                .setValue(LIT, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HANGING, STAGE, COOKING, LIT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(HANGING)) {
            return HANGING_SHAPE;
        }

        VoxelShape baseShape = state.getValue(STAGE) == CookpotStage.NORMAL
                ? NORMAL_POT_SHAPE
                : BASE_POT_SHAPE;

        return GeneralUtil.rotateShape(Direction.NORTH, state.getValue(FACING), baseShape);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(HANGING, false);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MenuProvider provider) {
            player.openMenu(provider);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moved) {
        super.neighborChanged(state, level, pos, block, fromPos, moved);
        if (!level.isClientSide()) updateHeatState(level, pos);
    }

    public static void updateHeatState(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CookingCauldronBlockEntity cookingCauldron)) return;

        BlockState currentState = level.getBlockState(pos);

        boolean heated = cookingCauldron.isBeingBurned();
        boolean boiling = cookingCauldron.isBoiling();
        boolean finished = cookingCauldron.isFinished();

        CookpotStage stage;
        if (!heated) {
            stage = CookpotStage.NORMAL;
        } else if (boiling) {
            stage = CookpotStage.COOKING;
        } else if (finished) {
            stage = CookpotStage.FILLED;
        } else {
            stage = CookpotStage.WARM;
        }

        boolean cooking = stage == CookpotStage.COOKING;

        BlockState updatedState = currentState
                .setValue(STAGE, stage)
                .setValue(LIT, heated)
                .setValue(COOKING, cooking);

        if (!updatedState.equals(currentState)) {
            level.setBlock(pos, updatedState, 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        CookpotStage stage = state.getValue(STAGE);
        if (stage == CookpotStage.NORMAL) return;

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + (state.getValue(HANGING) ? 1.0 : 0.7);
        double centerZ = pos.getZ() + 0.5;

        if (stage == CookpotStage.WARM) {
            if (random.nextInt(100) < 18) {
                level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.4, centerZ, 0.0, 0.05, 0.0);
            }
            return;
        }

        if (stage == CookpotStage.COOKING) {
            if (random.nextInt(100) < 35) {
                for (int i = 0; i < 3; i++) {
                    double offsetX = (random.nextDouble() - 0.5) * 0.4;
                    double offsetZ = (random.nextDouble() - 0.5) * 0.4;
                    level.addParticle(ParticleTypeRegistry.SOUP_BUBBLE.get(), centerX + offsetX, centerY + 0.15, centerZ + offsetZ, 0.0, 0.03, 0.0);
                }
            }

            if (random.nextInt(100) < 60) {
                double offsetX = (random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5) * 0.3;
                level.addParticle(ParticleTypeRegistry.SOUP_STEAM.get(), centerX + offsetX, centerY + 0.6, centerZ + offsetZ, 0.0, 0.07, 0.0);
            }

            if (random.nextInt(100) < 6) {
                level.playLocalSound(centerX, centerY, centerZ, SoundEventRegistry.COOKING_POT_BOILING.get(), SoundSource.BLOCKS, 0.75F, 0.75F, false);
            }
            return;
        }

        if (stage == CookpotStage.FILLED) {
            if (random.nextInt(100) < 38) {
                double offsetX = (random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5) * 0.3;
                level.addParticle(ParticleTypeRegistry.SOUP_STEAM.get(), centerX + offsetX, centerY + 0.6, centerZ + offsetZ, 0.0, 0.07, 0.0);
            }
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EntityTypeRegistry.COOKING_CAULDRON.get(), (lvl, pos, st, be) -> {
            be.tick(lvl);
            if (!lvl.isClientSide()) updateHeatState(lvl, pos);
        });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookingCauldronBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CookingCauldronBlockEntity cookingCauldron) {
                cookingCauldron.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        if (state.getValue(HANGING)) drops.add(new ItemStack(ObjectRegistry.FRAME.get()));
        return drops;
    }

    public enum CookpotStage implements StringRepresentable {
        NORMAL("normal"),
        WARM("warm"),
        COOKING("cooking"),
        FILLED("filled");

        private final String name;

        CookpotStage(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}