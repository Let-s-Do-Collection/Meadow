package net.satisfy.meadow.mixin.variant.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.entity.var.SheepVar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepFurLayer.class)
public class SheepFurLayerMixin {

    // Does not work because ModifyArgs is bugged on forge

    /*
    @ModifyArgs(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Sheep;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/SheepFurLayer;coloredCutoutModelCopyLayerRender(Lnet/minecraft/client/model/EntityModel;Lnet/minecraft/client/model/EntityModel;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFFFFF)V"))
    private void onGetTexture(Args args) {
        LivingEntity entity = args.get(6);

        if(entity instanceof Sheep sheep){
            SheepVar var = SheepVar.getVariant(sheep);
            if(var.equals(SheepVar.DEFAULT) || var.equals(SheepVar.HORNED)) return;
            args.set(2, new MeadowIdentifier(String.format("textures/entity/sheep/%s_sheep_fur.png", var.getSerializedName())));
        }
    }
     */

    // Accesswidening SHEEP_FUR_LOCATION, so it can be changed before the render
    @Unique
    private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Sheep;FFFFFF)V",
            at = @At(value = "HEAD"))
    public void onRender(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Sheep sheep, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        SheepVar var = SheepVar.getVariant(sheep);
        if(var.equals(SheepVar.DEFAULT) || var.equals(SheepVar.HORNED)){
            SheepFurLayer.SHEEP_FUR_LOCATION = SHEEP_FUR_LOCATION;
            return;
        }
        SheepFurLayer.SHEEP_FUR_LOCATION = Meadow.identifier(String.format("textures/entity/sheep/%s_sheep_fur.png", var.getSerializedName()));
    }

}
