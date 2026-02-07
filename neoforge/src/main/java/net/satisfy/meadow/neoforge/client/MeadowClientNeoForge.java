package net.satisfy.meadow.neoforge.client;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.MeadowClient;
import net.satisfy.meadow.client.gui.CheeseFormGui;
import net.satisfy.meadow.client.gui.CookingCauldronGui;
import net.satisfy.meadow.client.gui.WoodcutterGui;
import net.satisfy.meadow.client.particle.FondueBubbleParticle;
import net.satisfy.meadow.client.particle.SoupSteamParticle;
import net.satisfy.meadow.core.entity.PineBoatEntity;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.ParticleTypeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;
import net.satisfy.meadow.neoforge.client.extensions.FurArmorBootsExtensions;
import net.satisfy.meadow.neoforge.client.extensions.FurArmorChestplateExtensions;
import net.satisfy.meadow.neoforge.client.extensions.FurArmorHatExtensions;
import net.satisfy.meadow.neoforge.client.extensions.FurArmorLeggingsExtensions;

@EventBusSubscriber(modid = Meadow.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MeadowClientNeoForge {

    @SubscribeEvent
    public static void beforeClientSetup(RegisterEvent event) {
        MeadowClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(MeadowClient::initClient);
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(ScreenHandlerRegistry.CHEESE_FORM_SCREEN_HANDLER.get(), CheeseFormGui::new);
        event.register(ScreenHandlerRegistry.WOODCUTTER_SCREEN_HANDLER.get(), WoodcutterGui::new);
        event.register(ScreenHandlerRegistry.COOKING_CAULDRON_SCREEN_HANDLER.get(), CookingCauldronGui::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypeRegistry.FONDUE_BUBBLE.get(), FondueBubbleParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.SOUP_BUBBLE.get(), FondueBubbleParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.SOUP_STEAM.get(), SoupSteamParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (PineBoatEntity.Type type : PineBoatEntity.Type.values()) {
            event.registerLayerDefinition(new ModelLayerLocation(Meadow.identifier(type.getModelLocation()), "main"), BoatModel::createBodyModel);
            event.registerLayerDefinition(new ModelLayerLocation(Meadow.identifier(type.getChestModelLocation()), "main"), ChestBoatModel::createBodyModel);
        }
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new FurArmorHatExtensions(), ObjectRegistry.FUR_HELMET.get());
        event.registerItem(new FurArmorChestplateExtensions(), ObjectRegistry.FUR_CHESTPLATE.get());
        event.registerItem(new FurArmorLeggingsExtensions(), ObjectRegistry.FUR_LEGGINGS.get());
        event.registerItem(new FurArmorBootsExtensions(), ObjectRegistry.FUR_BOOTS.get());
    }
}