package net.satisfy.meadow.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.satisfy.meadow.Meadow;
import org.jetbrains.annotations.NotNull;

public class FurChestplateModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Meadow.identifier("fur_chest"), "main");

    public FurChestplateModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -1.2125F, -2.075F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.275F)).mirror(false).texOffs(40, 35).addBox(-1.0F, 7.7875F, -2.075F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)).texOffs(0, 16).addBox(-1.0F, -3.2125F, -2.075F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.4F)).texOffs(51, 10).addBox(2.0F, -7.5625F, -1.075F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 5.9125F, 0.575F));

        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-3.05F, -3.1667F, -2.05F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false).texOffs(40, 16).mirror().addBox(-3.05F, -1.1667F, -2.05F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.275F)).mirror(false).texOffs(40, 35).mirror().addBox(-3.05F, 7.8333F, -2.05F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(-6.2F, 11.6667F, 0.95F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int packedColor) {
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void copyBody(ModelPart baseBody, ModelPart baseLeftArm, ModelPart baseRightArm, ModelPart baseLeftLeg, ModelPart baseRightLeg) {
        this.body.copyFrom(baseBody);
        this.leftArm.copyFrom(baseLeftArm);
        this.rightArm.copyFrom(baseRightArm);
        this.leftLeg.copyFrom(baseLeftLeg);
        this.rightLeg.copyFrom(baseRightLeg);
    }
}