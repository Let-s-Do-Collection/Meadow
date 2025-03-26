package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.satisfy.meadow.core.item.CompletionistBannerItem;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class CompletionistBannerBlockEntity extends BlockEntity implements BlockEntityTicker<CompletionistBannerBlockEntity> {
    private ItemStack stack;

    public CompletionistBannerBlockEntity(BlockPos blockPos, BlockState state) {
        super(EntityTypeRegistry.MEADOW_BANNER.get(), blockPos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if(stack == null) stack = ItemStack.EMPTY;
        stack.save(provider);
        compoundTag.put("stack", tag);
        super.saveAdditional(compoundTag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        assert this.level != null;
        stack = ItemStack.parse(this.level.registryAccess(), compoundTag.getCompound("Item")).orElse(ItemStack.EMPTY);
    }

    public void fromItem(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof CompletionistBannerItem){
            this.stack = new ItemStack(stack.getItem());
        }
        else throw new RuntimeException("[DoApi] False item for standard! At: " + getBlockPos());
    }

    public Item getItem(){
        return stack == null ? Items.AIR : stack.getItem();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState blockState, CompletionistBannerBlockEntity blockEntity) {
        if (!level.isClientSide() && level.getGameTime() % 80L == 0L) {
            MobEffectInstance instance = CompletionistBannerItem.getEffectInstanceOrNull(getItem());
            if (instance == null) {
                return;
            }
            level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(8F), player -> true).forEach(player -> player.addEffect(instance));
        }
    }
}