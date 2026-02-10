package net.satisfy.meadow.core.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.meadow.client.model.FurBootsModel;
import net.satisfy.meadow.client.model.FurChestplateModel;
import net.satisfy.meadow.client.model.FurHelmetModel;
import net.satisfy.meadow.client.model.FurLeggingsModel;
import net.satisfy.meadow.core.item.FurBootsItem;
import net.satisfy.meadow.core.item.FurChestItem;
import net.satisfy.meadow.core.item.FurHelmetItem;
import net.satisfy.meadow.core.item.FurLegsItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ArmorRegistry {
    private static final Map<Item, FurHelmetModel<LivingEntity>> helmetModels = new HashMap<>();
    private static final Map<Item, FurChestplateModel<LivingEntity>> chestplateModels = new HashMap<>();
    private static final Map<Item, FurLeggingsModel<LivingEntity>> leggingsModels = new HashMap<>();
    private static final Map<Item, FurBootsModel<LivingEntity>> bootsModels = new HashMap<>();

    public static Model getHatModel(Item item, ModelPart baseHead, HumanoidModel<?> original) {
        if (item != ObjectRegistry.FUR_HELMET.get()) return original;

        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        FurHelmetModel<LivingEntity> model = helmetModels.computeIfAbsent(item, key -> new FurHelmetModel<>(modelSet.bakeLayer(FurHelmetModel.LAYER_LOCATION)));

        model.young = original.young;
        model.copyHead(baseHead);

        return model;
    }

    public static Model getChestplateModel(Item item, ModelPart body, ModelPart leftArm, ModelPart rightArm, ModelPart leftLeg, ModelPart rightLeg, HumanoidModel<?> original) {
        if (item != ObjectRegistry.FUR_CHESTPLATE.get()) return original;

        FurChestplateModel<LivingEntity> model = chestplateModels.computeIfAbsent(item, key -> new FurChestplateModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FurChestplateModel.LAYER_LOCATION)));

        model.young = original.young;
        model.copyBody(body, leftArm, rightArm, leftLeg, rightLeg);

        return model;
    }

    public static Model getLeggingsModel(Item item, ModelPart rightLeg, ModelPart leftLeg, HumanoidModel<?> original) {
        if (item != ObjectRegistry.FUR_LEGGINGS.get()) return original;

        FurLeggingsModel<LivingEntity> model = leggingsModels.computeIfAbsent(item, key -> new FurLeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FurLeggingsModel.LAYER_LOCATION)));

        model.young = original.young;
        model.copyLegs(rightLeg, leftLeg);

        return model;
    }

    public static Model getBootsModel(Item item, ModelPart rightLeg, ModelPart leftLeg, HumanoidModel<?> original) {
        if (item != ObjectRegistry.FUR_BOOTS.get()) return original;

        FurBootsModel<LivingEntity> model = bootsModels.computeIfAbsent(item, key -> new FurBootsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FurBootsModel.LAYER_LOCATION)));

        model.young = original.young;
        model.copyLegs(rightLeg, leftLeg);

        return model;
    }

    public static void appendToolTip(@NotNull List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.getItem() instanceof FurHelmetItem &&
                chestplate.getItem() instanceof FurChestItem &&
                leggings.getItem() instanceof FurLegsItem &&
                boots.getItem() instanceof FurBootsItem;

        tooltip.add(Component.nullToEmpty(""));
        tooltip.add(Component.nullToEmpty(ChatFormatting.DARK_GREEN + I18n.get("tooltip.meadow.armor.fur_armor0")));
        tooltip.add(Component.nullToEmpty((helmet.getItem() instanceof FurHelmetItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + ObjectRegistry.FUR_HELMET.get().getDescription().getString() + "]"));
        tooltip.add(Component.nullToEmpty((chestplate.getItem() instanceof FurChestItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + ObjectRegistry.FUR_CHESTPLATE.get().getDescription().getString() + "]"));
        tooltip.add(Component.nullToEmpty((leggings.getItem() instanceof FurLegsItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + ObjectRegistry.FUR_LEGGINGS.get().getDescription().getString() + "]"));
        tooltip.add(Component.nullToEmpty((boots.getItem() instanceof FurBootsItem ? ChatFormatting.GREEN.toString() : ChatFormatting.GRAY.toString()) + "- [" + ObjectRegistry.FUR_BOOTS.get().getDescription().getString() + "]"));
        tooltip.add(Component.nullToEmpty(""));

        ChatFormatting color = hasFullSet ? ChatFormatting.GREEN : ChatFormatting.GRAY;
        tooltip.add(Component.nullToEmpty(color + I18n.get("tooltip.meadow.armor.fur_armor1")));
        tooltip.add(Component.nullToEmpty(color + I18n.get("tooltip.meadow.armor.fur_armor2")));
    }
}