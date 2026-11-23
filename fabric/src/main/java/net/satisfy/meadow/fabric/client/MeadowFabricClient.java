package net.satisfy.meadow.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.satisfy.foundation.fabric.client.FoundationArmorRenderer;
import net.satisfy.meadow.client.ExtraModels;
import net.satisfy.meadow.client.MeadowClient;
import net.satisfy.meadow.core.registry.ObjectRegistry;

public class MeadowFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MeadowClient.preInitClient();
        MeadowClient.initClient();
        ModelLoadingPlugin.register(context -> context.addModels(ExtraModels.all()));
        ArmorRenderer.register(FoundationArmorRenderer.INSTANCE, ObjectRegistry.FUR_HELMET.get(), ObjectRegistry.FUR_CHESTPLATE.get(), ObjectRegistry.FUR_LEGGINGS.get(), ObjectRegistry.FUR_BOOTS.get());
    }
}
