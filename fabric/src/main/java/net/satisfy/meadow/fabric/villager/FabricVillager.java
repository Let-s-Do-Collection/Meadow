package net.satisfy.meadow.fabric.villager;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.registry.ObjectRegistry;

@SuppressWarnings("deprecation")
public class FabricVillager {

    private static final ResourceLocation CHEESEMAKER_POI_IDENTIFIER = Meadow.identifier("cheesemaker_poi");
    public static final PoiType CHEESEMAKER_POI = PointOfInterestHelper.register(CHEESEMAKER_POI_IDENTIFIER, 1, 12, ObjectRegistry.CHEESE_FORM.get());
    public static final VillagerProfession CHEESEMAKER = Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, new ResourceLocation("meadow", "cheesemaker"), VillagerProfessionBuilder.create().id(new ResourceLocation("meadow", "cheesemaker")).workstation(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, CHEESEMAKER_POI_IDENTIFIER)).build());

    private static final ResourceLocation HERMIT_POI_IDENTIFIER = Meadow.identifier("hermit_poi");
    public static final PoiType HERMIT_POI = PointOfInterestHelper.register(HERMIT_POI_IDENTIFIER, 1, 12, ObjectRegistry.WOODCUTTER.get());
    public static final VillagerProfession HERMIT = Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, new ResourceLocation("meadow", "hermit"), VillagerProfessionBuilder.create().id(new ResourceLocation("meadow", "hermit")).workstation(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, HERMIT_POI_IDENTIFIER)).build());

    public static final VillagerType MEADOW = Registry.register(BuiltInRegistries.VILLAGER_TYPE, new ResourceLocation("meadow", "meadow"), new VillagerType("meadow"));


    public static void init() {

        TradeOfferHelper.registerVillagerOffers(CHEESEMAKER, 1, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.RENNET.get(), 1, 2, 7));
        });
        TradeOfferHelper.registerVillagerOffers(CHEESEMAKER, 2, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.COOKING_CAULDRON.get(), 7, 1, 10));
        });
        TradeOfferHelper.registerVillagerOffers(CHEESEMAKER, 3, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.STOVE.get(), 6, 1, 10));
        });
        TradeOfferHelper.registerVillagerOffers(CHEESEMAKER, 4, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.WOODEN_BUCKET.get(), 5, 1, 10));
            factories.add(new SellItemFactory(ObjectRegistry.WOODEN_MILK_BUCKET.get(), 12, 1, 10));
        });
        TradeOfferHelper.registerVillagerOffers(CHEESEMAKER, 5, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.CHEESE_BLOCK.get(), 10, 1, 10));
            factories.add(new SellItemFactory(ObjectRegistry.CHEESE_FORM.get(), 4, 1, 10));
            factories.add(new SellItemFactory(ObjectRegistry.AMETHYST_CHEESE_BLOCK.get(), 12, 1, 15));
            factories.add(new SellItemFactory(ObjectRegistry.GRAIN_CHEESE_BLOCK.get(), 12, 1, 15));
            factories.add(new SellItemFactory(ObjectRegistry.WOODEN_WARPED_MILK_BUCKET.get(), 18, 2, 15));

        });


        TradeOfferHelper.registerVillagerOffers(HERMIT, 1, factories -> {
            factories.add(new BuyForOneEmeraldFactory(ObjectRegistry.BENCH.get(), 15, 4, 5));
            factories.add(new BuyForOneEmeraldFactory(ObjectRegistry.PINE_LOG.get(), 10, 4, 5));
            factories.add(new SellItemFactory(ObjectRegistry.PINE_SAPLING.get(), 10, 6, 5));
            factories.add(new SellItemFactory(ObjectRegistry.PINE_SLAB.get(), 7, 4, 5));
        });
        TradeOfferHelper.registerVillagerOffers(HERMIT, 2, factories -> {
            factories.add(new BuyForOneEmeraldFactory(ObjectRegistry.SHELF.get(), 15, 4, 5));
            factories.add(new BuyForOneEmeraldFactory(ObjectRegistry.WOODEN_SHEEP_MILK_BUCKET.get(), 10, 2, 5));
            factories.add(new SellItemFactory(ObjectRegistry.WATERING_CAN_ITEM.get(), 25, 4, 5));
            factories.add(new SellItemFactory(ObjectRegistry.CHEESE_SANDWICH.get(), 4, 4, 5));
        });
        TradeOfferHelper.registerVillagerOffers(HERMIT, 3, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.PINE_PLANKS.get(), 3, 2, 10));
            factories.add(new SellItemFactory(ObjectRegistry.FRAME.get(), 25, 1, 10));
        });
        TradeOfferHelper.registerVillagerOffers(HERMIT, 4, factories -> {
            factories.add(new SellItemFactory(ObjectRegistry.WOODEN_BUCKET.get(), 5, 1, 10));
            factories.add(new SellItemFactory(Items.RABBIT_HIDE, 8, 2, 10));
            factories.add(new SellItemFactory(ObjectRegistry.FIRE_LOG.get(), 6, 2, 10));
        });
        TradeOfferHelper.registerVillagerOffers(HERMIT, 5, factories -> {
            factories.add(new SellItemFactory(Items.IRON_AXE, 10, 1, 10));
            factories.add(new SellItemFactory(ObjectRegistry.WOODEN_CAULDRON.get(), 5, 1, 10));
            factories.add(new SellItemFactory(ObjectRegistry.ALPINE_SALT.get(), 3, 1, 5));

        });

        VillagerType.BY_BIOME.put(ResourceKey.create(Registries.BIOME, new ResourceLocation("meadow")), MEADOW);

    }

    static class BuyForOneEmeraldFactory implements VillagerTrades.ItemListing {
        private final Item buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public BuyForOneEmeraldFactory(ItemLike item, int price, int maxUses, int experience) {
            this.buy = item.asItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05F;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack itemStack = new ItemStack(this.buy, this.price);
            return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellItemFactory implements VillagerTrades.ItemListing {
        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        public SellItemFactory(Block item, int price, int count, int experience) {
            this(new ItemStack(item), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int experience) {
            this(new ItemStack(item), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
            this(stack, price, count, maxUses, experience, 0.05F);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(
                    new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier
            );
        }
    }
}