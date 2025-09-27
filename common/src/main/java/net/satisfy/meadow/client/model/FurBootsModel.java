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

public class FurBootsModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Meadow.identifier("fur_boots"), "main");
    private final ModelPart right_leg;
    private final ModelPart left_leg;

    public FurBootsModel(ModelPart root) {
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 48)
                        .addBox(-2.0F, 6.75F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.325F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 48)
                        .mirror()
                        .addBox(-2.0F, 6.75F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.325F))
                        .mirror(false),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int k) {
        poseStack.pushPose();
        poseStack.scale(1.075F, 1.075F, 1.075F);
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, k);
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, k);
        poseStack.popPose();
    }

    @Override
    public void setupAnim(@NotNull T entity, float f, float g, float h, float i, float j) {
    }

    public void copyLegs(ModelPart rightLegModel, ModelPart leftLegModel) {
        this.right_leg.copyFrom(rightLegModel);
        this.left_leg.copyFrom(leftLegModel);
    }
}
