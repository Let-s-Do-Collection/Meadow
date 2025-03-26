package net.satisfy.meadow.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.meadow.Meadow;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterialRegistry {
    public static DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Meadow.MOD_ID, Registries.ARMOR_MATERIAL);

    public static final Holder<ArmorMaterial> FUR = registerMaterial("fur", Map.of(ArmorItem.Type.HELMET, 2, ArmorItem.Type.CHESTPLATE, 5, ArmorItem.Type.LEGGINGS, 4, ArmorItem.Type.BOOTS, 1), 12, SoundEventRegistry.FUR, () -> Ingredient.of(Items.RABBIT_HIDE), 0.0F, 0.1F, false);

    public static Holder<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints, int enchantability, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier, float toughness, float knockbackResistance, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Meadow.MOD_ID, id), "", dyeable)
        );


        final ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);
        return ARMOR_MATERIALS.register(id, () -> material);
    }

    public static void init() {
        ARMOR_MATERIALS.register();
    }

}