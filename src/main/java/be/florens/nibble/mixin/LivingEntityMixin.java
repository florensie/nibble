package be.florens.nibble.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected int useItemRemaining;

    @Unique private int appliedNutrition;

    @Shadow public abstract int getUseItemRemainingTicks();

    @Shadow protected abstract boolean shouldTriggerItemUseEffects();

    @Shadow protected abstract void triggerItemUseEffects(ItemStack itemStack, int i);

    @Shadow protected abstract void completeUsingItem();

    @Shadow protected abstract void setLivingEntityFlag(int i, boolean bl);

    @Shadow public abstract boolean isUsingItem();

    @Shadow protected ItemStack useItem;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author Florens
     * @reason Testing
     */
    @Overwrite
    public void updateUsingItem(ItemStack itemStack) {
        LivingEntity self = (LivingEntity) (Object) this;
        itemStack.onUseTick(this.level, self, this.getUseItemRemainingTicks());

        // TODO: server/client split?
        if (self instanceof Player player && itemStack.isEdible()) {
            // TODO: is there a better way to write this algorithm?
            // FIXME: useDuration shrinks while eating, causing nutritionPerTick to shrink while eating
            float nutritionPerTick = (float) itemStack.nibble$getNutritionRemaining() / itemStack.getUseDuration();
            int elapsedUseTicks = itemStack.getUseDuration() - this.getUseItemRemainingTicks() + 1;
            int nutrition = (int) (nutritionPerTick * elapsedUseTicks) - appliedNutrition;
            if (nutrition > 0) {
                player.getFoodData().nibble$eatOnlyNutrition(nutrition);
                appliedNutrition += nutrition;
                itemStack.nibble$shrinkNutritionRemaining(nutrition);
            }
        }

        if (this.shouldTriggerItemUseEffects()) {
            this.triggerItemUseEffects(itemStack, 5);
        }

        if (--this.useItemRemaining == 0 && !this.level.isClientSide && !itemStack.useOnRelease()) {
            this.completeUsingItem();
            itemStack.nibble$resetNutritionRemaining();
        }
    }

    /**
     * @author Florens
     * @reason Testing
     */
    @Overwrite
    public void stopUsingItem() {
        if (!this.level.isClientSide) {
            boolean bl = this.isUsingItem();
            this.setLivingEntityFlag(1, false);
            if (bl) {
                this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }
        }

        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = 0;
        this.appliedNutrition = 0;
    }
}
