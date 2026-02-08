package net.satisfy.meadow.neoforge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.CompostableRegistry;
import net.satisfy.meadow.neoforge.core.config.MeadowNeoForgeConfig;
import net.satisfy.meadow.neoforge.core.registry.MeadowBiomeModifiers;
import net.satisfy.meadow.neoforge.core.registry.MeadowForgeVillagers;
import net.satisfy.meadow.neoforge.core.registry.MeadowSpawns;
import net.satisfy.meadow.platform.neoforge.PlatformHelperImpl;

@Mod(Meadow.MOD_ID)
public class MeadowNeoForge {

    public MeadowNeoForge(ModContainer modContainer, IEventBus modEventBus) {
        IEventBus bus = modContainer.getEventBus();
        assert bus != null;

        PlatformHelperImpl.ENTITY_TYPES.register(bus);
        MeadowForgeVillagers.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::addPackFinders);

        modContainer.registerConfig(ModConfig.Type.COMMON, MeadowNeoForgeConfig.COMMON_CONFIG);
        bus.addListener(MeadowNeoForgeConfig::onLoad);
        bus.addListener(MeadowNeoForgeConfig::onReload);

        Meadow.init();
        MeadowSpawns.init();
        MeadowBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        event.addPackFinders(
                Meadow.identifier("resourcepacks/betterleaves"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Better Leaves"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );

        event.addPackFinders(
                Meadow.identifier("resourcepacks/connectedtexturesupport"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Connected Texture Support"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }
}