package net.satisfy.meadow.forge.core.event;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.util.VillagerUtil;
import net.satisfy.meadow.forge.core.registry.MeadowForgeVillagers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeadowForgeEventhandler {

    @Mod.EventBusSubscriber(modid = Meadow.MOD_ID)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void addCustomTrades(VillagerTradesEvent event) {
            if (event.getType().equals(MeadowForgeVillagers.CHEESEMAKER.get())) {
                Map<Integer, List<VillagerTrades.ItemListing>> trades = new HashMap<>(event.getTrades());

                trades.computeIfAbsent(1, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.RENNET.get(), 1, 2, 7));

                trades.computeIfAbsent(2, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.COOKING_CAULDRON.get(), 7, 1, 10));

                trades.computeIfAbsent(3, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.STOVE.get(), 6, 1, 10));

                trades.computeIfAbsent(4, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.WOODEN_BUCKET.get(), 5, 1, 10));
                trades.get(4).add(new VillagerUtil.SellItemFactory(ObjectRegistry.WOODEN_MILK_BUCKET.get(), 12, 1, 10));

                trades.computeIfAbsent(5, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.CHEESE_BLOCK.get(), 10, 1, 10));
                trades.get(5).add(new VillagerUtil.SellItemFactory(ObjectRegistry.CHEESE_FORM.get(), 4, 1, 10));
                trades.get(5).add(new VillagerUtil.SellItemFactory(ObjectRegistry.AMETHYST_CHEESE_BLOCK.get(), 12, 1, 15));
                trades.get(5).add(new VillagerUtil.SellItemFactory(ObjectRegistry.GRAIN_CHEESE_BLOCK.get(), 12, 1, 15));
                trades.get(5).add(new VillagerUtil.SellItemFactory(ObjectRegistry.WOODEN_WARPED_MILK_BUCKET.get(), 18, 2, 15));

                event.getTrades().clear();
                event.getTrades().putAll(trades);
            } else if (event.getType().equals(MeadowForgeVillagers.HERMIT.get())) {
                Map<Integer, List<VillagerTrades.ItemListing>> trades = new HashMap<>(event.getTrades());

                trades.computeIfAbsent(1, k -> new java.util.ArrayList<>()).add(new VillagerUtil.BuyForOneEmeraldFactory(ObjectRegistry.BENCH.get(), 15, 4, 5));
                trades.get(1).add(new VillagerUtil.BuyForOneEmeraldFactory(ObjectRegistry.PINE_LOG.get(), 10, 4, 5));
                trades.get(1).add(new VillagerUtil.SellItemFactory(ObjectRegistry.PINE_SAPLING.get(), 10, 6, 5));
                trades.get(1).add(new VillagerUtil.SellItemFactory(ObjectRegistry.PINE_SLAB.get(), 7, 4, 5));

                trades.computeIfAbsent(2, k -> new java.util.ArrayList<>()).add(new VillagerUtil.BuyForOneEmeraldFactory(ObjectRegistry.SHELF.get(), 15, 4, 5));
                trades.get(2).add(new VillagerUtil.BuyForOneEmeraldFactory(ObjectRegistry.WOODEN_SHEEP_MILK_BUCKET.get(), 10, 2, 5));
                trades.get(2).add(new VillagerUtil.SellItemFactory(ObjectRegistry.WATERING_CAN_ITEM.get(), 25, 4, 5));
                trades.get(2).add(new VillagerUtil.SellItemFactory(ObjectRegistry.CHEESE_SANDWICH.get(), 4, 4, 5));

                trades.computeIfAbsent(3, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.PINE_PLANKS.get(), 3, 2, 10));
                trades.get(3).add(new VillagerUtil.SellItemFactory(ObjectRegistry.FRAME.get(), 25, 1, 10));

                trades.computeIfAbsent(4, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(ObjectRegistry.WOODEN_BUCKET.get(), 5, 1, 10));
                trades.get(4).add(new VillagerUtil.SellItemFactory(Items.RABBIT_HIDE, 8, 2, 10));
                trades.get(4).add(new VillagerUtil.SellItemFactory(ObjectRegistry.FIRE_LOG.get(), 6, 2, 10));

                trades.computeIfAbsent(5, k -> new java.util.ArrayList<>()).add(new VillagerUtil.SellItemFactory(Items.IRON_AXE, 10, 1, 10));
                trades.get(5).add(new VillagerUtil.SellItemFactory(ObjectRegistry.WOODEN_CAULDRON.get(), 5, 1, 10));
                trades.get(5).add(new VillagerUtil.SellItemFactory(ObjectRegistry.ALPINE_SALT.get(), 3, 1, 5));

                event.getTrades().clear();
                event.getTrades().putAll(trades);
            }
        }
    }
}
