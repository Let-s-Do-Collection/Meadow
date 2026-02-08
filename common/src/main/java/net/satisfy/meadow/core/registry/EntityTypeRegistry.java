package net.satisfy.meadow.core.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.block.entity.*;
import net.satisfy.meadow.core.entity.ChairEntity;
import net.satisfy.meadow.core.entity.PineBoatEntity;
import net.satisfy.meadow.core.entity.PineChestBoatEntity;
import net.satisfy.meadow.core.entity.WaterBuffaloEntity;
import net.satisfy.meadow.core.entity.WoolyCowEntity;
import net.satisfy.meadow.platform.PlatformHelper;

import java.util.HashSet;
import java.util.function.Supplier;

public class EntityTypeRegistry {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Meadow.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Meadow.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_ENTITY = registerBlockEntity("storage", () -> BlockEntityType.Builder.of(StorageBlockEntity::new, StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])).build(null));
    public static final RegistrySupplier<BlockEntityType<CookingCauldronBlockEntity>> COOKING_CAULDRON = registerBlockEntity("cooking_cauldron", () -> BlockEntityType.Builder.of(CookingCauldronBlockEntity::new, ObjectRegistry.COOKING_CAULDRON.get(), ObjectRegistry.COOKING_FRAME.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CheeseFormBlockEntity>> CHEESE_FORM_BLOCK_ENTITY = registerBlockEntity("cheese_form", () -> BlockEntityType.Builder.of(CheeseFormBlockEntity::new, ObjectRegistry.CHEESE_FORM.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CheeseRackBlockEntity>> CHEESE_RACK_BLOCK_ENTITY = registerBlockEntity("cheese_rack", () -> BlockEntityType.Builder.of(CheeseRackBlockEntity::new, ObjectRegistry.PINE_CHEESE_RACK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<StoveBlockEntity>> STOVE_BLOCK_ENTITY = registerBlockEntity("stove_block_entity", () -> BlockEntityType.Builder.of(StoveBlockEntity::new, ObjectRegistry.TILED_STOVE_SMOKER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CabinetBlockEntity>> CABINET_BLOCK_ENTITY = registerBlockEntity("cabinet", () -> BlockEntityType.Builder.of(CabinetBlockEntity::new, StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])).build(null));
    public static final RegistrySupplier<BlockEntityType<CompletionistBannerEntity>> MEADOW_BANNER = registerBlockEntity("meadow_banner", () -> BlockEntityType.Builder.of(CompletionistBannerEntity::new, ObjectRegistry.MEADOW_BANNER.get(), ObjectRegistry.MEADOW_WALL_BANNER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<PineSignBlockEntity>> MOD_SIGN = BLOCK_ENTITY_TYPES.register("mod_sign", () -> BlockEntityType.Builder.of(PineSignBlockEntity::new, ObjectRegistry.PINE_SIGN.get(), ObjectRegistry.PINE_WALL_SIGN.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<PineHangingSignBlockEntity>> MOD_HANGING_SIGN = BLOCK_ENTITY_TYPES.register("mod_hanging_sign", () -> BlockEntityType.Builder.of(PineHangingSignBlockEntity::new, ObjectRegistry.PINE_HANGING_SIGN.get(), ObjectRegistry.PINE_WALL_HANGING_SIGN.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WardrobeBlockEntity>> WARDROBE_BLOCK_ENTITY = registerBlockEntity("wardrobe", () -> BlockEntityType.Builder.of(WardrobeBlockEntity::new, ObjectRegistry.PINE_WARDROBE.get()).build(null));

    public static final RegistrySupplier<EntityType<WaterBuffaloEntity>> WATER_BUFFALO = registerEntity("water_buffalo", () -> EntityType.Builder.of(WaterBuffaloEntity::new, MobCategory.CREATURE).sized(0.9f, 1.4f).build(Meadow.identifier("water_buffalo").toString()));
    public static final RegistrySupplier<EntityType<WoolyCowEntity>> WOOLY_COW = registerEntity("wooly_cow", () -> EntityType.Builder.of(WoolyCowEntity::new, MobCategory.CREATURE).sized(0.9f, 1.4f).build(Meadow.identifier("wooly_cow").toString()));
    public static final RegistrySupplier<EntityType<ChairEntity>> CHAIR = registerEntity("chair", () -> EntityType.Builder.of(ChairEntity::new, MobCategory.MISC).sized(0.001F, 0.001F).build(Meadow.identifier("chair").toString()));

    public static final Supplier<EntityType<PineBoatEntity>> PINE_BOAT = PlatformHelper.registerBoatType("pine_boat", PineBoatEntity::new, MobCategory.MISC, 1.375F, 0.5625F, 10);
    public static final Supplier<EntityType<PineChestBoatEntity>> PINE_CHEST_BOAT = PlatformHelper.registerBoatType("pine_chest_boat", PineChestBoatEntity::new, MobCategory.MISC, 1.375F, 0.5625F, 10);

    public static void registerCow(Supplier<? extends EntityType<? extends Animal>> typeSupplier) {
        EntityAttributeRegistry.register(typeSupplier, Cow::createAttributes);
    }

    public static <T extends EntityType<?>> RegistrySupplier<T> registerEntity(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(Meadow.identifier(path), type);
    }

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String path, final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(Meadow.identifier(path), type);
    }

    public static void init() {
        ENTITY_TYPES.register();
        BLOCK_ENTITY_TYPES.register();
        registerCow(WOOLY_COW);
        registerCow(WATER_BUFFALO);
    }
}