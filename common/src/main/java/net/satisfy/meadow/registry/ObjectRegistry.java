package net.satisfy.meadow.registry;

import de.cristelknight.doapi.Util;
import de.cristelknight.doapi.common.block.FlowerBoxBlock;
import de.cristelknight.doapi.common.block.*;
import de.cristelknight.doapi.common.registry.DoApiSoundEventRegistry;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.block.*;
import net.satisfy.meadow.block.storage.CheeseRackBlock;
import net.satisfy.meadow.block.storage.MeadowFlowerPotBigBlock;
import net.satisfy.meadow.block.storage.WheelBarrowBlock;
import net.satisfy.meadow.block.storage.FlowerPotSmallBlock;
import net.satisfy.meadow.item.*;
import net.satisfy.meadow.item.armor.FurBoots;
import net.satisfy.meadow.item.armor.FurChest;
import net.satisfy.meadow.item.armor.FurHead;
import net.satisfy.meadow.item.armor.FurLegs;
import net.satisfy.meadow.util.WoodenCauldronBehavior;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ObjectRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Meadow.MOD_ID, Registries.ITEM);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Meadow.MOD_ID, Registries.BLOCK);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();
    public static final RegistrySupplier<Block> ALPINE_SALT_ORE = registerWithItem("alpine_salt_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F),  UniformInt.of(0, 2)));
    public static final RegistrySupplier<Block> ALPINE_COAL_ORE = registerWithItem("alpine_coal_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F),  UniformInt.of(0, 2)));
    public static final RegistrySupplier<Block> ALPINE_LAPIS_ORE = registerWithItem("alpine_lapis_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<Block> ALPINE_GOLD_ORE = registerWithItem("alpine_gold_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F),  UniformInt.of(0, 1)));
    public static final RegistrySupplier<Block> ALPINE_EMERALD_ORE = registerWithItem("alpine_emerald_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<Block> ALPINE_IRON_ORE = registerWithItem("alpine_iron_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<Block> ALPINE_COPPER_ORE = registerWithItem("alpine_copper_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<Block> ALPINE_DIAMOND_ORE = registerWithItem("alpine_diamond_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<Block> ALPINE_REDSTONE_ORE = registerWithItem("alpine_redstone_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f).requiresCorrectToolForDrops()));
    public static final RegistrySupplier<Block> LIMESTONE = registerWithItem("limestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistrySupplier<Block> LIMESTONE_STAIRS = registerWithItem("limestone_stairs", () -> new StairBlock(LIMESTONE.get().defaultBlockState(), BlockBehaviour.Properties.copy(LIMESTONE.get())));
    public static final RegistrySupplier<Block> LIMESTONE_SLAB = registerWithItem("limestone_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(LIMESTONE.get())));
    public static final RegistrySupplier<Block> COBBLED_LIMESTONE = registerWithItem("cobbled_limestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static final RegistrySupplier<Block> COBBLED_LIMESTONE_STAIRS = registerWithItem("cobbled_limestone_stairs", () -> new StairBlock(COBBLED_LIMESTONE.get().defaultBlockState(), BlockBehaviour.Properties.copy(COBBLED_LIMESTONE.get())));
    public static final RegistrySupplier<Block> COBBLED_LIMESTONE_SLAB = registerWithItem("cobbled_limestone_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(COBBLED_LIMESTONE.get())));
    public static final RegistrySupplier<Block> LIMESTONE_BRICKS = registerWithItem("limestone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistrySupplier<Block> LIMESTONE_BRICK_STAIRS = registerWithItem("limestone_brick_stairs", () -> new StairBlock(LIMESTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(LIMESTONE_BRICKS.get())));
    public static final RegistrySupplier<Block> LIMESTONE_BRICK_SLAB = registerWithItem("limestone_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(LIMESTONE_BRICKS.get())));
    public static final RegistrySupplier<Block> MOSSY_COBBLED_LIMESTONE = registerWithItem("mossy_cobbled_limestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.MOSSY_COBBLESTONE)));
    public static final RegistrySupplier<Block> MOSSY_COBBLED_LIMESTONE_STAIRS = registerWithItem("mossy_cobbled_limestone_stairs", () -> new StairBlock(MOSSY_COBBLED_LIMESTONE.get().defaultBlockState(), BlockBehaviour.Properties.copy(MOSSY_COBBLED_LIMESTONE.get())));
    public static final RegistrySupplier<Block> MOSSY_COBBLED_LIMESTONE_SLAB = registerWithItem("mossy_cobbled_limestone_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(MOSSY_COBBLED_LIMESTONE.get())));
    public static final RegistrySupplier<Block> MOSSY_LIMESTONE_BRICKS = registerWithItem("mossy_limestone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.MOSSY_STONE_BRICKS)));
    public static final RegistrySupplier<Block> MOSSY_LIMESTONE_BRICK_STAIRS = registerWithItem("mossy_limestone_brick_stairs", () -> new StairBlock(LIMESTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(LIMESTONE_BRICKS.get())));
    public static final RegistrySupplier<Block> MOSSY_LIMESTONE_BRICK_SLAB = registerWithItem("mossy_limestone_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(MOSSY_LIMESTONE_BRICKS.get())));
    public static final RegistrySupplier<Block> CRACKED_LIMESTONE_BRICKS = registerWithItem("cracked_limestone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CRACKED_STONE_BRICKS)));
    public static final RegistrySupplier<Block> CHISELED_LIMESTONE_BRICKS = registerWithItem("chiseled_limestone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CHISELED_STONE_BRICKS)));
    public static final RegistrySupplier<Block> POLISHED_LIMESTONE_BRICKS = registerWithItem("polished_limestone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE)));
    public static final RegistrySupplier<Block> LIMESTONE_WALL = registerWithItem("limestone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(LIMESTONE.get())));
    public static final RegistrySupplier<Block> COBBLED_LIMESTONE_WALL = registerWithItem("cobbled_limestone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(COBBLED_LIMESTONE.get())));
    public static final RegistrySupplier<Block> LIMESTONE_BRICK_WALL = registerWithItem("limestone_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(LIMESTONE_BRICKS.get())));
    public static final RegistrySupplier<Block> MOSSY_COBBLED_LIMESTONE_WALL = registerWithItem("mossy_cobbled_limestone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(MOSSY_COBBLED_LIMESTONE.get())));
    public static final RegistrySupplier<Block> MOSSY_LIMESTONE_BRICK_WALL = registerWithItem("mossy_limestone_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(MOSSY_LIMESTONE_BRICKS.get())));
    public static final RegistrySupplier<Block> STOVE = registerWithItem("stove_tiles", () -> new StoveBlockMain(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistrySupplier<Block> STOVE_WOOD = registerWithItem("stove_tiles_wood", () -> new StoveBlockWood(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(state -> state.getValue(StoveBlockWood.LIT) ? 13 : 0).randomTicks(), Direction.UP));
    public static final RegistrySupplier<Block> STOVE_LID = registerWithItem("stove_tiles_lid", () -> new StoveBlockSmoker(BlockBehaviour.Properties.copy(Blocks.SMOKER), Direction.UP));
    public static final RegistrySupplier<Block> STOVE_BENCH = registerWithItem("stove_tiles_bench", () -> new StoveBlockBench(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistrySupplier<Block> PINE_LOG = registerLog("pine_log");
    public static final RegistrySupplier<Block> PINE_WOOD = registerLog("pine_wood");
    public static final RegistrySupplier<Block> STRIPPED_PINE_WOOD = registerLog("stripped_pine_wood");
    public static final RegistrySupplier<Block> STRIPPED_PINE_LOG = registerLog("stripped_pine_log");
    public static final RegistrySupplier<Block> PINE_BEAM = registerLog("pine_beam");
    public static final RegistrySupplier<Block> PINE_PLANKS = registerWithItem("pine_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> PINE_STAIRS = registerWithItem("pine_stairs", () -> new StairBlock(PINE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(PINE_PLANKS.get())));
    public static final RegistrySupplier<Block> PINE_SLAB = registerWithItem("pine_slab", () -> new SlabBlock(getSlabSettings()));
    public static final RegistrySupplier<Block> PINE_PRESSURE_PLATE = registerWithItem("pine_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().noCollission().strength(0.5f).sound(SoundType.WOOD).mapColor(PINE_PLANKS.get().defaultMapColor()), BlockSetType.OAK));
    public static final RegistrySupplier<Block> PINE_BUTTON = registerWithItem("pine_button", () -> woodenButton(FeatureFlags.VANILLA));
    public static final RegistrySupplier<Block> PINE_TRAPDOOR = registerWithItem("pine_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_TRAPDOOR), BlockSetType.OAK));
    public static final RegistrySupplier<Block> PINE_DOOR = registerWithItem("pine_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.WOOD).noOcclusion().mapColor(PINE_PLANKS.get().defaultMapColor()), BlockSetType.OAK));
    public static final RegistrySupplier<Block> PINE_BARN_TRAPDOOR = registerWithItem("pine_barn_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_TRAPDOOR), BlockSetType.OAK));
    public static final RegistrySupplier<Block> PINE_BARN_DOOR = registerWithItem("pine_barn_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.WOOD).noOcclusion().mapColor(PINE_PLANKS.get().defaultMapColor()), BlockSetType.OAK));
    public static final RegistrySupplier<Block> PINE_FENCE = registerWithItem("pine_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> PINE_FENCE_GATE = registerWithItem("pine_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD).mapColor(PINE_PLANKS.get().defaultMapColor()), WoodType.OAK));
    public static final RegistrySupplier<Block> PINE_RAILING = registerWithItem("pine_railing", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> SHUTTER_BLOCK = registerWithItem("shutter_block", () -> new ShutterBlock(BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> SHUTTER_BLOCK_POPPY = registerWithItem("shutter_block_poppy", () -> new ShutterBlock(BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> SHUTTER_BLOCK_FIR = registerWithItem("shutter_block_fir", () -> new ShutterBlock(BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> SHUTTER_BLOCK_BERRY = registerWithItem("shutter_block_berry", () -> new ShutterBlock(BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> SHELF = registerWithItem("shelf", () -> new CabinetBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), DoApiSoundEventRegistry.CABINET_OPEN, DoApiSoundEventRegistry.CABINET_CLOSE));
    public static final RegistrySupplier<Block> CHEESE_RACK = registerWithItem("cheese_rack", () -> new CheeseRackBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistrySupplier<Block> CHAIR = registerWithItem("chair", () -> new ChairBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> TABLE = registerWithItem("table", () -> new TableBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistrySupplier<Block> BENCH = registerWithItem("bench", () -> new BenchBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> STONE_TABLE = registerWithItem("stone_table", () -> new TableBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f, 0.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistrySupplier<Block> STONE_BENCH = registerWithItem("stone_bench", () -> new BenchBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4f, 0.0f).sound(SoundType.STONE)));
    public static final RegistrySupplier<Block> HEART_PATTERNED_WINDOW = registerWithItem("heart_patterned_window", () -> new WindowBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(0.3f).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistrySupplier<Block> SUN_PATTERNED_WINDOW = registerWithItem("sun_patterned_window", () -> new WindowBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(0.3f).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistrySupplier<Block> PINE_WINDOW = registerWithItem("pine_window", () -> new WindowBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(0.3f).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistrySupplier<Block> FLECKED_WOOL = registerWithItem("flecked_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> FLECKED_CARPET = registerWithItem("flecked_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> FLECKED_BED = registerWithItem("flecked_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> HIGHLAND_WOOL = registerWithItem("highland_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> HIGHLAND_CARPET = registerWithItem("highland_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> HIGHLAND_BED = registerWithItem("highland_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> PATCHED_WOOL = registerWithItem("patched_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> PATCHED_CARPET = registerWithItem("patched_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> PATCHED_BED = registerWithItem("patched_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> ROCKY_WOOL = registerWithItem("rocky_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> ROCKY_CARPET = registerWithItem("rocky_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> ROCKY_BED = registerWithItem("rocky_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> UMBRA_WOOL = registerWithItem("umbra_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> UMBRA_CARPET = registerWithItem("umbra_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> UMBRA_BED = registerWithItem("umbra_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> INKY_WOOL = registerWithItem("inky_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> INKY_CARPET = registerWithItem("inky_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> INKY_BED = registerWithItem("inky_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> WARPED_WOOL = registerWithItem("warped_wool", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> WARPED_CARPET = registerWithItem("warped_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> WARPED_BED = registerWithItem("warped_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> STRAW_BED = registerWithItem("straw_bed", () -> new MeadowBedBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion()));
    public static final RegistrySupplier<Block> CHEESE_FORM = registerWithItem("cheese_form", () -> new CheeseFormBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.1f).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> FONDUE = registerWithItem("fondue", () -> new FondueBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL).lightLevel((blockState) -> 13)));
    public static final RegistrySupplier<Block> COOKING_CAULDRON = registerWithItem("cooking_cauldron", () -> new CookingCauldronBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL).lightLevel(state -> state.getValue(CookingCauldronBlock.HANGING) ? 13 : 0)));
    public static final RegistrySupplier<Block> FRAME = registerWithItem("frame", () -> new FrameBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().lightLevel((blockState) -> 13).strength(3.5F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistrySupplier<Block> WOODCUTTER = registerWithItem("woodcutter", () -> new WoodcutterBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> WOODEN_CAULDRON = registerWithItem("wooden_cauldron", () -> new WoodenCauldronBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2.0f).noOcclusion().sound(SoundType.WOOD).mapColor(MapColor.PODZOL)));
    public static final RegistrySupplier<Block> WHEELBARROW = registerWithItem("wheelbarrow", () -> new WheelBarrowBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> WOODEN_WATER_CAULDRON = registerWithoutItem("wooden_water_cauldron", () -> new LayeredCauldronBlock(BlockBehaviour.Properties.copy(ObjectRegistry.WOODEN_CAULDRON.get()), LayeredCauldronBlock.RAIN, WoodenCauldronBehavior.WATER));
    public static final RegistrySupplier<Block> WOODEN_POWDER_SNOW_CAULDRON = registerWithoutItem("wooden_powder_snow_cauldron", () -> new WoodenPowderSnowCauldronBlock(BlockBehaviour.Properties.copy(ObjectRegistry.WOODEN_CAULDRON.get()), LayeredCauldronBlock.SNOW, WoodenCauldronBehavior.POWDER_SNOW));
    public static final RegistrySupplier<Block> FIRE_LOG = registerWithItem("fire_log", () -> new FireLog(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> CAN = registerWithItem("can", () -> new CanBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(0.8f).noOcclusion().sound(SoundType.METAL)));
    public static final RegistrySupplier<Block> CLIMBING_ROPE = registerWithoutItem("climbing_rope", () -> new ClimbingRopeBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).noCollission()));
    public static final RegistrySupplier<Block> WATERING_CAN = registerWithoutItem("watering_can", () -> new WateringCanBlock(BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)));
    public static final RegistrySupplier<Item> WATERING_CAN_ITEM = registerItem("watering_can", () -> new WateringCanItem(ObjectRegistry.WATERING_CAN.get(), getSettings().durability(5)));
    public static final RegistrySupplier<Block> CLIMBING_ROPE_TOPMOUNT = registerWithItem("climbing_rope_topmount", () -> new ClimbingRopeTopmountBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).noCollission()));
    public static final RegistrySupplier<Block> OIL_LANTERN = registerWithItem("oil_lantern", () -> new OilLantern(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.LANTERN).lightLevel((blockState) -> 14).noOcclusion()));
    public static final RegistrySupplier<Block> CAMERA = registerWithItem("camera", () -> new CameraBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistrySupplier<Block> DOORMAT = registerWithItem("doormat", () -> new DoormatBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
    public static final RegistrySupplier<Block> WOODEN_FLOWER_POT_BIG = registerWithItem("wooden_flower_pot_big", () -> new MeadowFlowerPotBigBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).instabreak()));
    public static final RegistrySupplier<Block> WOODEN_FLOWER_POT_SMALL = registerWithItem("wooden_flower_pot_small", () -> new FlowerPotSmallBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).instabreak()));
    public static final RegistrySupplier<Block> WOODEN_FLOWER_BOX = registerWithItem("wooden_flower_box", () -> new FlowerBoxBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).instabreak()));
    public static final RegistrySupplier<Item> FUR_HELMET = registerItem("fur_helmet", () -> new FurHead(ArmorMaterialRegistry.FUR_ARMOR, ArmorItem.Type.HELMET, getSettings().rarity(Rarity.EPIC), Meadow.identifier("textures/models/armor/fur.png")));
    public static final RegistrySupplier<Item> FUR_CHESTPLATE = registerItem("fur_chestplate", () -> new FurChest(ArmorMaterialRegistry.FUR_ARMOR, getSettings().rarity(Rarity.RARE)));
    public static final RegistrySupplier<Item> FUR_LEGGINGS = registerItem("fur_leggings", () -> new FurLegs(ArmorMaterialRegistry.FUR_ARMOR, getSettings().rarity(Rarity.RARE)));
    public static final RegistrySupplier<Item> FUR_BOOTS = registerItem("fur_boots", () -> new FurBoots(ArmorMaterialRegistry.FUR_ARMOR, getSettings().rarity(Rarity.RARE)));
    public static final RegistrySupplier<Block> SMALL_FIR = registerWithItem("small_fir", () -> new DoublePlantBlock(BlockBehaviour.Properties.copy(Blocks.ROSE_BUSH)));
    public static final RegistrySupplier<Block> PINE_SAPLING = registerWithItem("pine_sapling", () -> new SaplingBlock(new AbstractTreeGrower() {
        @Override
        protected @NotNull ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource random, boolean bees) {return configuredFeatureKey("forest_pine");
        }
    }, BlockBehaviour.Properties.copy(Blocks.SPRUCE_SAPLING)));
    public static final RegistrySupplier<Block> ALPINE_POPPY = registerWithItem("alpine_poppy", () -> new FlowerBlock(MobEffects.HEAL, 1, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> DELPHINIUM = registerWithItem("delphinium", () -> new FlowerBlock(MobEffects.HEAL, 1, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> SAXIFRAGE = registerWithItem("saxifrage", () -> new FlowerBlock(MobEffects.HEAL, 1, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> ENZIAN = registerWithItem("enzian", () -> new FlowerBlock(MobEffects.HEAL, 1, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> FIRE_LILY = registerWithItem("fire_lily", () -> new FlowerBlock(MobEffects.HEAL, 1, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> ERIOPHORUM = registerWithItem("eriophorum", () -> new FlowerBlock(MobEffects.HEAL, 1, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> ERIOPHORUM_TALL = registerWithItem("eriophorum_tall", () -> new TallFlowerBlock(BlockBehaviour.Properties.copy(Blocks.ROSE_BUSH)));
    public static final RegistrySupplier<Block> PINE_LEAVES = registerWithItem("pine_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2f).randomTicks().sound(SoundType.GRASS).noOcclusion().isViewBlocking((state, world, pos) -> false).isSuffocating((state, world, pos) -> false)));
    public static final RegistrySupplier<Block> ALPINE_BIRCH_LEAVES_HANGING = registerWithItem("alpine_birch_leaves_hanging", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2f).randomTicks().sound(SoundType.GRASS).noOcclusion().isViewBlocking((state, world, pos) -> false).isSuffocating((state, world, pos) -> false)));
    public static final RegistrySupplier<Item> CHEESECAKE_SLICE = registerItem("cheesecake_slice", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> CHEESE_TART_SLICE = registerItem("cheese_tart_slice", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_CHEESE = registerItem("piece_of_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_SHEEP_CHEESE = registerItem("piece_of_sheep_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_GRAIN_CHEESE = registerItem("piece_of_grain_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_AMETHYST_CHEESE = registerItem("piece_of_amethyst_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_BUFFALO_CHEESE = registerItem("piece_of_buffalo_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_GOAT_CHEESE = registerItem("piece_of_goat_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> PIECE_OF_WARPED_CHEESE = registerItem("piece_of_warped_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 1), 1.0F).build())));
    public static final RegistrySupplier<Block> CHEESECAKE = registerWithItem("cheesecake", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.CHEESECAKE_SLICE, CheeseBlock.CheeseType.CAKE));
    public static final RegistrySupplier<Block> CHEESE_TART = registerWithItem("cheese_tart", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.CHEESE_TART_SLICE, CheeseBlock.CheeseType.CAKE));
    public static final RegistrySupplier<Block> CHEESE_BLOCK = registerWithItem("cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_CHEESE, CheeseBlock.CheeseType.REGULAR));
    public static final RegistrySupplier<Block> AMETHYST_CHEESE_BLOCK = registerWithItem("amethyst_cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_AMETHYST_CHEESE, CheeseBlock.CheeseType.REGULAR));
    public static final RegistrySupplier<Block> BUFFALO_CHEESE_BLOCK = registerWithItem("buffalo_cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_BUFFALO_CHEESE, CheeseBlock.CheeseType.BUFFALO));
    public static final RegistrySupplier<Block> WARPED_CHEESE_BLOCK = registerWithItem("warped_cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_WARPED_CHEESE, CheeseBlock.CheeseType.WARPED));
    public static final RegistrySupplier<Block> GRAIN_CHEESE_BLOCK = registerWithItem("grain_cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_GRAIN_CHEESE, CheeseBlock.CheeseType.GRAIN));
    public static final RegistrySupplier<Block> SHEEP_CHEESE_BLOCK = registerWithItem("sheep_cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_SHEEP_CHEESE, CheeseBlock.CheeseType.SHEEP));
    public static final RegistrySupplier<Block> GOAT_CHEESE_BLOCK = registerWithItem("goat_cheese_block", () -> new CheeseBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ObjectRegistry.PIECE_OF_GOAT_CHEESE, CheeseBlock.CheeseType.GOAT));
    public static final RegistrySupplier<Item> ALPINE_SALT = registerItem("alpine_salt", () -> new Item(getSettings()));
    public static final RegistrySupplier<Item> RENNET = registerItem("rennet", () -> new Item(getSettings()));
    public static final RegistrySupplier<Item> CHEESE_SANDWICH = registerItem("cheese_sandwich", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.7f).build())));
    public static final RegistrySupplier<Item> CHEESE_ROLL = registerItem("cheese_roll", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Item> CHEESE_STICK = registerItem("cheese_stick", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6f).build())));
    public static final RegistrySupplier<Item> RAW_BUFFALO_MEAT = registerItem("raw_buffalo_meat", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).build())));
    public static final RegistrySupplier<Item> COOKED_BUFFALO_MEAT = registerItem("cooked_buffalo_meat", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).build())));
    public static final RegistrySupplier<Block> ROASTED_HAM = registerWithItem("roasted_ham", () -> new FoodBlock(Block.Properties.of(), new MobEffectInstance(MobEffects.HEAL, 3600, 1), 8, 0.9f));
    public static final RegistrySupplier<Item> SAUSAGE_WITH_CHEESE = registerItem("sausage_with_cheese", () -> new Item(getSettings().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.9f).build())));
    public static final RegistrySupplier<Item> WOODEN_BUCKET = registerItem("wooden_bucket", () -> new WoodenBucket(Fluids.EMPTY, getSettings().stacksTo(16)));
    public static final RegistrySupplier<Item> WOODEN_WATER_BUCKET = registerItem("wooden_water_bucket", () -> new WoodenBucket(Fluids.WATER, getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_MILK_BUCKET = registerItem("wooden_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_SHEEP_MILK_BUCKET = registerItem("wooden_sheep_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_BUFFALO_MILK_BUCKET = registerItem("wooden_buffalo_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_GOAT_MILK_BUCKET = registerItem("wooden_goat_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_WARPED_MILK_BUCKET = registerItem("wooden_warped_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_GRAIN_MILK_BUCKET = registerItem("wooden_grain_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WOODEN_AMETHYST_MILK_BUCKET = registerItem("wooden_amethyst_milk_bucket", () -> new WoodenMilkBucket(getSettings().stacksTo(1).craftRemainder(ObjectRegistry.WOODEN_BUCKET.get())));
    public static final RegistrySupplier<Item> WATER_BUFFALO_SPAWN_EGG_ITEM = registerItem("water_buffalo_spawn_egg", () -> new ArchitecturySpawnEggItem(EntityRegistry.WATER_BUFFALO, -1, -1, getSettings()));
    public static final RegistrySupplier<Item> WOOLY_COW_SPAWN_EGG_ITEM = registerItem("wooly_cow_spawn_egg", () -> new ArchitecturySpawnEggItem(EntityRegistry.SHEARABLE_MEADOW_VAR_COW, -1, -1, getSettings()));
    public static final RegistrySupplier<Block> POTTED_ENZIAN = registerWithoutItem("potted_enzian", () -> new FlowerPotBlock(ObjectRegistry.ENZIAN.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> POTTED_FIRE_LILY = registerWithoutItem("potted_fire_lily", () -> new FlowerPotBlock(ObjectRegistry.FIRE_LILY.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> POTTED_ALPINE_POPPY = registerWithoutItem("potted_alpine_poppy", () -> new FlowerPotBlock(ObjectRegistry.ALPINE_POPPY.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> POTTED_SAXIFRAGE = registerWithoutItem("potted_saxifrage", () -> new FlowerPotBlock(ObjectRegistry.SAXIFRAGE.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> POTTED_DELPHINIUM = registerWithoutItem("potted_delphinium", () -> new FlowerPotBlock(ObjectRegistry.DELPHINIUM.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> POTTED_ERIOPHORUM = registerWithoutItem("potted_eriophorum", () -> new FlowerPotBlock(ObjectRegistry.ERIOPHORUM.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> POTTED_PINE_SAPLING = registerWithoutItem("potted_pine_sapling", () -> new FlowerPotBlock(ObjectRegistry.PINE_SAPLING.get(), BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistrySupplier<Block> ALPINE_OAK_LOG = registerWithItem("alpine_oak_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0f)));
    public static final RegistrySupplier<Block> ALPINE_BIRCH_LOG = registerWithItem("alpine_birch_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0f)));
    public static final RegistrySupplier<Block> PINE_LEAVES_2 = registerWithoutItem("pine_leaves_2", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LEAVES)));
    public static final RegistrySupplier<Item>  MEADOW_STANDARD = registerItem("meadow_standard", () -> new MeadowStandardItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));


    private static RegistrySupplier<Block> registerLog(String path) {
        return registerWithItem(path, () -> new RotatedPillarBlock(getLogBlockSettings()));
    }

    private static BlockBehaviour.Properties getLogBlockSettings() {
        return BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0F).sound(SoundType.WOOD);
    }

    private static BlockBehaviour.Properties getSlabSettings() {
        return getLogBlockSettings().explosionResistance(3.0F);
    }


    private static Item.Properties getSettings(Consumer<Item.Properties> consumer) {
        Item.Properties settings = new Item.Properties();
        consumer.accept(settings);
        return settings;
    }
    
    static Item.Properties getSettings() {
        return getSettings(settings -> {
        });
    }


    public static void init() {
        Meadow.LOGGER.debug("Registering Mod Block and Items for " + Meadow.MOD_ID);
        ITEMS.register();
        BLOCKS.register();
    }

    public static void commonInit() {
        FuelRegistry.register(300, PINE_FENCE.get(), PINE_FENCE_GATE.get(), PINE_PLANKS.get(), PINE_LOG.get(), PINE_WOOD.get(), STRIPPED_PINE_LOG.get(), STRIPPED_PINE_WOOD.get());
        FuelRegistry.register(2800, FIRE_LOG.get());

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Meadow.identifier(name));
    }

    private static ButtonBlock woodenButton(FeatureFlag... featureFlags) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY);
        if (featureFlags.length > 0) {
            properties = properties.requiredFeatures(featureFlags);
        }

        return new ButtonBlock(properties, BlockSetType.OAK, 30, true);
    }

    
    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        return Util.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, Meadow.identifier(name), block);
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(String path, Supplier<T> block) {
        return Util.registerWithoutItem(BLOCKS, BLOCK_REGISTRAR, Meadow.identifier(path), block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(String path, Supplier<T> itemSupplier) {
        return Util.registerItem(ITEMS, ITEM_REGISTRAR, Meadow.identifier(path), itemSupplier);
    }
}

