package xyz.ashyboxy.mc.custompotions.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import xyz.ashyboxy.mc.custompotions.CustomPotion;
import xyz.ashyboxy.mc.custompotions.PotionLike;

@Mixin(Potion.class)
public class PotionMixin implements PotionLike {
    @Override
    public boolean customPotions$same(PotionLike potion) {
        if (!(potion instanceof Potion)) return false;
        return potion.equals(this);
    }

    @Override
    public ResourceLocation customPotions$getType() {
        return CustomPotion.VANILLA_POTION_TYPE;
    }

    @Override
    public ResourceLocation customPotions$getLocation() {
        return BuiltInRegistries.POTION.getKey((Potion) (Object) this);
    }

    @Override
    public ItemStack customPotions$make(ItemStack base) {
        return PotionContents.createItemStack(base.getItem(), BuiltInRegistries.POTION.getHolder(customPotions$getLocation()).orElseThrow());
    }
}
