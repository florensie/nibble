package be.florens.nibble.extension;

import net.minecraft.world.entity.player.Player;

public interface ItemStackExtension {

    default int nibble$getNutritionRemaining() {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default boolean nibble$isFoodBarVisible() {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default int nibble$getFoodBarHeight() {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default void nibble$resetNutritionRemaining() {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default void nibble$nibbleFood(Player player, int nutrition) {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default int nibble$getOriginalUseDuration() {
        throw new IllegalStateException("Extension not implemented by mixin");
    }
}
