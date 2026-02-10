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

public class FurLeggingsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Meadow.identifier("fur_leggings"), "main");

    public FurLeggingsModel(ModelPart root) {
        super(root);
        setAllVisible(false);
        body.visible = true;
        rightLeg.visible = true;
        leftLeg.visible = true;
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 35).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.325F)), PartPose.ZERO);

        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(29, 39).addBox(-0.8F, -3.0F, -3.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(16, 48).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int packedColor) {
        setAllVisible(false);
        body.visible = true;
        rightLeg.visible = true;
        leftLeg.visible = true;
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
    }

    public void copyBody(ModelPart bodyModel) {
        body.copyFrom(bodyModel);
    }

    public void copyLegs(ModelPart rightLegModel, ModelPart leftLegModel) {
        rightLeg.copyFrom(rightLegModel);
        leftLeg.copyFrom(leftLegModel);
    }
}