package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhotoFrameBlockEntity extends BlockEntity {
    private ItemStack photo = ItemStack.EMPTY;
    @Nullable
    private byte[] colors;

    public PhotoFrameBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.PHOTO_FRAME_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack getPhoto() {
        return photo;
    }

    public void setPhoto(ItemStack stack) {
        photo = stack.copyWithCount(1);
        setChanged();
    }

    @Nullable
    public byte[] getColors() {
        return colors;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        photo = tag.contains("Photo") ? ItemStack.parse(provider, tag.getCompound("Photo")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        colors = tag.contains("Colors") ? tag.getByteArray("Colors") : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!photo.isEmpty()) {
            tag.put("Photo", photo.save(provider));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = saveWithoutMetadata(provider);
        if (level instanceof ServerLevel serverLevel && !photo.isEmpty()) {
            MapItemSavedData data = MapItem.getSavedData(photo, serverLevel);
            if (data != null) {
                tag.putByteArray("Colors", data.colors);
            }
        }
        return tag;
    }
}
