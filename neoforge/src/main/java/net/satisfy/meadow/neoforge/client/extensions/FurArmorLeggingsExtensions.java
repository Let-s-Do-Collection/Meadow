package net.satisfy.meadow.neoforge.client.extensions;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.satisfy.meadow.core.item.FurLegsItem;
import net.satisfy.meadow.core.registry.ArmorRegistry;
import org.jetbrains.annotations.NotNull;

public class FurArmorLeggingsExtensions implements IClientItemExtensions {

    @Override
    public @NotNull Model getGenericArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
        if (slot != EquipmentSlot.LEGS || !(stack.getItem() instanceof FurLegsItem furLegsItem)) return original;

        return ArmorRegistry.getLeggingsModel(furLegsItem, original.rightLeg, original.leftLeg, original);
    }
}