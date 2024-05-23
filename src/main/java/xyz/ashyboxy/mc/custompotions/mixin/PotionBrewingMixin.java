package xyz.ashyboxy.mc.custompotions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import xyz.ashyboxy.mc.custompotions.PotionBrewing;

@Mixin(net.minecraft.world.item.alchemy.PotionBrewing.class)
public class PotionBrewingMixin {
    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void mix(ItemStack reagent, ItemStack potion, CallbackInfoReturnable<ItemStack> cir) {
        PotionBrewing.mix(reagent, potion, cir);
    }

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void hasMix(ItemStack input, ItemStack reagent, CallbackInfoReturnable<Boolean> cir) {
        PotionBrewing.hasMix(input, reagent, cir);
    }

    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void isIngredient(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        PotionBrewing.isIngredient(input, cir);
    }
}
