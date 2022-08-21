package be.florens.nibble.mixin;

import be.florens.nibble.extension.ItemStackExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackExtension {

    @Unique private int nutritionRemaining;

    @Shadow public abstract boolean isEdible();
    @Shadow public abstract Item getItem();

    @Unique
    @Override
    public int nibble$getNutritionRemaining() {
        return this.isEdible() ? 0 : nutritionRemaining;
    }

    @Unique
    @Override
    public boolean nibble$isFoodBarVisible() {
        return this.isEdible() && nutritionRemaining < Objects.requireNonNull(this.getItem().getFoodProperties()).getNutrition();
    }

    @Unique
    @Override
    public int nibble$getFoodBarHeight() {
        if (!this.isEdible()) {
            return 13;
        }

        FoodProperties foodProperties = Objects.requireNonNull(this.getItem().getFoodProperties());
        return nutritionRemaining / foodProperties.getNutrition() * 13;
    }

    @Unique
    @Override
    public void nibble$resetNutritionRemaining() {
        if (this.isEdible()) {
            FoodProperties foodProperties = Objects.requireNonNull(this.getItem().getFoodProperties());
            this.nutritionRemaining = foodProperties.getNutrition();
        }
    }

    @Unique
    @Override
    public void nibble$shrinkNutritionRemaining(int nutrition) {
        if (this.isEdible()) {
            this.nutritionRemaining -= nutrition;
        }
    }

    @ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
    private int adjustUseDurationForNibbled(int useDuration) {
        if (this.isEdible()) {
            FoodProperties foodProperties = Objects.requireNonNull(this.getItem().getFoodProperties());

            if (nutritionRemaining != 0) {
                useDuration /= (double) nutritionRemaining / foodProperties.getNutrition();
            } else {
                throw new RuntimeException("NutritionRemaining was never (re)set!");
            }
        }

        return useDuration;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;I)V", at = @At("RETURN"))
    private void initNutritionRemaining(ItemLike itemLike, int i, CallbackInfo ci) {
        if (this.isEdible()) {
            FoodProperties foodProperties = Objects.requireNonNull(this.getItem().getFoodProperties());
            this.nutritionRemaining = foodProperties.getNutrition();
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void initNutritionRemaining(CompoundTag compoundTag, CallbackInfo ci) {
        if (this.isEdible()) {
            this.nutritionRemaining = compoundTag.getInt("NutritionRemaining");
        }
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void saveNutritionRemaining(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        compoundTag.putInt("NutritionRemaining", nutritionRemaining);
    }
}
