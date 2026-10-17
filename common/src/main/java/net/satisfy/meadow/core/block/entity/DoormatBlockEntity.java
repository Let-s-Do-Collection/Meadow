package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;

import java.util.List;

public class DoormatBlockEntity extends BlockEntity {
    public static final int LINES = 2;
    public static final int MAX_LENGTH = 12;

    private final String[] lines = {"", ""};
    private DyeColor color = DyeColor.BLACK;
    private boolean glowing = false;

    public DoormatBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.DOORMAT_BLOCK_ENTITY.get(), pos, state);
    }

    public String getLine(int line) {
        return lines[line];
    }

    public boolean isEmpty() {
        for (String line : lines) {
            if (!line.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void setLines(List<String> text) {
        for (int i = 0; i < LINES; i++) {
            String line = i < text.size() ? text.get(i) : "";
            lines[i] = line.length() > MAX_LENGTH ? line.substring(0, MAX_LENGTH) : line;
        }
        sync();
    }

    public DyeColor getColor() {
        return color;
    }

    public boolean setColor(DyeColor color) {
        if (this.color == color) {
            return false;
        }
        this.color = color;
        sync();
        return true;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public boolean setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
            return false;
        }
        this.glowing = glowing;
        sync();
        return true;
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        for (int i = 0; i < LINES; i++) {
            lines[i] = tag.getString("Text" + i);
        }
        color = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);
        glowing = tag.getBoolean("Glowing");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        for (int i = 0; i < LINES; i++) {
            tag.putString("Text" + i, lines[i]);
        }
        tag.putString("Color", color.getName());
        tag.putBoolean("Glowing", glowing);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
