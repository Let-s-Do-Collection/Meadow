package net.satisfy.meadow.forge.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.Meadow;

public class MeadowNetworkForge {

    public static final ResourceLocation VAR_S2C = Meadow.identifier("var");
    public static final ResourceLocation VAR_REQUEST_S2C = Meadow.identifier("var_request");

    public static void registerC2SPackets() {
        registerReceiver(NetworkManager.Side.C2S, VAR_REQUEST_S2C, new VarRequestC2SPacket());
    }

    public static void registerS2CPackets() {
        registerReceiver(NetworkManager.Side.S2C, VAR_S2C, new VarS2CPacket());
    }

    private static void registerReceiver(NetworkManager.Side side, ResourceLocation id, NetworkManager.NetworkReceiver receiver) {
        NetworkManager.registerReceiver(side, id, receiver);
    }
}
