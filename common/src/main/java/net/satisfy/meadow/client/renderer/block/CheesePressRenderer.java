package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.ExtraModels;
import net.satisfy.meadow.core.block.entity.CheeseFormBlockEntity;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;

import java.util.Map;
import java.util.WeakHashMap;
import net.satisfy.meadow.platform.PlatformHelper;

public class CheesePressRenderer implements BlockEntityRenderer<CheeseFormBlockEntity> {
    private static final float INNER_MIN = 4.0F / 16.0F;
    private static final float INNER_MAX = 12.0F / 16.0F;
    private static final float FLOOR = 1.0F;
    private static final float PLATE_BOTTOM = 9.0F;
    private static final float TURNS = 3.0F;
    private static final float SQUEEZE = 0.3F;
    private static final int MILK_COLOR = 0xFFF8F6EE;
    private static final Map<CheeseFormBlockEntity, Animation> ANIMATIONS = new WeakHashMap<>();

    public CheesePressRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CheeseFormBlockEntity press, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Level level = press.getLevel();
        if (level == null) {
            return;
        }
        Animation animation = ANIMATIONS.computeIfAbsent(press, key -> new Animation());
        float time = level.getGameTime() + partialTick;
        float delta = animation.lastTime < 0 ? 0 : Mth.clamp(time - animation.lastTime, 0, 5);
        animation.lastTime = time;
        boolean animated = PlatformHelper.animationsEnabled();

        float pressProgress = press.pressProgress();
        float targetFill = press.getMilkCount() * (1.0F - SQUEEZE * pressProgress);
        animation.fill = animation.fill < 0 || !animated ? targetFill : Mth.lerp(Math.min(1.0F, delta * 0.2F), animation.fill, targetFill);
        float targetCrank = press.crankProgress(partialTick);
        animation.crank = animation.crank < 0 || !animated ? targetCrank : Mth.lerp(Math.min(1.0F, delta * 0.5F), animation.crank, targetCrank);

        float surface = FLOOR + animation.fill;
        float lowest = Math.max(surface, FLOOR + 1.0F);
        float drop = (PLATE_BOTTOM - lowest) * animation.crank;

        if (animation.fill > 0.01F) {
            renderCurd(poseStack, buffers, light, level, press, surface / 16.0F, pressProgress);
        }
        if (press.getResultCount() > 0) {
            renderCheese(poseStack, buffers, light, overlay, press.getResult());
        }
        renderScrew(poseStack, buffers, light, overlay, press, drop / 16.0F, animation.crank * TURNS * Mth.TWO_PI);
    }

    private static void renderScrew(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, CheeseFormBlockEntity press, float drop, float angle) {
        poseStack.pushPose();
        poseStack.translate(0.0F, -drop, 0.0F);
        renderPart(poseStack, buffers, light, overlay, press, ExtraModels.CHEESE_PRESS_PLATE);
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotation(-angle));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        renderPart(poseStack, buffers, light, overlay, press, ExtraModels.CHEESE_PRESS_SCREW);
        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, CheeseFormBlockEntity press, ResourceLocation model) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(RenderType.cutout()), press.getBlockState(), ExtraModels.get(model), 1.0F, 1.0F, 1.0F, light, overlay);
    }

    private static void renderCurd(PoseStack poseStack, MultiBufferSource buffers, int light, Level level, CheeseFormBlockEntity press, float height, float progress) {
        CheeseFormRecipe recipe = CheeseFormBlockEntity.recipeFor(level, press.getMilk());
        int cheese = recipe == null ? MILK_COLOR : TextureColors.of(recipe.getResultItem(level.registryAccess()));
        int color = FastColor.ARGB32.lerp(progress, MILK_COLOR, cheese) | 0xFF000000;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Meadow.identifier("block/milk"));
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float u0 = sprite.getU(INNER_MIN);
        float u1 = sprite.getU(INNER_MAX);
        float v0 = sprite.getV(INNER_MIN);
        float v1 = sprite.getV(INNER_MAX);
        vertex(consumer, pose, INNER_MIN, height, INNER_MIN, u0, v0, color, light);
        vertex(consumer, pose, INNER_MIN, height, INNER_MAX, u0, v1, color, light);
        vertex(consumer, pose, INNER_MAX, height, INNER_MAX, u1, v1, color, light);
        vertex(consumer, pose, INNER_MAX, height, INNER_MIN, u1, v0, color, light);
    }

    private static void renderCheese(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, ItemStack result) {
        if (!(result.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        BlockState state = blockItem.getBlock().defaultBlockState();
        AABB bounds = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).bounds();
        float scale = (float) Math.min(1.0, Math.min((INNER_MAX - INNER_MIN) / Math.max(bounds.getXsize(), bounds.getZsize()), (5.0 / 16.0) / bounds.getYsize()));
        poseStack.pushPose();
        poseStack.translate(0.5F, FLOOR / 16.0F, 0.5F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-(bounds.minX + bounds.maxX) / 2.0, -bounds.minY, -(bounds.minZ + bounds.maxZ) / 2.0);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffers, light, overlay);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light) {
        consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }

    private static final class Animation {
        private float lastTime = -1;
        private float fill = -1;
        private float crank = -1;
    }
}
