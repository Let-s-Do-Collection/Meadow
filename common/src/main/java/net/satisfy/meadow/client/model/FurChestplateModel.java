package net.satisfy.meadow.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.satisfy.meadow.Meadow;
import org.jetbrains.annotations.NotNull;

public class FurChestplateModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Meadow.identifier("fur_chest"), "main");
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart right_arm;

    public FurChestplateModel(ModelPart root) {
        this.body = root.getChild("body");
        this.left_arm = root.getChild("left_arm");
        this.right_arm = root.getChild("right_arm");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 16).mirror()
                        .addBox(-1.0F, -1.2125F, -2.075F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.275F)).mirror(false)
                        .texOffs(40, 35)
                        .addBox(-1.0F, 7.7875F, -2.075F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F))
                        .texOffs(0, 16)
                        .addBox(-1.0F, -3.2125F, -2.075F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.4F))
                        .texOffs(51, 10)
                        .addBox(2.0F, -7.5625F, -1.075F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(7.0F, 5.9125F, 0.575F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(0, 16).mirror()
                        .addBox(-3.05F, -3.1667F, -2.05F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
                        .texOffs(40, 16).mirror()
                        .addBox(-3.05F, -1.1667F, -2.05F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.275F)).mirror(false)
                        .texOffs(40, 35).mirror()
                        .addBox(-3.05F, 7.8333F, -2.05F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false),
                PartPose.offset(-6.2F, 11.6667F, 0.95F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int k) {
        poseStack.pushPose();
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, k);
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, k);
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, k);
        poseStack.popPose();
    }

    @Override
    public void setupAnim(@NotNull T entity, float f, float g, float h, float i, float j) {
    }

    @SuppressWarnings("unused")
    public void copyBody(ModelPart baseBody, ModelPart leftArm, ModelPart rightArm, ModelPart leftLeg, ModelPart rightLeg) {
        this.body.copyFrom(baseBody);
        this.left_arm.copyFrom(leftArm);
        this.right_arm.copyFrom(rightArm);
    }
}
