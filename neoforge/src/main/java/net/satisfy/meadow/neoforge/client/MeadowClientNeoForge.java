package net.satisfy.meadow.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.MeadowClient;
import net.satisfy.meadow.client.gui.CheeseFormGui;
import net.satisfy.meadow.client.gui.CookingCauldronGui;
import net.satisfy.meadow.client.gui.WoodcutterGui;
import net.satisfy.meadow.client.particle.FondueBubbleParticle;
import net.satisfy.meadow.core.registry.ParticleTypeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;

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
    }
}
