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

public class FurArmorHat<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Meadow.identifier("fur_helmet"), "main");
    private final ModelPart head;

    public FurArmorHat(ModelPart root) {
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition head = modelPartData.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 50.0F, 0.0F));

        head.addOrReplaceChild("hat", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.0F, -38.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(36, 0).addBox(-3.0F, -40.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(49, 7).addBox(3.0F, -45.0F, 0.0F, 0.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(4, 22).addBox(6.0F, -41.0F, -6.0F, 0.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(4, 22).addBox(-6.0F, -41.0F, -6.0F, 0.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(28, 34).addBox(-3.0F, -36.0F, -6.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 29.0F, 0.0F));

        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.scale(1F, 1F, 1F);
        head.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void setupAnim(T entity, float f, float g, float h, float i, float j) {

    }

    public void copyHead(ModelPart model) {
        head.copyFrom(model);
    }
}
