package be.florens.nibble.extension;

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

    default void nibble$shrinkNutritionRemaining(int nutrition) {
        throw new IllegalStateException("Extension not implemented by mixin");
    }
}
