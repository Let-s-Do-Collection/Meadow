package net.satisfy.meadow.core.item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.satisfy.meadow.core.registry.ArmorRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FurBootsItem extends ArmorItem {
    private final ResourceLocation bootsTexture;

    public FurBootsItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties, ResourceLocation bootsTexture) {
        super(armorMaterial, type, properties);
        this.bootsTexture = bootsTexture;
    }

    public ResourceLocation getBootsTexture() {
        return bootsTexture;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.FEET;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        ArmorRegistry.appendToolTip(list);
    }
}
