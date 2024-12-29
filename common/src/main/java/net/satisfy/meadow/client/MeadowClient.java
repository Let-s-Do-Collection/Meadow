package net.satisfy.meadow.client;

import de.cristelknight.doapi.client.render.block.storage.api.StorageBlockEntityRenderer;
import de.cristelknight.doapi.terraform.sign.TerraformSignHelper;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.CheeseFormGui;
import net.satisfy.meadow.client.gui.CookingCauldronGui;
import net.satisfy.meadow.client.gui.FondueGui;
import net.satisfy.meadow.client.gui.WoodcutterGui;
import net.satisfy.meadow.client.model.FurArmorHat;
import net.satisfy.meadow.client.model.WaterBuffaloModel;
import net.satisfy.meadow.client.model.WoolyCowModel;
import net.satisfy.meadow.client.render.block.storage.CheeseRackRenderer;
import net.satisfy.meadow.client.render.block.storage.WheelBarrowRenderer;
import net.satisfy.meadow.client.render.block.storage.FlowerPotSmallRenderer;
import net.satisfy.meadow.client.render.entity.ShearableVarCowRenderer;
import net.satisfy.meadow.client.render.entity.WaterBuffaloRenderer;
import net.satisfy.meadow.registry.*;

import static net.satisfy.meadow.registry.ObjectRegistry.*;

public class MeadowClient {
    public static final ModelLayerLocation SHEARABLE_MEADOW_COW_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("shearable_meadow_cow"), "head");
    public static final ModelLayerLocation WATER_BUFFALO_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("water_buffalo"), "head");

    public static void preInitClient() {
        registerEntityRenderers();
        registerEntityModelLayers();

        TerraformSignHelper.regsterSignSprite(BoatsAndSignsRegistry.PINE_SIGN_TEXTURE);
        TerraformSignHelper.regsterSignSprite(BoatsAndSignsRegistry.PINE_HANGING_SIGN_TEXTURE);
    }

    public static void initClient() {


        RenderTypeRegistry.register(RenderType.cutout(),
                PINE_DOOR.get(), PINE_TRAPDOOR.get(), DELPHINIUM.get(), ALPINE_POPPY.get(), SAXIFRAGE.get(),
                ENZIAN.get(), COOKING_CAULDRON.get(), FRAME.get(), TABLE.get(), FIRE_LOG.get(), ERIOPHORUM.get(),
                ERIOPHORUM_TALL.get(), SMALL_FIR.get(), PINE_SAPLING.get(), CHAIR.get(),
                POTTED_DELPHINIUM.get(), POTTED_ALPINE_POPPY.get(), POTTED_SAXIFRAGE.get(), POTTED_ENZIAN.get(),
                POTTED_ERIOPHORUM.get(), ERIOPHORUM_TALL.get(), PINE_SAPLING.get(), POTTED_PINE_SAPLING.get(), FIRE_LILY.get(),
                POTTED_FIRE_LILY.get(), WOODEN_FLOWER_POT_SMALL.get(), FONDUE.get(), OIL_LANTERN.get(), WHEELBARROW.get(),
                PINE_LEAVES_2.get(), WOODEN_FLOWER_POT_BIG.get(), WOODCUTTER.get());

        RenderTypeRegistry.register(RenderType.translucent(), HEART_PATTERNED_WINDOW.get(), SUN_PATTERNED_WINDOW.get(), PINE_WINDOW.get());

        registerStorageTypeRenderers();
        registerClientScreens();

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                    if (world == null || pos == null) {
                        return -1;
                    }
                    return BiomeColors.getAverageWaterColor(world, pos);
                }, WOODEN_WATER_CAULDRON, WATERING_CAN
        );
    }

    public static void registerStorageTypeRenderers(){
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.WHEEL_BARROW, new WheelBarrowRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.FLOWER_POT_SMALL, new FlowerPotSmallRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.CHEESE_RACK, new CheeseRackRenderer());
    }

    private static void registerClientScreens() {
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.CHEESE_FORM_SCREEN_HANDLER.get(), CheeseFormGui::new);
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.WOODCUTTER_SCREEN_HANDLER.get(), WoodcutterGui::new);
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.COOKING_CAULDRON_SCREEN_HANDLER.get(), CookingCauldronGui::new);
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.FONDUE_SCREEN_HANDLER.get(), FondueGui::new);
    }

    private static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityRegistry.SHEARABLE_MEADOW_VAR_COW, ShearableVarCowRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.WATER_BUFFALO, WaterBuffaloRenderer::new);
    }

    public static void registerEntityModelLayers() {
        EntityModelLayerRegistry.register(FurArmorHat.LAYER_LOCATION, FurArmorHat::createBodyLayer);
        EntityModelLayerRegistry.register(SHEARABLE_MEADOW_COW_MODEL_LAYER, WoolyCowModel::createBodyLayer);
        EntityModelLayerRegistry.register(WATER_BUFFALO_MODEL_LAYER, WaterBuffaloModel::getTexturedModelData);


        ArmorRegistry.registerArmorModelLayers();
    }
}