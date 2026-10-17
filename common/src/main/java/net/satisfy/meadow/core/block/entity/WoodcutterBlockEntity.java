package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class WoodcutterBlockEntity extends BlockEntity {
    public static final int CHOP_TICKS = 8;

    private final SimpleContainer inventory = new SimpleContainer(1);
    private long cutStart = Long.MIN_VALUE / 2;
    private ItemStack chopped = ItemStack.EMPTY;

    public WoodcutterBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.WOODCUTTER_BLOCK_ENTITY.get(), pos, state);
        this.inventory.addListener(container -> this.markUpdated());
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public ItemStack getInput() {
        return inventory.getItem(0);
    }

    public long getCutStart() {
        return cutStart;
    }

    public void chop(ItemStack stack) {
        if (level == null) {
            return;
        }
        cutStart = level.getGameTime();
        chopped = stack.copyWithCount(1);
        markUpdated();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WoodcutterBlockEntity cutter) {
        if (cutter.chopped.isEmpty() || level.getGameTime() - cutter.cutStart != CHOP_TICKS / 2) {
            return;
        }
        RandomSource random = level.getRandom();
        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, cutter.chopped);
        for (int i = 0; i < 10; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            level.addParticle(particle, x, pos.getY() + 1.05, z, (random.nextDouble() - 0.5) * 0.15, 0.1 + random.nextDouble() * 0.1, (random.nextDouble() - 0.5) * 0.15);
        }
        BlockParticleOption dust = new BlockParticleOption(ParticleTypes.BLOCK, state);
        for (int i = 0; i < 4; i++) {
            level.addParticle(dust, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, (random.nextDouble() - 0.5) * 0.2, 0.05, (random.nextDouble() - 0.5) * 0.2);
        }
    }

    private void markUpdated() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        ItemStack input = tag.contains("Input") ? ItemStack.parse(provider, tag.getCompound("Input")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        inventory.getItems().set(0, input);
        chopped = tag.contains("Chopped") ? ItemStack.parse(provider, tag.getCompound("Chopped")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        cutStart = tag.contains("CutStart") ? tag.getLong("CutStart") : Long.MIN_VALUE / 2;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!getInput().isEmpty()) {
            tag.put("Input", getInput().save(provider));
        }
        if (!chopped.isEmpty()) {
            tag.put("Chopped", chopped.save(provider));
        }
        tag.putLong("CutStart", cutStart);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
