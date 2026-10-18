package net.satisfy.meadow.fabric.core.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.satisfy.foundation.client.gui.LetsDoConfigScreen;
import net.satisfy.meadow.Meadow;

@Environment(EnvType.CLIENT)
public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new LetsDoConfigScreen(parent, Meadow.MOD_ID, screen -> AutoConfig.getConfigScreen(MeadowFabricConfig.class, screen).get());
    }
}
