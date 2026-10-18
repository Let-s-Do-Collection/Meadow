package net.satisfy.meadow.client;

import net.satisfy.foundation.tooltip.InfoTooltip;

import static net.satisfy.meadow.core.registry.ObjectRegistry.*;

public final class MeadowTooltips {
    private MeadowTooltips() {
    }

    public static void init() {
        InfoTooltip.init();
        InfoTooltip.of(CAMERA.get()).placeable().register();
        InfoTooltip.of(MILK_CAN.get()).placeable().details(1).register();
        InfoTooltip.of(CHEESE_FORM.get()).placeable().register();
        InfoTooltip.of(COOKING_CAULDRON.get()).placeable().register();
        InfoTooltip.of(COOKING_FRAME.get()).placeable().register();
        InfoTooltip.of(FONDUE_POT.get()).placeable().register();
        InfoTooltip.of(WOODEN_CAULDRON.get()).placeable().register();
        InfoTooltip.of(WOODCUTTER.get()).placeable().register();
        InfoTooltip.of(WHEELBARROW.get()).placeable().register();
        InfoTooltip.of(OIL_LANTERN.get()).placeable().register();
        InfoTooltip.of(FIREWOOD.get()).placeable().register();
        InfoTooltip.of(CLIMBING_ROPE.get()).placeable().details(1).register();
        InfoTooltip.of(DOORMAT.get()).placeable().register();
        InfoTooltip.of(WOODEN_FLOWER_POT_SMALL.get()).placeable().register();
        InfoTooltip.of(WOODEN_FLOWER_POT_BIG.get()).placeable().register();
        InfoTooltip.of(WOODEN_FLOWER_BOX.get()).placeable().register();
        InfoTooltip.of(CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(AMETHYST_CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(BUFFALO_CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(GOAT_CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(GRAIN_CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(SHEEP_CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(WARPED_CHEESE_WHEEL.get()).placeable().register();
        InfoTooltip.of(CHEESECAKE.get()).placeable().register();
        InfoTooltip.of(CHEESE_TART.get()).placeable().register();
        InfoTooltip.of(WATERING_CAN_ITEM.get()).details(1).register();
        InfoTooltip.of(FELTING_NEEDLE.get()).details(1).register();
    }
}
