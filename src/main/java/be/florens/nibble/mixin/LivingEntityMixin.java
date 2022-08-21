package be.florens.nibble.mixin;

import be.florens.nibble.NibbleNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique private int appliedNutrition;

    @Shadow public abstract int getUseItemRemainingTicks();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    public void doFoodNibbling(ItemStack itemStack, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        itemStack.onUseTick(this.level, self, this.getUseItemRemainingTicks());

        if (!getLevel().isClientSide() && self instanceof ServerPlayer player && itemStack.isEdible()) {
            // TODO: is there a better way to write this algorithm?
            // FIXME: useDuration shrinks while eating, causing nutritionPerTick to shrink while eating
            float nutritionPerTick = (float) itemStack.nibble$getNutritionRemaining() / itemStack.getUseDuration();
            int elapsedUseTicks = itemStack.getUseDuration() - this.getUseItemRemainingTicks() + 1;
            int nutrition = (int) (nutritionPerTick * elapsedUseTicks) - appliedNutrition;
            if (nutrition > 0) {
                player.getFoodData().nibble$eatOnlyNutrition(nutrition);
                appliedNutrition += nutrition;
                itemStack.nibble$shrinkNutritionRemaining(nutrition);

                FriendlyByteBuf byteBuf = PacketByteBufs.create();
                byteBuf.writeInt(nutrition); // nutrition
                byteBuf.writeInt(0); // saturation
                NibbleNetworking.sendNibblePacket(player, nutrition);
            }
        }
    }

    @Inject(method = "updateUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;completeUsingItem()V"))
    private void resetFoodNutritionOnComplete(ItemStack stack, CallbackInfo ci) {
        stack.nibble$resetNutritionRemaining();
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    public void resetAppliedNutrition(CallbackInfo ci) {
        this.appliedNutrition = 0;
    }
}
