package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.CompletionistBannerBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CompletionistBannerBlock extends BaseEntityBlock {
    public static final MapCodec<CompletionistBannerBlock> CODEC = simpleCodec(CompletionistBannerBlock::new);
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public CompletionistBannerBlock(Properties properties) {
        super(properties);
        makeDefaultState();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).get() && state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CompletionistBannerBlockEntity entity) {
                Item item = entity.getItem();
                if(item != null){
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(item));
                }
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    protected void makeDefaultState() {
        registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        Optional<CompletionistBannerBlockEntity> entity = levelReader.getBlockEntity(blockPos, DoApiBlockEntityTypes.COMPLETIONISTBANNER.get());
        if(entity.isPresent()){
            Item item = entity.get().getItem();
            if(item != null){
                return new ItemStack(item);
            }
        }
        return super.getCloneItemStack(levelReader, blockPos, blockState);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CompletionistBannerBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        level.getBlockEntity(blockPos, EntityTypeRegistry.MEADOW_BANNER.get()).ifPresent(entity -> entity.fromItem(itemStack));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.below()).isSolid();
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState blockState)
    {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(blockPlaceContext.getRotation() + 180.0F));
    }

    @Override
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(ROTATION, rotation.rotate(blockState.getValue(ROTATION), 16));
    }

    @Override
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(ROTATION, mirror.mirror(blockState.getValue(ROTATION), 16));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EntityTypeRegistry.MEADOW_BANNER.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }
}