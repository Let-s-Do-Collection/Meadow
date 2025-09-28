package net.satisfy.meadow.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.CompostableRegistry;
import net.satisfy.meadow.fabric.core.config.MeadowFabricConfig;
import net.satisfy.meadow.fabric.core.villager.FabricVillager;
import net.satisfy.meadow.fabric.core.world.FeatureModification;
import net.satisfy.meadow.fabric.core.world.MeadowBiomeModification;


public class MeadowFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoConfig.register(MeadowFabricConfig.class, GsonConfigSerializer::new);

        Meadow.init();
        CompostableRegistry.registerCompostable();
        FeatureModification.init();
        Meadow.commonSetup();
        FabricVillager.init();
        MeadowBiomeModification.init();
    }
}

