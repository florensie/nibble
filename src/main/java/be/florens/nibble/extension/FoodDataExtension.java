package be.florens.nibble.extension;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Unique;

public interface FoodDataExtension {
    default void eatOnlyNutrition(int nutrition) {
        throw new IllegalStateException("Extension not implemented by mixin");
    }

    default void eatOnlySaturation(Item item) {
        throw new IllegalStateException("Extension not implemented by mixin");
    }
}
