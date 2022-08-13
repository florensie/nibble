package be.florens.nibble.mixin;

import be.florens.nibble.extension.FoodDataExtension;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements FoodDataExtension {

    @Shadow private int foodLevel;

    @Shadow private float saturationLevel;

    @Unique
    @Override
    public void eatOnlyNutrition(int nutrition) {
        this.foodLevel = Math.min(nutrition + this.foodLevel, 20);
    }

    @Unique
    @Override
    public void eatOnlySaturation(Item item) {
        if (item.isEdible()) {
            FoodProperties foodProperties = Objects.requireNonNull(item.getFoodProperties(),
                    "Edible item should always have FoodProperties");
            int nutrition = foodProperties.getNutrition();
            float saturationModifier = foodProperties.getSaturationModifier();

            this.saturationLevel = Math.min(this.saturationLevel + (float)nutrition * saturationModifier * 2.0F, (float)this.foodLevel);
        }
    }
}
