package net.satisfy.meadow.registry;

import de.cristelknight.doapi.DoApiCommonEP;
import de.cristelknight.doapi.terraform.boat.TerraformBoatType;
import de.cristelknight.doapi.terraform.boat.item.TerraformBoatItemHelper;
import de.cristelknight.doapi.terraform.sign.TerraformSignHelper;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.satisfy.meadow.Meadow;

@SuppressWarnings("unused")
public class BoatsAndSignsRegistry {
    public static final ResourceLocation PINE_BOAT_TYPE = Meadow.identifier("pine");
    public static final ResourceLocation PINE_SIGN_TEXTURE = Meadow.identifier("entity/signs/pine");
    public static final ResourceLocation PINE_HANGING_SIGN_TEXTURE = Meadow.identifier("entity/signs/hanging/pine");
    public static final ResourceLocation PINE_HANGING_SIGN_GUI_TEXTURE = Meadow.identifier("textures/gui/hanging_signs/pine");

    public static final RegistrySupplier<Block> PINE_SIGN = registerPineSign();
    public static final RegistrySupplier<Block> PINE_WALL_SIGN = registerPineWallSign();
    public static final RegistrySupplier<Item> PINE_SIGN_ITEM = registerPineSignItem();
    public static final RegistrySupplier<Block> PINE_HANGING_SIGN = registerPineHangingSign();
    public static final RegistrySupplier<Block> PINE_WALL_HANGING_SIGN = registerPineWallHangingSign();
    public static final RegistrySupplier<Item> PINE_HANGING_SIGN_ITEM = registerPineHangingSignItem();
    public static final RegistrySupplier<Item> PINE_BOAT = registerPineBoat(false);
    public static final RegistrySupplier<Item> PINE_CHEST_BOAT = registerPineBoat(true);

    public static void init() {
        DoApiCommonEP.registerBoatType(PINE_BOAT_TYPE, new TerraformBoatType.Builder().item(PINE_BOAT).chestItem(PINE_CHEST_BOAT).build());
    }

    private static RegistrySupplier<Block> registerPineSign() {
        return ObjectRegistry.registerWithoutItem("pine_sign", () -> TerraformSignHelper.getSign(PINE_SIGN_TEXTURE));
    }

    private static RegistrySupplier<Block> registerPineWallSign() {
        return ObjectRegistry.registerWithoutItem("pine_wall_sign", () -> TerraformSignHelper.getWallSign(PINE_SIGN_TEXTURE));
    }

    private static RegistrySupplier<Item> registerPineSignItem() {
        return ObjectRegistry.registerItem("pine_sign", () -> new SignItem(ObjectRegistry.getSettings().stacksTo(16), PINE_SIGN.get(), PINE_WALL_SIGN.get()));
    }

    private static RegistrySupplier<Block> registerPineHangingSign() {
        return ObjectRegistry.registerWithoutItem("pine_hanging_sign", () -> TerraformSignHelper.getHangingSign(PINE_HANGING_SIGN_TEXTURE, PINE_HANGING_SIGN_GUI_TEXTURE));
    }

    private static RegistrySupplier<Block> registerPineWallHangingSign() {
        return ObjectRegistry.registerWithoutItem("pine_wall_hanging_sign", () -> TerraformSignHelper.getWallHangingSign(PINE_HANGING_SIGN_TEXTURE, PINE_HANGING_SIGN_GUI_TEXTURE));
    }

    private static RegistrySupplier<Item> registerPineHangingSignItem() {
        return ObjectRegistry.registerItem("pine_hanging_sign", () -> new HangingSignItem(PINE_HANGING_SIGN.get(), PINE_WALL_HANGING_SIGN.get(), ObjectRegistry.getSettings().stacksTo(16)));
    }

    private static RegistrySupplier<Item> registerPineBoat(boolean withChest) {
        return TerraformBoatItemHelper.registerBoatItem(ObjectRegistry.ITEMS, withChest ? "pine_chest_boat" : "pine_boat", PINE_BOAT_TYPE, withChest);
    }
}
