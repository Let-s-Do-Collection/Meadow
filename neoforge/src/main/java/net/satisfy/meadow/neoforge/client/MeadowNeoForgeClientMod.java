package net.satisfy.meadow.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.satisfy.foundation.client.gui.LetsDoConfigScreen;
import net.satisfy.meadow.Meadow;

@Mod(value = Meadow.MOD_ID, dist = Dist.CLIENT)
public class MeadowNeoForgeClientMod {
    public MeadowNeoForgeClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new LetsDoConfigScreen(parent, Meadow.MOD_ID, screen -> new ConfigurationScreen(modContainer, screen)));
    }
}
