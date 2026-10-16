package net.satisfy.meadow.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.satisfy.meadow.client.MeadowClient;

public class MeadowFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MeadowClient.preInitClient();
        MeadowClient.initClient();
    }
}
