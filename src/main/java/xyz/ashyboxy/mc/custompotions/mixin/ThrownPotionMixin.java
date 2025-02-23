package xyz.ashyboxy.mc.custompotions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ashyboxy.mc.custompotions.PotionLike;
import xyz.ashyboxy.mc.custompotions.minterfaces.CustomPotionAreaEffectCloud;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin extends ThrowableItemProjectile {
    public ThrownPotionMixin(EntityType<? extends ThrowableItemProjectile> entityType) {
        super(entityType, null);
        throw new AssertionError();
    }

    @Inject(method = "makeAreaOfEffectCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setPotionContents(Lnet/minecraft/world/item/alchemy/PotionContents;)V"))
    private void customPotions$setAreaEffectCloudCustomPotion(CallbackInfo ci, @Local AreaEffectCloud areaEffectCloud) {
        PotionLike p = PotionLike.fromItemStack(getItem());
        if (p == null || p == PotionLike.EMPTY || p instanceof Potion)
            return;
        ((CustomPotionAreaEffectCloud) areaEffectCloud).customPotions$setCustomPotion(true);
    }
}
