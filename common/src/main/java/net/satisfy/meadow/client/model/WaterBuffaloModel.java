package net.satisfy.meadow.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.satisfy.meadow.core.entity.WaterBuffaloEntity;

import java.util.ArrayList;
import java.util.List;

public class WaterBuffaloModel extends QuadrupedModel<WaterBuffaloEntity> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;

    public WaterBuffaloModel(ModelPart root) {
        super(root, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leg0 = root.getChild("left_hind_leg");
        this.leg1 = root.getChild("right_hind_leg");
        this.leg2 = root.getChild("left_front_leg");
        this.leg3 = root.getChild("right_front_leg");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();


        PartDefinition body = modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.0F, -4.0F, -5.0F, 14.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.0F, -16.0F, -5.0F, 14.0F, 12.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(33, 32).addBox(-4.0F, -2.3393F, -7.0F, 8.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.3393F, -14.0F));

        PartDefinition horn_right_top_r1 = head.addOrReplaceChild("horn_right_top_r1", CubeListBuilder.create().texOffs(20, 47).mirror().addBox(0.5F, -3.25F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(38, 25).mirror().addBox(-5.5F, -0.25F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.7271F, -2.5991F, -4.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition horn_left_top_r1 = head.addOrReplaceChild("horn_left_top_r1", CubeListBuilder.create().texOffs(20, 47).addBox(-2.5F, -3.25F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(38, 25).addBox(-2.5F, -0.25F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.7271F, -2.5991F, -4.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition snout_r1 = head.addOrReplaceChild("snout_r1", CubeListBuilder.create().texOffs(41, 0).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.6607F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.4F, -1.5F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 1.1607F, -3.0F, 0.0F, 0.0F, 0.48F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 25).addBox(-0.6F, -1.5F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 1.1607F, -3.0F, 0.0F, 0.0F, -0.48F));

        PartDefinition left_hind_leg = modelPartData.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 10.0F, 7.5F));

        PartDefinition right_hind_leg = modelPartData.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 10.0F, 7.5F));

        PartDefinition left_front_leg = modelPartData.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 10.0F, -10.5F));

        PartDefinition right_front_leg = modelPartData.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 10.0F, -10.5F));

        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        List<ModelPart> partsToScale = getPartsToScale();
        for (ModelPart part : partsToScale) {
            part.render(poseStack, vertexConsumer, i, j, k);
        }
    }

    public List<ModelPart> getPartsToScale() {
        List<ModelPart> parts = new ArrayList<>();
        parts.add(this.head);
        parts.add(this.body);
        parts.add(this.leg0);
        parts.add(this.leg1);
        parts.add(this.leg2);
        parts.add(this.leg3);
        return parts;
    }
}
