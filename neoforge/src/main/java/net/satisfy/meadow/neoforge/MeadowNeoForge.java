package net.satisfy.meadow.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.CompostableRegistry;
import net.satisfy.meadow.neoforge.core.config.MeadowNeoForgeConfig;
import net.satisfy.meadow.neoforge.core.registry.MeadowBiomeModifiers;
import net.satisfy.meadow.neoforge.core.registry.MeadowForgeVillagers;
import net.satisfy.meadow.platform.neoforge.PlatformHelperImpl;

@Mod(Meadow.MOD_ID)
public class MeadowNeoForge {
    public MeadowNeoForge(ModContainer modContainer, final IEventBus modEventBus) {
        IEventBus bus = modContainer.getEventBus();
        assert bus != null;
        PlatformHelperImpl.ENTITY_TYPES.register(bus);
        MeadowForgeVillagers.register(bus);
        bus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, MeadowNeoForgeConfig.COMMON_CONFIG);
        bus.addListener(MeadowNeoForgeConfig::onLoad);
        bus.addListener(MeadowNeoForgeConfig::onReload);
        Meadow.init();
        MeadowBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
        Meadow.commonSetup();
    }
}
