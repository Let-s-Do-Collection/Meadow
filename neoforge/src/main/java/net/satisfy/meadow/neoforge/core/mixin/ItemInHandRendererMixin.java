package net.satisfy.meadow.neoforge.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.satisfy.meadow.core.item.FeltingNeedleItem;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void injectFeltingUseAnim(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress, PoseStack matrices, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        if (!player.isUsingItem()) return;
        if (!(player.getUseItem().getItem() instanceof FeltingNeedleItem)) return;

        ItemInHandRenderer self = (ItemInHandRenderer) (Object) this;

        ItemStack used = player.getUseItem();
        int duration = used.getUseDuration(player);
        float remaining = player.getUseItemRemainingTicks();
        float progress = (duration - remaining + partialTicks) / (float) duration;

        float mainAngle = progress * (float) Math.PI * 8f;
        float wiggleMainHand = Mth.sin(mainAngle) * 0.05f;
        float bounceMainHand = Mth.sin(mainAngle * 0.5f) * 0.025f;

        float offAngle = progress * (float) Math.PI * 2f;
        float wiggleOffHand = Mth.sin(offAngle) * 0.025f;
        float bounceOffHand = Mth.cos(offAngle) * 0.025f;

        if (hand == InteractionHand.MAIN_HAND) {
            matrices.pushPose();
            matrices.translate(0.05f + wiggleMainHand, -0.05f + bounceMainHand, -0.5f);
            matrices.mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(180), 1.0f, 0.0f, 0.0f)));
            self.renderItem(player, stack, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, false, matrices, bufferSource, light);
            matrices.popPose();
        }

        if (hand == InteractionHand.OFF_HAND) {
            matrices.pushPose();
            matrices.translate(0.02f - wiggleOffHand, -0.4f - bounceOffHand, -0.45f);
            matrices.scale(0.85f, 0.85f, 0.85f);
            self.renderItem(player, stack, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, true, matrices, bufferSource, light);
            matrices.popPose();
        }

        Minecraft mc = Minecraft.getInstance();
        if (hand == InteractionHand.MAIN_HAND && mc.level != null && !mc.gameRenderer.getMainCamera().isDetached()) {
            ItemStack wool = player.getOffhandItem();
            if (!wool.isEmpty() && remaining % 5 == 0) {

                Vec3 look = player.getLookAngle();
                Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
                Vec3 up = right.cross(look).normalize();

                double baseX = player.getX() + look.x * 0.4 + right.x * 0.15;
                double baseY = player.getY() + 1.3 + up.y * 0.1;
                double baseZ = player.getZ() + look.z * 0.4 + right.z * 0.15;

                double xOffset = (mc.level.random.nextDouble() - 0.5) * 0.1;
                double yOffset = (mc.level.random.nextDouble() - 0.5) * 0.05;
                double zOffset = (mc.level.random.nextDouble() - 0.5) * 0.1;

                double xVelocity = (mc.level.random.nextDouble() - 0.5) * 0.02;
                double yVelocity = mc.level.random.nextDouble() * 0.02;
                double zVelocity = (mc.level.random.nextDouble() - 0.5) * 0.02;

                mc.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, wool), baseX + xOffset, baseY + yOffset, baseZ + zOffset, xVelocity, yVelocity, zVelocity);
            }

            if (remaining % 20 == 0) {
                player.playSound(SoundEvents.WOOL_HIT, 0.4f, 1.1f);
            }
        }
        ci.cancel();
    }
}