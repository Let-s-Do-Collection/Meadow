package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.satisfy.meadow.core.block.WardrobeBlock;
import net.satisfy.meadow.core.block.entity.WardrobeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class WardrobeRenderer implements BlockEntityRenderer<WardrobeBlockEntity>, RenderLayerParent<ArmorStand, HumanoidModel<ArmorStand>> {
    private final HumanoidModel<ArmorStand> baseModel;
    private final HumanoidArmorLayer<ArmorStand, HumanoidModel<ArmorStand>, HumanoidModel<ArmorStand>> armorLayer;
    private ArmorStand dummy;

    public static float CHEST_TX = -0.2f;
    public static float CHEST_TY = 1.2f;
    public static float CHEST_TZ = 0.0f;
    public static float CHEST_YAW = 90.0f;
    public static float CHEST_PITCH = 0.0f;
    public static float CHEST_ROLL = 0.0f;
    public static float CHEST_SCALE = 1.7f;

    public static float LEGS_TX = 0.2f;
    public static float LEGS_TY = 1.6f;
    public static float LEGS_TZ = 0.0f;
    public static float LEGS_YAW = 90.0f;
    public static float LEGS_PITCH = 0.0f;
    public static float LEGS_ROLL = 0.0f;
    public static float LEGS_SCALE = 1.7f;

    public static float FEET_TX = 0.2f;
    public static float FEET_TY = 1.4f;
    public static float FEET_TZ = 0.0f;
    public static float FEET_YAW = 45.0f;
    public static float FEET_PITCH = 0.0f;
    public static float FEET_ROLL = 0.0f;
    public static float FEET_SCALE = 1.7f;
    
    public WardrobeRenderer(BlockEntityRendererProvider.Context context) {
        this.baseModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        this.armorLayer = new HumanoidArmorLayer<>(
                this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                Minecraft.getInstance().getModelManager()
        );
    }

    @Override
    public void render(WardrobeBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getLevel() == null) return;
        if (be.getBlockState().getValue(WardrobeBlock.HALF) == DoubleBlockHalf.UPPER) return;

        if (dummy == null || dummy.level() != be.getLevel()) {
            dummy = new ArmorStand(EntityType.ARMOR_STAND, be.getLevel());
            dummy.setNoBasePlate(true);
            dummy.setInvisible(true);
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 1.6, 0.5);
        switch (be.getBlockState().getValue(WardrobeBlock.FACING)) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
        }

        renderArmorPiece(poseStack, buffer, packedLight,
                be.getItem(WardrobeBlockEntity.SLOT_CHEST), EquipmentSlot.CHEST,
                CHEST_TX, CHEST_TY, CHEST_TZ, CHEST_YAW, CHEST_PITCH, CHEST_ROLL, CHEST_SCALE,
                partialTicks);

        renderArmorPiece(poseStack, buffer, packedLight,
                be.getItem(WardrobeBlockEntity.SLOT_LEGS), EquipmentSlot.LEGS,
                LEGS_TX, LEGS_TY, LEGS_TZ, LEGS_YAW, LEGS_PITCH, LEGS_ROLL, LEGS_SCALE,
                partialTicks);

        renderArmorPiece(poseStack, buffer, packedLight,
                be.getItem(WardrobeBlockEntity.SLOT_FEET), EquipmentSlot.FEET,
                FEET_TX, FEET_TY, FEET_TZ, FEET_YAW, FEET_PITCH, FEET_ROLL, FEET_SCALE,
                partialTicks);

        poseStack.popPose();
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack stack, EquipmentSlot slot, float tx, float ty, float tz, float yaw, float pitch, float roll, float scale, float partialTicks) {
        if (stack.isEmpty()) return;

        ArmorStand partDummy = new ArmorStand(EntityType.ARMOR_STAND, dummy.level());
        partDummy.setNoBasePlate(true);
        partDummy.setInvisible(true);
        partDummy.setItemSlot(slot, stack);

        poseStack.pushPose();
        poseStack.translate(tx, ty, tz);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
        poseStack.scale(scale, -scale, -scale);

        if (slot == EquipmentSlot.LEGS) {
            baseModel.body.visible = false;
            baseModel.rightLeg.visible = true;
            baseModel.leftLeg.visible = true;
        } else if (slot == EquipmentSlot.CHEST) {
            baseModel.body.visible = true;
            baseModel.rightArm.visible = true;
            baseModel.leftArm.visible = true;
        } else if (slot == EquipmentSlot.FEET) {
            baseModel.rightLeg.visible = true;
            baseModel.leftLeg.visible = true;
        } else {
            baseModel.setAllVisible(false);
        }

        armorLayer.render(poseStack, buffer, packedLight, partDummy, 0, 0, partialTicks, 0, 0, 0);

        poseStack.popPose();
    }

    @Override
    public @NotNull HumanoidModel<ArmorStand> getModel() {
        return baseModel;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ArmorStand entity) {
        return ResourceLocation.withDefaultNamespace("textures/entity/armorstand/wood.png");
    }
}
