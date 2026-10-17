package net.satisfy.meadow.core.registry;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.satisfy.foundation.armor.ArmorSet;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.platform.PlatformHelper;

public final class ArmorSetRegistry {
    private static final ResourceLocation FUR_KNOCKBACK_RESISTANCE = Meadow.identifier("fur_set_knockback_resistance");
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
                .bonusActive(entity -> PlatformHelper.isFurSetBonusEnabled() && fur.isComplete(entity))
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
        double amount = fur.isBonusActive(player) ? PlatformHelper.getFurSetKnockbackResistance() : 0.0;
        AttributeModifier applied = attribute.getModifier(FUR_KNOCKBACK_RESISTANCE);
        if (applied != null && applied.amount() == amount) {
            return;
        }
        if (applied != null) {
            attribute.removeModifier(FUR_KNOCKBACK_RESISTANCE);
        }
        if (amount > 0.0) {
            attribute.addTransientModifier(new AttributeModifier(FUR_KNOCKBACK_RESISTANCE, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
