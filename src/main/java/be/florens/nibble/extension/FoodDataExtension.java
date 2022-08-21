package be.florens.nibble.extension;

import net.minecraft.world.item.Item;

public interface FoodDataExtension {
    default void nibble$eatOnlyNutrition(int nutrition) {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default void nibble$eatOnlySaturation(Item item) {
        throw new IllegalStateException("Extension not implemented by mixin");
    }
}
