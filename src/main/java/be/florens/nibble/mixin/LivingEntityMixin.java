package be.florens.nibble.mixin;

import be.florens.nibble.NibbleNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique private int appliedNutrition;
    @Unique private float nutritionPerTick;
    @Unique private int useDuration;

    @Shadow public abstract int getUseItemRemainingTicks();

    @Shadow protected ItemStack useItem;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    public void doFoodNibbling(ItemStack itemStack, CallbackInfo ci) {
        //noinspection ConstantConditions
        if (!getLevel().isClientSide() && (Object) this instanceof ServerPlayer player && itemStack.isEdible()) {
            int elapsedUseTicks = this.useDuration - this.getUseItemRemainingTicks() + 1;
            int nutrition = (int) (this.nutritionPerTick * elapsedUseTicks) - this.appliedNutrition;
            if (nutrition > 0) {
                this.appliedNutrition += nutrition;
                itemStack.nibble$nibbleFood(player, nutrition);
                NibbleNetworking.sendNibblePacket(player, nutrition);
            }
        }
    }

    @Inject(method = "updateUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;completeUsingItem()V"))
    private void resetFoodNutritionOnComplete(ItemStack stack, CallbackInfo ci) {
        stack.nibble$resetNutritionRemaining();
    }

    @Inject(method = "startUsingItem", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/entity/LivingEntity;useItem:Lnet/minecraft/world/item/ItemStack;"))
    public void setNutritionPerTick(CallbackInfo ci) {
        this.nutritionPerTick = (float) this.useItem.nibble$getNutritionRemaining() / this.useItem.getUseDuration();
        this.useDuration = this.useItem.getUseDuration();
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    public void resetAppliedNutrition(CallbackInfo ci) {
        this.appliedNutrition = 0;
    }
}
