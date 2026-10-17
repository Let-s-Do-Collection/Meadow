package net.satisfy.meadow.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.satisfy.meadow.client.ExtraModels;
import net.satisfy.meadow.client.MeadowClient;

public class MeadowFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MeadowClient.preInitClient();
        MeadowClient.initClient();
        ModelLoadingPlugin.register(context -> context.addModels(ExtraModels.all()));
    }
}
