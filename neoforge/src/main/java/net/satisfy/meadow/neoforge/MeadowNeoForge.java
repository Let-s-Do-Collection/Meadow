package net.satisfy.meadow.neoforge;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.neoforge.core.config.MeadowNeoForgeConfig;
import net.satisfy.meadow.neoforge.core.registry.MeadowBiomeModifiers;
import net.satisfy.meadow.neoforge.core.registry.MeadowForgeVillagers;
import net.satisfy.meadow.neoforge.core.registry.MeadowSpawns;
import net.satisfy.meadow.platform.neoforge.PlatformHelperImpl;

import java.util.List;

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
        bus.addListener(MeadowNeoForge::reapplyFoodConfig);

        Meadow.init();
        MeadowSpawns.init();
        MeadowBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
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

    private static void reapplyFoodConfig(ModifyDefaultComponentsEvent event) {
        patchFood(event, ObjectRegistry.CHEESECAKE_SLICE, "cheesecake_slice");
        patchFood(event, ObjectRegistry.CHEESE_TART_SLICE, "cheese_tart_slice");
        patchFood(event, ObjectRegistry.CHEESE_SLICE, "cheese_slice");
        patchFood(event, ObjectRegistry.SHEEP_CHEESE_SLICE, "sheep_cheese_slice");
        patchFood(event, ObjectRegistry.GRAIN_CHEESE_SLICE, "grain_cheese_slice");
        patchFood(event, ObjectRegistry.AMETHYST_CHEESE_SLICE, "amethyst_cheese_slice");
        patchFood(event, ObjectRegistry.BUFFALO_CHEESE_SLICE, "buffalo_cheese_slice");
        patchFood(event, ObjectRegistry.GOAT_CHEESE_SLICE, "goat_cheese_slice");
        patchFood(event, ObjectRegistry.WARPED_CHEESE_SLICE, "warped_cheese_slice");
        patchFood(event, ObjectRegistry.CHEESE_SANDWICH, "cheese_sandwich");
        patchFood(event, ObjectRegistry.CHEESE_ROLL, "cheese_roll");
        patchFood(event, ObjectRegistry.CHEESE_STICK, "cheese_stick");
        patchFood(event, ObjectRegistry.RAW_BUFFALO_MEAT, "raw_buffalo_meat");
        patchFood(event, ObjectRegistry.COOKED_BUFFALO_MEAT, "cooked_buffalo_meat");
        patchFood(event, ObjectRegistry.SAUSAGE_WITH_CHEESE, "sausage_with_cheese");
    }

    private static void patchFood(ModifyDefaultComponentsEvent event, RegistrySupplier<Item> itemSupplier, String key) {
        Item item = itemSupplier.get();
        FoodProperties current = item.components().get(DataComponents.FOOD);
        if (current == null) return;

        FoodProperties updated = new FoodProperties(
                PlatformHelperImpl.getNutrition(key),
                PlatformHelperImpl.getSaturation(key),
                current.canAlwaysEat(),
                current.eatSeconds(),
                current.usingConvertsTo(),
                effects(current, PlatformHelperImpl.getEffectDuration(key))
        );
        event.modify(item, builder -> builder.set(DataComponents.FOOD, updated));
    }

    private static List<FoodProperties.PossibleEffect> effects(FoodProperties current, int duration) {
        if (duration < 0) {
            return current.effects();
        }
        return current.effects().stream()
                .map(possible -> new FoodProperties.PossibleEffect(() -> new MobEffectInstance(possible.effect().getEffect(), duration, possible.effect().getAmplifier()), possible.probability()))
                .toList();
    }
}
