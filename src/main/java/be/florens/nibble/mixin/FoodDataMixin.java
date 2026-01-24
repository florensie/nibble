package be.florens.nibble.mixin;

import be.florens.nibble.extension.FoodDataExtension;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements FoodDataExtension {

    @Shadow private int foodLevel;

    @Shadow private float saturationLevel;

    @Unique
    @Override
    public void nibble$eatOnlyNutrition(int nutrition) {
        this.foodLevel = Math.min(this.foodLevel + nutrition, 20);
    }

//    @Unique
//    @Override
//    public void nibble$eatOnlySaturation(ItemStack itemStack) {
//        FoodProperties food = itemStack.get(DataComponents.FOOD);
//        if (food != null) {
//            float saturation = food.saturation();
//            this.saturationLevel = Math.min(this.saturationLevel + saturation, (float)this.foodLevel);
//        }
//    }
}
