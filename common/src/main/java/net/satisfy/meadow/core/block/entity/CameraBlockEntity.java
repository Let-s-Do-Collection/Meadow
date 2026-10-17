package net.satisfy.meadow.core.block.entity;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.util.LibUtil;
import net.satisfy.meadow.core.block.CameraBlock;
import net.satisfy.meadow.core.network.packet.PhotoRequestPacket;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class CameraBlockEntity extends BlockEntity {
    public static final int PHOTO_SIZE = 128;
    private static final int PHOTO_TIMEOUT = 200;
    private static final double PHOTOGRAPHER_RANGE = 32.0;
    private static final DustParticleOptions TIMER_LIGHT = new DustParticleOptions(new Vector3f(1.0F, 0.1F, 0.1F), 0.6F);

    private int paper;
    private int timer;
    @Nullable
    private UUID timerOwner;
    @Nullable
    private UUID photographer;
    private long requestedAt;

    public CameraBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CAMERA_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CameraBlockEntity camera) {
        if (camera.timer > 0) {
            camera.timer--;
            if (level.isClientSide()) {
                if (camera.timer % 10 == 0) {
                    Vec3 lens = CameraBlock.lens(pos, state.getValue(CameraBlock.FACING));
                    level.addParticle(TIMER_LIGHT, lens.x, lens.y + 0.2, lens.z, 0.0, 0.0, 0.0);
                }
            } else if (level instanceof ServerLevel serverLevel) {
                boolean lastSecond = camera.timer < 20;
                if (camera.timer % (lastSecond ? 5 : 20) == 0 && camera.timer > 0) {
                    level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.BLOCKS, 0.7F, lastSecond ? 1.6F : 1.2F);
                }
                if (camera.timer == 0) {
                    camera.setChanged();
                    camera.shoot(serverLevel, camera.timerOwner);
                    camera.timerOwner = null;
                }
            }
        }
        if (!level.isClientSide() && camera.photographer != null && level.getGameTime() - camera.requestedAt > PHOTO_TIMEOUT) {
            camera.photographer = null;
            camera.paper = Math.min(PlatformHelper.getCameraMaxPaper(), camera.paper + 1);
            camera.setChanged();
        }
    }

    public void toggleTimer(Player player) {
        if (level == null) {
            return;
        }
        if (timer > 0) {
            timer = 0;
            timerOwner = null;
            level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 0.7F, 0.8F);
        } else if (!getBlockState().getValue(CameraBlock.POWERED)) {
            timer = PlatformHelper.getCameraTimerDuration();
            timerOwner = player.getUUID();
            level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.BLOCKS, 0.7F, 1.2F);
        }
        setChanged();
    }

    public void shoot(ServerLevel level, @Nullable UUID preferred) {
        BlockState state = getBlockState();
        if (state.getValue(CameraBlock.POWERED)) {
            return;
        }
        Direction facing = state.getValue(CameraBlock.FACING);
        level.playSound(null, worldPosition, SoundEventRegistry.CLICK_CAMERA.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlock(worldPosition, state.setValue(CameraBlock.POWERED, true), Block.UPDATE_ALL);
        level.updateNeighborsAt(worldPosition, state.getBlock());
        level.scheduleTick(worldPosition, state.getBlock(), PlatformHelper.getCameraCooldown());
        level.gameEvent(null, GameEvent.BLOCK_ACTIVATE, worldPosition);
        boolean flash = PlatformHelper.enableCameraFlash();
        level.blockEvent(worldPosition, state.getBlock(), CameraBlock.FLASH_EVENT, flash ? 1 : 0);
        if (flash) {
            dazzle(level, facing);
        }
        if (paper > 0 && photographer == null) {
            ServerPlayer target = photographer(level, preferred);
            if (target != null) {
                paper--;
                photographer = target.getUUID();
                requestedAt = level.getGameTime();
                NetworkManager.sendToPlayer(target, new PhotoRequestPacket(worldPosition));
            }
        }
        setChanged();
    }

    @Nullable
    private ServerPlayer photographer(ServerLevel level, @Nullable UUID preferred) {
        Vec3 center = worldPosition.getCenter();
        if (preferred != null && level.getPlayerByUUID(preferred) instanceof ServerPlayer player && player.distanceToSqr(center) <= PHOTOGRAPHER_RANGE * PHOTOGRAPHER_RANGE) {
            return player;
        }
        Player nearest = level.getNearestPlayer(center.x, center.y, center.z, PHOTOGRAPHER_RANGE, entity -> !entity.isSpectator());
        return nearest instanceof ServerPlayer player ? player : null;
    }

    private void dazzle(ServerLevel level, Direction facing) {
        int range = PlatformHelper.getCameraFlashRange();
        int duration = PlatformHelper.getCameraFlashDuration();
        Vec3 lens = CameraBlock.lens(worldPosition, facing);
        Vec3 forward = new Vec3(facing.getStepX(), 0.0, facing.getStepZ());
        for (Mob mob : level.getEntitiesOfClass(Mob.class, new AABB(lens, lens).inflate(range), mob -> mob instanceof Enemy && mob.isAlive())) {
            Vec3 eye = mob.getEyePosition();
            Vec3 offset = eye.subtract(lens);
            if (offset.lengthSqr() > range * range || offset.normalize().dot(forward) < 0.5) {
                continue;
            }
            if (level.clip(new ClipContext(lens, eye, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() != HitResult.Type.MISS) {
                continue;
            }
            mob.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 0, false, false, true));
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 3, false, false, true));
            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 1, false, false, true));
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, Math.min(duration, 60), 0, false, false, false));
            mob.setTarget(null);
            mob.getNavigation().stop();
            mob.getLookControl().setLookAt(lens);
            if (mob instanceof Creeper creeper) {
                creeper.setSwellDir(-1);
            }
        }
    }

    public void acceptPhoto(ServerPlayer player, byte[] colors) {
        if (!(level instanceof ServerLevel serverLevel) || photographer == null || !photographer.equals(player.getUUID()) || colors.length != PHOTO_SIZE * PHOTO_SIZE) {
            return;
        }
        photographer = null;
        MapId id = serverLevel.getFreeMapId();
        MapItemSavedData data = MapItemSavedData.createFresh(worldPosition.getX(), worldPosition.getZ(), (byte) 0, false, false, serverLevel.dimension()).locked();
        System.arraycopy(colors, 0, data.colors, 0, colors.length);
        data.setDirty();
        serverLevel.setMapData(id, data);

        ItemStack photo = new ItemStack(ObjectRegistry.PHOTO.get());
        photo.set(DataComponents.MAP_ID, id);
        photo.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("tooltip.meadow.photo.taken_by", player.getDisplayName()))));

        Direction facing = getBlockState().getValue(CameraBlock.FACING);
        Vec3 lens = CameraBlock.lens(worldPosition, facing);
        ItemEntity item = new ItemEntity(serverLevel, lens.x, lens.y, lens.z, photo, facing.getStepX() * 0.12, 0.15, facing.getStepZ() * 0.12);
        item.setDefaultPickUpDelay();
        serverLevel.addFreshEntity(item);
        serverLevel.playSound(null, worldPosition, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 0.8F);
        setChanged();
    }

    public void addPaper() {
        paper = Math.min(PlatformHelper.getCameraMaxPaper(), paper + 1);
        setChanged();
    }

    public int getPaper() {
        return paper;
    }

    public int getTimer() {
        return timer;
    }

    public boolean isCounting() {
        return timer > 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        paper = tag.getInt("Paper");
        timer = tag.getInt("Timer");
        timerOwner = tag.hasUUID("TimerOwner") ? tag.getUUID("TimerOwner") : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("Paper", paper);
        tag.putInt("Timer", timer);
        if (timerOwner != null) {
            tag.putUUID("TimerOwner", timerOwner);
        }
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
