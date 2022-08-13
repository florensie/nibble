package be.florens.nibble.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract void awardStat(Stat<?> stat);

    @Shadow public abstract FoodData getFoodData();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author Florens
     * @reason Testing
     */
    @Overwrite
    public ItemStack eat(Level level, ItemStack itemStack) {
        this.getFoodData().eatOnlySaturation(itemStack.getItem());
        this.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);

        //noinspection ConstantConditions
        if ((Object) this instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, itemStack);
        }

        return super.eat(level, itemStack);
    }
}
