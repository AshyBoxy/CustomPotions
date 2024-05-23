package xyz.ashyboxy.mc.custompotions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class PotionRecipe {
    public static final Codec<PotionRecipe> S_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("reagent").forGetter(PotionRecipe::getReagent),
            PotionLike.CODEC.fieldOf("base").forGetter(PotionRecipe::getBase),
            PotionLike.CODEC.fieldOf("result").forGetter(PotionRecipe::getResult),
            ResourceLocation.CODEC.fieldOf("id").forGetter(PotionRecipe::getId))
            .apply(instance, (i, b, r, id) -> PotionRecipe.createFully(i, id, b, r)));

    public static final StreamCodec<ByteBuf, PotionRecipe> STREAM_CODEC = ByteBufCodecs.fromCodec(S_CODEC);

    public static Codec<PotionRecipe> getDCodec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance ->
            instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("reagent").forGetter(PotionRecipe::getReagent),
                    PotionLike.CODEC.fieldOf("base").forGetter(PotionRecipe::getBase),
                    PotionLike.CODEC.fieldOf("result").forGetter(PotionRecipe::getResult))
                    .apply(instance, (i, b, r) -> new PotionRecipe(i, id, b, r))
        );
    }

    private final Item reagent;
    private final ResourceLocation id;
    private final PotionLike base;
    private final PotionLike result;

    public PotionRecipe(ItemLike reagent, ResourceLocation id, PotionLike base, PotionLike result) {
        this.reagent = reagent.asItem();
        this.id = id;
        this.base = base;
        this.result = result;
    }

    public static PotionRecipe createFully(ItemLike reagent, ResourceLocation id, PotionLike base, PotionLike result) {
        return new PotionRecipe(reagent, id, PotionLike.create(base.customPotions$getType(),
                base.customPotions$getLocation()), PotionLike.create(result.customPotions$getType(), result.customPotions$getLocation()));
    }

    public PotionLike getBase() {
        return base;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Item getReagent() {
        return reagent;
    }

    public PotionLike getResult() {
        return result;
    }
}
