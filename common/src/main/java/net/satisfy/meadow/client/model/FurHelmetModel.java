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

public class FurHelmetModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Meadow.identifier("fur_hat"), "main");
    private final ModelPart top_part;

    public FurHelmetModel(ModelPart root) {
        this.top_part = root.getChild("top_part");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition top_part = partdefinition.addOrReplaceChild("top_part", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)) // Moved down by 6 pixels
                        .texOffs(36, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))  // Moved down by 6 pixels
                        .texOffs(51, 10).addBox(3.0F, -14.0F, 0.0F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))  // Moved down by 6 pixels
                        .texOffs(4, 22).addBox(6.0F, -11.0F, -6.0F, 0.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))  // Moved down by 6 pixels
                        .texOffs(4, 22).addBox(-6.0F, -11.0F, -6.0F, 0.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)) // Moved down by 6 pixels
                        .texOffs(28, 34).addBox(-3.0F, -6.0F, -6.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), // Moved down by 6 pixels
                PartPose.offset(0.0F, 17.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int k) {
        poseStack.pushPose();
        poseStack.scale(1.05F, 1.05F, 1.05F);
        top_part.render(poseStack, vertexConsumer, packedLight, packedOverlay, k);
        poseStack.popPose();
    }

    @Override
    public void setupAnim(@NotNull T entity, float f, float g, float h, float i, float j) {

    }

    public void copyHead(ModelPart model) {
        top_part.copyFrom(model);
    }
}
