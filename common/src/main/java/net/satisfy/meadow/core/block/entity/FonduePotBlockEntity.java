package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.LibUtil;
import net.satisfy.meadow.core.block.FonduePotBlock;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import net.satisfy.meadow.platform.PlatformHelper;

public class FonduePotBlockEntity extends BlockEntity {
    public static final int CAPACITY = 4;
    private static final int STIR_TICKS = 6;
    private static final int SYNC_INTERVAL = 5;
    private static final int STIR_SOUND_INTERVAL = 8;
    public static final int SHAKE_EVENT = 1;
    public static ToIntFunction<FonduePotBlockEntity> colorProvider = pot -> 0xFFF2C14E;

    private final List<ItemStack> portions = new ArrayList<>();
    private ItemStack melting = ItemStack.EMPTY;
    private int meltTicks;
    private int stirTicks;
    private long lastStirTime = Long.MIN_VALUE / 2;
    private long lastStirSound = Long.MIN_VALUE / 2;
    private long lastShakeTime = Long.MIN_VALUE / 2;

    public FonduePotBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FONDUE_POT_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FonduePotBlockEntity pot) {
        if (pot.melting.isEmpty() || pot.stirTicks <= 0 || !FonduePotBlock.isHeated(level, pos)) {
            if (pot.stirTicks > 0) {
                pot.stirTicks--;
            }
            return;
        }
        pot.stirTicks--;
        pot.meltTicks++;
        if (level.isClientSide()) {
            if (pot.meltTicks % 4 == 0) {
                pot.splash(level, 2);
            }
            return;
        }
        if (pot.meltTicks >= pot.meltDuration()) {
            int amount = pot.meltingPortions();
            for (int i = 0; i < amount; i++) {
                pot.portions.add(pot.melting.copyWithCount(1));
            }
            pot.melting = ItemStack.EMPTY;
            pot.meltTicks = 0;
            pot.stirTicks = 0;
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 1.2F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(FoundationParticles.SOUP_STEAM.get(), pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 4, 0.12, 0.02, 0.12, 0.02);
            }
            pot.setChanged();
        } else if (pot.meltTicks % SYNC_INTERVAL == 0) {
            pot.setChanged();
        }
    }

    public static boolean isWheel(ItemStack stack) {
        return stack.is(TagRegistry.CHEESE_WHEELS);
    }

    public static boolean isPiece(ItemStack stack) {
        return stack.is(TagRegistry.CHEESE);
    }

    public boolean canAccept(ItemStack stack) {
        if (!melting.isEmpty()) {
            return false;
        }
        if (isWheel(stack)) {
            return portions.isEmpty();
        }
        return isPiece(stack) && portions.size() < CAPACITY;
    }

    public void insert(ItemStack stack) {
        melting = stack.copyWithCount(1);
        meltTicks = 0;
        stirTicks = 0;
        if (level instanceof ServerLevel serverLevel) {
            level.playSound(null, worldPosition, SoundEvents.SLIME_SQUISH_SMALL, SoundSource.BLOCKS, 0.5F, 1.3F + level.random.nextFloat() * 0.2F);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, melting), worldPosition.getX() + 0.5, worldPosition.getY() + surfaceY(), worldPosition.getZ() + 0.5, 4, 0.08, 0.02, 0.08, 0.04);
        }
        setChanged();
    }

    public void stir() {
        boolean started = stirTicks <= 0;
        stirTicks = STIR_TICKS;
        if (level != null) {
            lastStirTime = level.getGameTime();
            if (level.isClientSide()) {
                if (hasContents()) {
                    splash(level, 1);
                }
            } else if (lastStirTime - lastStirSound >= STIR_SOUND_INTERVAL) {
                lastStirSound = lastStirTime;
                level.playSound(null, worldPosition, SoundEventRegistry.FONDUE_STIRRING.get(), SoundSource.BLOCKS, 0.6F, 0.9F + level.random.nextFloat() * 0.2F);
            }
        }
        if (started) {
            setChanged();
        }
    }

    public boolean hasContents() {
        return !portions.isEmpty() || !melting.isEmpty();
    }

    public boolean isBeingStirred() {
        return stirTicks > 0;
    }

    public long getLastShakeTime() {
        return lastShakeTime;
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == SHAKE_EVENT) {
            if (level != null) {
                lastShakeTime = level.getGameTime();
            }
            return true;
        }
        return super.triggerEvent(id, param);
    }

    public ItemStack takePortion() {
        if (portions.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack portion = portions.remove(portions.size() - 1);
        setChanged();
        return portion;
    }

    public void splash(Level level, int amount) {
        int color = 0xFF000000 | colorProvider.applyAsInt(this);
        double y = worldPosition.getY() + surfaceY();
        for (int i = 0; i < amount; i++) {
            double x = worldPosition.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
            double z = worldPosition.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
            level.addParticle(ColorParticleOption.create(FoundationParticles.DYE_SPLASH.get(), color), x, y, z, (level.random.nextDouble() - 0.5) * 0.06, 0.06, (level.random.nextDouble() - 0.5) * 0.06);
            level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_SOUP_BUBBLE.get(), color), x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public double surfaceY() {
        return FonduePotBlock.surfaceHeight(portions.size() + meltProgress(0) * meltingPortions()) / 16.0;
    }

    public List<ItemStack> getPortions() {
        return portions;
    }

    public ItemStack getMelting() {
        return melting;
    }

    public int meltingPortions() {
        if (melting.isEmpty()) {
            return 0;
        }
        return isWheel(melting) ? CAPACITY : 1;
    }

    public int meltDuration() {
        return isWheel(melting) ? PlatformHelper.getFondueWheelMeltDuration() : PlatformHelper.getFonduePieceMeltDuration();
    }

    public float meltProgress(float partialTick) {
        if (melting.isEmpty()) {
            return 0.0F;
        }
        float ticks = meltTicks + (isStirring() ? partialTick : 0.0F);
        return Math.min(1.0F, ticks / meltDuration());
    }

    public boolean isStirring() {
        return stirTicks > 0 && !melting.isEmpty();
    }

    public long getLastStirTime() {
        return lastStirTime;
    }

    public NonNullList<ItemStack> drops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        if (!melting.isEmpty()) {
            drops.add(melting.copy());
        }
        return drops;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        portions.clear();
        ListTag list = tag.getList("Portions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size() && portions.size() < CAPACITY; i++) {
            ItemStack.parse(provider, list.getCompound(i)).ifPresent(portions::add);
        }
        melting = tag.contains("Melting") ? ItemStack.parse(provider, tag.getCompound("Melting")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        meltTicks = tag.getInt("MeltTicks");
        int syncedStir = tag.getInt("StirTicks");
        if (syncedStir > 0 && level != null && level.isClientSide()) {
            lastStirTime = Math.max(lastStirTime, level.getGameTime());
        }
        stirTicks = syncedStir;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ListTag list = new ListTag();
        for (ItemStack portion : portions) {
            list.add(portion.save(provider));
        }
        tag.put("Portions", list);
        if (!melting.isEmpty()) {
            tag.put("Melting", melting.save(provider));
        }
        tag.putInt("MeltTicks", meltTicks);
        tag.putInt("StirTicks", stirTicks);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public void setChanged() {
        if (level instanceof ServerLevel serverLevel) {
            Packet<ClientGamePacketListener> packet = getUpdatePacket();
            for (ServerPlayer player : LibUtil.tracking(serverLevel, getBlockPos())) {
                player.connection.send(packet);
            }
        }
        super.setChanged();
    }
}
