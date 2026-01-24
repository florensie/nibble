package be.florens.nibble.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {

    @Unique
    private static final int FOOD_BAR_COLOR = ARGB.color(147, 73, 5);

    @Shadow
    public abstract Item getItem();

    @ModifyReturnValue(method = "isBarVisible", at = @At("RETURN"))
    private boolean isBarVisible(boolean original) {
        return original || isFood();
    }

    @ModifyReturnValue(method = "getBarWidth", at = @At("RETURN"))
    private int getBarWidth(int original) {
        if (isNonFoodBarVisible() || !isFood()) {
            return original;
        }

        return 0; // TODO
    }

    @ModifyReturnValue(method = "getBarColor", at = @At("RETURN"))
    private int getBarColor(int original) {
        if (isNonFoodBarVisible() || !isFood()) {
            return original;
        }

        return FOOD_BAR_COLOR;
    }

    /// Returns the original result of [ItemStack#isBarVisible()]
    @Unique
    private boolean isNonFoodBarVisible() {
        return this.getItem().isBarVisible((ItemStack) (Object) this);
    }

    @Unique
    private boolean isFood() {
        return this.has(DataComponents.FOOD);
    }
}
