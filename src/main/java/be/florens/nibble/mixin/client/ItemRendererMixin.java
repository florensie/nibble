package be.florens.nibble.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemRendererMixin {

    @Unique
    private static final int FOOD_BAR_COLOR = Mth.color(147, 73, 5);

    @Shadow protected abstract void fillRect(BufferBuilder bufferBuilder, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("TAIL"))
    private void renderFoodBar(Font font, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (!itemStack.isEmpty() && itemStack.nibble$isFoodBarVisible()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();

            int height = itemStack.nibble$getFoodBarHeight();
            // Background
            this.fillRect(bufferBuilder, x + 2, y + 2, 2, 13, 0, 0, 0, 255);
            // Bar fill
            this.fillRect(bufferBuilder, x + 2, y + 15 - height, 1, height, FOOD_BAR_COLOR >> 16 & 0xFF, FOOD_BAR_COLOR >> 8 & 0xFF, FOOD_BAR_COLOR & 0xFF, 255);

            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
