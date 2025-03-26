package net.satisfy.meadow.core.util;

import com.mojang.datafixers.util.Pair;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.entity.ChairEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

public class GeneralUtil {
    public static final EnumProperty<LineConnectingType> LINE_CONNECTING_TYPE = EnumProperty.create("type", LineConnectingType.class);
    private static final Map<ResourceLocation, Map<BlockPos, Pair<ChairEntity, BlockPos>>> CHAIRS = new HashMap<>();


    public static <T extends Block> RegistrySupplier<T> registerWithItem(DeferredRegister<Block> registerB, Registrar<Block> registrarB, DeferredRegister<Item> registerI, Registrar<Item> registrarI, ResourceLocation name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = registerWithoutItem(registerB, registrarB, name, block);
        registerItem(registerI, registrarI, name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(DeferredRegister<Block> register, Registrar<Block> registrar, ResourceLocation path, Supplier<T> block) {
        return Platform.isNeoForge() ? register.register(path.getPath(), block) : registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(DeferredRegister<Item> register, Registrar<Item> registrar, ResourceLocation path, Supplier<T> itemSupplier) {
        return Platform.isNeoForge() ? register.register(path.getPath(), itemSupplier) : registrar.register(path, itemSupplier);
    }

    public static boolean matchesRecipe(Container inventory, NonNullList<Ingredient> recipe, int startIndex, int endIndex) {
        final List<ItemStack> validStacks = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            final ItemStack stackInSlot = inventory.getItem(i);
            if (!stackInSlot.isEmpty())
                validStacks.add(stackInSlot);
        }
        for (Ingredient entry : recipe) {
            boolean matches = false;
            for (ItemStack item : validStacks) {
                if (entry.test(item)) {
                    matches = true;
                    validStacks.remove(item);
                    break;
                }
            }
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, ChunkPos pos) {
        Objects.requireNonNull(world, "The world cannot be null");
        Objects.requireNonNull(pos, "The chunk pos cannot be null");
        return world.getChunkSource().chunkMap.getPlayers(pos, false);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, BlockPos pos) {
        Objects.requireNonNull(pos, "BlockPos cannot be null");
        return tracking(world, new ChunkPos(pos));
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

        for (int i = 0; i < times; ++i) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.joinUnoptimized(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX), BooleanOp.OR));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static Optional<Tuple<Float, Float>> getRelativeHitCoordinatesForBlockFace(
            BlockHitResult blockHitResult,
            Direction direction,
            Direction[] unAllowedDirections) {

        Direction hitDirection = blockHitResult.getDirection();

        for (Direction unAllowed : unAllowedDirections) {
            if (unAllowed == hitDirection) {
                return Optional.empty();
            }
        }

        if (hitDirection != direction && hitDirection != Direction.UP && hitDirection != Direction.DOWN) {
            return Optional.empty();
        }

        BlockPos adjacentPos = blockHitResult.getBlockPos().relative(hitDirection);
        Vec3 hitLocation = blockHitResult.getLocation().subtract(
                adjacentPos.getX(),
                adjacentPos.getY(),
                adjacentPos.getZ()
        );

        float x = (float) hitLocation.x();
        float z = (float) hitLocation.z();
        float y = (float) hitLocation.y();

        Direction effectiveDirection = (hitDirection == Direction.UP || hitDirection == Direction.DOWN)
                ? direction
                : hitDirection;

        return switch (effectiveDirection) {
            case NORTH -> Optional.of(new Tuple<>(1.0f - x, y));
            case SOUTH -> Optional.of(new Tuple<>(x, y));
            case WEST -> Optional.of(new Tuple<>(z, y));
            case EAST -> Optional.of(new Tuple<>(1.0f - z, y));
            default -> Optional.empty();
        };
    }

    public enum LineConnectingType implements StringRepresentable {
        NONE("none"),
        MIDDLE("middle"),
        LEFT("left"),
        RIGHT("right");

        private final String name;

        LineConnectingType(String type) {
            this.name = type;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static InteractionResult onUse(Level world, Player player, InteractionHand hand, BlockHitResult hit, double extraHeight) {
        if (world.isClientSide) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (GeneralUtil.isPlayerSitting(player)) return InteractionResult.PASS;
        if (hit.getDirection() == Direction.DOWN) return InteractionResult.PASS;
        BlockPos hitPos = hit.getBlockPos();
        if (!GeneralUtil.isOccupied(world, hitPos) && player.getItemInHand(hand).isEmpty()) {
            ChairEntity chair = EntityTypeRegistry.CHAIR.get().create(world);
            assert chair != null;
            chair.moveTo(hitPos.getX() + 0.5D, hitPos.getY() + 0.25D + extraHeight, hitPos.getZ() + 0.5D, 0, 0);
            if (GeneralUtil.addChairEntity(world, hitPos, chair, player.blockPosition())) {
                world.addFreshEntity(chair);
                player.startRiding(chair);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static void onStateReplaced(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            ChairEntity entity = GeneralUtil.getChairEntity(world, pos);
            if (entity != null) {
                GeneralUtil.removeChairEntity(world, pos);
                entity.ejectPassengers();
            }
        }
    }

    public static boolean addChairEntity(Level world, BlockPos blockPos, ChairEntity entity, BlockPos playerPos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);
            if (!CHAIRS.containsKey(id)) CHAIRS.put(id, new HashMap<>());
            CHAIRS.get(id).put(blockPos, Pair.of(entity, playerPos));
            return true;
        }
        return false;
    }

    public static void removeChairEntity(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);
            if (CHAIRS.containsKey(id)) {
                CHAIRS.get(id).remove(pos);
            }
        }
    }

    public static ChairEntity getChairEntity(Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            ResourceLocation id = getDimensionTypeId(world);
            if (CHAIRS.containsKey(id) && CHAIRS.get(id).containsKey(pos))
                return CHAIRS.get(id).get(pos).getFirst();
        }
        return null;
    }

    public static BlockPos getPreviousPlayerPosition(Player player, ChairEntity chairEntity) {
        if (!player.level().isClientSide()) {
            ResourceLocation id = getDimensionTypeId(player.level());
            if (CHAIRS.containsKey(id)) {
                for (Pair<ChairEntity, BlockPos> pair : CHAIRS.get(id).values()) {
                    if (pair.getFirst() == chairEntity)
                        return pair.getSecond();
                }
            }
        }
        return null;
    }

    public static boolean isOccupied(Level world, BlockPos pos) {
        ResourceLocation id = getDimensionTypeId(world);
        return GeneralUtil.CHAIRS.containsKey(id) && GeneralUtil.CHAIRS.get(id).containsKey(pos);
    }

    public static boolean isPlayerSitting(Player player) {
        for (ResourceLocation i : CHAIRS.keySet()) {
            for (Pair<ChairEntity, BlockPos> pair : CHAIRS.get(i).values()) {
                if (pair.getFirst().hasPassenger(player))
                    return true;
            }
        }
        return false;
    }

    private static ResourceLocation getDimensionTypeId(Level world) {
        return world.dimension().location();
    }

    public enum ShutterType implements StringRepresentable {
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom"),
        NONE("none");

        private final String name;

        ShutterType(String type) {
            this.name = type;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static final EnumProperty<ShutterType> SHUTTER_TYPE;

        static {
            SHUTTER_TYPE = EnumProperty.create("type", ShutterType.class);
        }
    }

    public interface WoodenCauldronBehavior extends CauldronInteraction {
        InteractionMap EMPTY = CauldronInteraction.newInteractionMap("empty");
        InteractionMap WATER = CauldronInteraction.newInteractionMap("water");
        InteractionMap POWDER_SNOW = CauldronInteraction.newInteractionMap("powder_snow");

        CauldronInteraction FILL_WATER = (state, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillCauldron(world, pos, player, hand, stack, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY, Items.BUCKET);
        CauldronInteraction FILL_POWDER_SNOW = (state, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillCauldron(world, pos, player, hand, stack, ObjectRegistry.WOODEN_POWDER_SNOW_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW, Items.BUCKET);
        CauldronInteraction FILL_WITH_WATER_W = (state, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillCauldron(world, pos, player, hand, stack, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY, ObjectRegistry.WOODEN_BUCKET.get());

        @NotNull
        ItemInteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

        static void bootStrap() {
            registerCauldronBehavior();
            WoodenCauldronBehavior.addDefaultInteractions(EMPTY);
            Map<Item, CauldronInteraction> map = EMPTY.map();
            map.put(Items.POTION, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
                if(itemStack.get(DataComponents.POTION_CONTENTS) != null || !Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS)).is(Potions.WATER)){
                    return ItemInteractionResult.FAIL;
                }

                if (!level.isClientSide) {
                    Item item = itemStack.getItem();
                    player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.ITEM_USED.get(item));
                    level.setBlockAndUpdate(blockPos, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState());
                    level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                }
                return ItemInteractionResult.SUCCESS;
            }));

            WoodenCauldronBehavior.addDefaultInteractions(WATER);

            WATER.map().put(Items.BUCKET, ((blockState, level, blockPos, player, interactionHand, itemStack) -> WoodenCauldronBehavior.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(Items.WATER_BUCKET), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL)));
            WATER.map().put(ObjectRegistry.WOODEN_BUCKET.get(), ((blockState, level, blockPos, player, interactionHand, itemStack) -> WoodenCauldronBehavior.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(ObjectRegistry.WOODEN_WATER_BUCKET.get()), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL)));
            WATER.map().put(Items.GLASS_BOTTLE, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
                if (!level.isClientSide) {
                    Item item = itemStack.getItem();
                    player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
                    player.awardStat(Stats.ITEM_USED.get(item));
                    LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
                    level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                }
                return ItemInteractionResult.SUCCESS;
            }));
            WATER.map().put(Items.POTION, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
                if (itemStack.get(DataComponents.POTION_CONTENTS) != null || !Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS)).is(Potions.WATER)) {
                    return ItemInteractionResult.FAIL;
                }
                if (!level.isClientSide) {
                    Item item = itemStack.getItem();
                    player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.ITEM_USED.get(item));
                    level.setBlockAndUpdate(blockPos, blockState.cycle(LEVEL));
                    level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                }
                return ItemInteractionResult.SUCCESS;
            }));


            WATER.map().put(Items.LEATHER_BOOTS, DYED_ITEM);
            WATER.map().put(Items.LEATHER_LEGGINGS, DYED_ITEM);
            WATER.map().put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
            WATER.map().put(Items.LEATHER_HELMET, DYED_ITEM);
            WATER.map().put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
            WATER.map().put(Items.WHITE_BANNER, BANNER);
            WATER.map().put(Items.GRAY_BANNER, BANNER);
            WATER.map().put(Items.BLACK_BANNER, BANNER);
            WATER.map().put(Items.BLUE_BANNER, BANNER);
            WATER.map().put(Items.BROWN_BANNER, BANNER);
            WATER.map().put(Items.CYAN_BANNER, BANNER);
            WATER.map().put(Items.GREEN_BANNER, BANNER);
            WATER.map().put(Items.LIGHT_BLUE_BANNER, BANNER);
            WATER.map().put(Items.LIGHT_GRAY_BANNER, BANNER);
            WATER.map().put(Items.LIME_BANNER, BANNER);
            WATER.map().put(Items.MAGENTA_BANNER, BANNER);
            WATER.map().put(Items.ORANGE_BANNER, BANNER);
            WATER.map().put(Items.PINK_BANNER, BANNER);
            WATER.map().put(Items.PURPLE_BANNER, BANNER);
            WATER.map().put(Items.RED_BANNER, BANNER);
            WATER.map().put(Items.YELLOW_BANNER, BANNER);
            WATER.map().put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.RED_SHULKER_BOX, SHULKER_BOX);
            WATER.map().put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
            POWDER_SNOW.map().put(Items.BUCKET, (state2, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillBucket(state2, world, pos, player, hand, stack, new ItemStack(Items.POWDER_SNOW_BUCKET), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
            WoodenCauldronBehavior.addDefaultInteractions(POWDER_SNOW);
        }

        static void addDefaultInteractions(CauldronInteraction.InteractionMap behavior) {
            behavior.map().put(Items.WATER_BUCKET, FILL_WATER);
            behavior.map().put(ObjectRegistry.WOODEN_WATER_BUCKET.get(), FILL_WITH_WATER_W);
            behavior.map().put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
        }

        static ItemInteractionResult fillBucket(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
            if (!predicate.test(state)) {
                return ItemInteractionResult.FAIL;
            }
            if (!world.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, output));
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, ObjectRegistry.WOODEN_CAULDRON.get().defaultBlockState());
                world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
                world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }

        static ItemInteractionResult fillCauldron(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, Item returnItem) {
            if (!world.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(returnItem)));
                player.awardStat(Stats.FILL_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, state);
                world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }

        static void registerCauldronBehavior() {
            CauldronInteraction.WATER.map().put(ObjectRegistry.WOODEN_BUCKET.get(), (state2, world, pos, player, hand, stack) -> CauldronInteraction.fillBucket(state2, world, pos, player, hand, stack, new ItemStack(ObjectRegistry.WOODEN_WATER_BUCKET.get()), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL));
            registerBucketBehaviorForNormalCauldron(CauldronInteraction.EMPTY.map());
        }

        static void registerBucketBehaviorForNormalCauldron(Map<Item, CauldronInteraction> behavior) {
            CauldronInteraction fillWithWater = (state, world, pos, player, hand, stack) -> fillCauldron(world, pos, player, hand, stack, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY, ObjectRegistry.WOODEN_BUCKET.get());
            CauldronInteraction fillWithPowderSnow = (state, world, pos, player, hand, stack) -> fillCauldron(world, pos, player, hand, stack, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW, ObjectRegistry.WOODEN_BUCKET.get());
            behavior.put(ObjectRegistry.WOODEN_WATER_BUCKET.get(), fillWithWater);
        }
    }

    public static boolean matchesRecipe(RecipeInput inventory, NonNullList<Ingredient> recipe, int startIndex, int endIndex) {
        List<ItemStack> validStacks = new ArrayList<>();

        for(int i = startIndex; i <= endIndex; ++i) {
            ItemStack stackInSlot = inventory.getItem(i);
            if (!stackInSlot.isEmpty()) {
                validStacks.add(stackInSlot);
            }
        }

        Iterator<Ingredient> var10 = recipe.iterator();

        boolean matches;
        do {
            if (!var10.hasNext()) {
                return true;
            }

            Ingredient entry = (Ingredient)var10.next();
            matches = false;

            for (ItemStack item : validStacks) {
                if (entry.test(item)) {
                    matches = true;
                    validStacks.remove(item);
                    break;
                }
            }
        } while(matches);

        return false;
    }

    public static final class StreamCodecUtil {
        public static <T, B extends FriendlyByteBuf> StreamCodec<B, NonNullList<T>> nonNullList(StreamCodec<B, T> elementCodec, T defaultElement) {
            return StreamCodec.of((buf, value) -> {
                buf.writeVarInt(value.size());

                for (T element : value) {
                    elementCodec.encode(buf, element);
                }
            }, buf -> {
                NonNullList<T> list = NonNullList.withSize(buf.readVarInt(), defaultElement);

                list.replaceAll(element -> elementCodec.decode(buf));

                return list;
            });
        }
    }
}
