package net.satisfy.meadow.fabric.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.satisfy.meadow.core.item.FurHelmetItem;
import net.satisfy.meadow.core.registry.ArmorRegistry;

public class FurHelmetRenderer implements ArmorRenderer {
    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
        if (slot != EquipmentSlot.HEAD) return;
        if (!(stack.getItem() instanceof FurHelmetItem furHelmetItem)) return;

        Model model = ArmorRegistry.getHatModel(furHelmetItem, contextModel.head, contextModel);

        ResourceLocation baseTexture = furHelmetItem.getHatTexture();
        String texturePath = baseTexture.getPath();
        if (!texturePath.startsWith("textures/")) texturePath = "textures/" + texturePath;
        if (!texturePath.endsWith(".png")) texturePath = texturePath + ".png";
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(baseTexture.getNamespace(), texturePath);

        model.renderToBuffer(matrices, vertexConsumers.getBuffer(model.renderType(texture)), light, OverlayTexture.NO_OVERLAY, -1);
    }
}