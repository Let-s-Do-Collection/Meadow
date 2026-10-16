package net.satisfy.meadow.core.registry;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.satisfy.foundation.armor.ArmorSet;
import net.satisfy.meadow.Meadow;

public final class ArmorSetRegistry {
    private static final AttributeModifier FUR_KNOCKBACK_RESISTANCE = new AttributeModifier(Meadow.identifier("fur_set_knockback_resistance"), 0.2, AttributeModifier.Operation.ADD_VALUE);
    private static ArmorSet fur;

    private ArmorSetRegistry() {
    }

    public static void init() {
        fur = ArmorSet.builder("tooltip.meadow.armor.fur_set")
                .piece(ObjectRegistry.FUR_HELMET)
                .piece(ObjectRegistry.FUR_CHESTPLATE)
                .piece(ObjectRegistry.FUR_LEGGINGS)
                .piece(ObjectRegistry.FUR_BOOTS)
                .bonus("tooltip.meadow.armor.fur_set.bonus")
                .register();
        TickEvent.PLAYER_POST.register(ArmorSetRegistry::applyFurBonus);
    }

    private static void applyFurBonus(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        AttributeInstance attribute = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attribute == null) {
            return;
        }
        boolean complete = fur.isComplete(player);
        boolean applied = attribute.hasModifier(FUR_KNOCKBACK_RESISTANCE.id());
        if (complete && !applied) {
            attribute.addTransientModifier(FUR_KNOCKBACK_RESISTANCE);
        } else if (!complete && applied) {
            attribute.removeModifier(FUR_KNOCKBACK_RESISTANCE.id());
        }
    }
}
