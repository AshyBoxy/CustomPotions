package xyz.ashyboxy.mc.custompotions;

import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.Commands;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomPotionsMod implements ModInitializer {
    public static final String MOD_ID = "custompotions";
    public static final Logger LOGGER = LoggerFactory.getLogger("CustomPotions");

    public static final ResourceLocation SYNC_POTIONS = new ResourceLocation(MOD_ID, "sync_potions");
    public static final ResourceLocation SYNC_RECIPES = new ResourceLocation(MOD_ID, "sync_recipes");
    public static final ResourceLocation CHECK = new ResourceLocation(MOD_ID, "check");
    public static final ResourceLocation ACK_POTIONS = new ResourceLocation(MOD_ID, "ack_potions");

    public static final ArrayList<UUID> moddedPlayers = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Hewwo Fabric-niichan~");

        Config.load();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PotionRecipeResourceReloadListener());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CustomPotionResourceReloadListener());

        CustomPotion.initialize();

        if (Config.debug) {
            TestPotionLike.initialize();
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                dispatcher.register(Commands.literal("custompotions").then(Commands.literal("dumpRecipe").executes(e -> {
                    PotionRecipe r = PotionBrewing.POTION_RECIPES.values().stream().findFirst().orElseThrow();
                    e.getSource().sendSuccess(() -> Component.literal(PotionRecipe.S_CODEC.encodeStart(JsonOps.INSTANCE, r).getOrThrow().toString()), true);
                    return 0;
                })).then(Commands.literal("dumpPotion").executes(e -> {
                    CustomPotion p = PotionBrewing.CUSTOM_POTIONS.values().stream().findFirst().orElseThrow();
                    e.getSource().sendSuccess(() -> Component.literal(CustomPotion.S_CODEC.encodeStart(JsonOps.INSTANCE, p).getOrThrow().toString()), true);
                    return 0;
                })));
            });
        }

        // please excuse me, i have no clue about networking
        PayloadTypeRegistry.playS2C().register(CheckPacket.type, CheckPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(CheckPacket.type, CheckPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPotionsPacket.type, SyncPotionsPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncRecipesPacket.type, SyncRecipesPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AckPotionsPacket.type, AckPotionsPacket.CODEC);

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            ServerPlayNetworking.registerReceiver(handler, CheckPacket.type, (payload, context) -> {
                ServerPlayer player = context.player();
                moddedPlayers.add(player.getUUID());
                LOGGER.info("{} has the custom potions mod", player.getName().getString());
                context.responseSender().sendPacket(new SyncPotionsPacket(PotionBrewing.CUSTOM_POTIONS));
            });
            sender.sendPacket(new CheckPacket());
        }));

        ServerPlayNetworking.registerGlobalReceiver(AckPotionsPacket.type, (payload, context) -> {
            context.responseSender().sendPacket(new SyncRecipesPacket(PotionBrewing.POTION_RECIPES));
        });

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            moddedPlayers.remove(handler.player.getUUID());
        }));

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(((player, joined) -> {
            if (!moddedPlayers.contains(player.getUUID())) return;
            player.connection.send(ServerPlayNetworking.createS2CPacket(new SyncPotionsPacket(PotionBrewing.CUSTOM_POTIONS)));
        }));
    }

    public record CheckPacket() implements CustomPacketPayload {
        public static final Type<CheckPacket> type = new Type<>(CHECK);
        public static final StreamCodec<RegistryFriendlyByteBuf, CheckPacket> CODEC = CustomPacketPayload.codec(CheckPacket::write, CheckPacket::new);

        public CheckPacket(RegistryFriendlyByteBuf buf) {
            this();
        }

        public void write(RegistryFriendlyByteBuf buf) {}

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }

    // custom potions HAVE to be processed before recipes, otherwise the recipes fail due to not being able to find
    // their constituent potions
    public record SyncPotionsPacket(Map<ResourceLocation, CustomPotion> potions) implements CustomPacketPayload {
        public static final Type<SyncPotionsPacket> type = new Type<>(SYNC_POTIONS);
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncPotionsPacket> CODEC =
                CustomPacketPayload.codec(SyncPotionsPacket::write, SyncPotionsPacket::new);

        public static final StreamCodec<ByteBuf, Map<ResourceLocation, CustomPotion>> POTION_MAP_CODEC =
                ByteBufCodecs.map((m) -> new HashMap<>(), ResourceLocation.STREAM_CODEC, CustomPotion.STREAM_CODEC);

        public SyncPotionsPacket(RegistryFriendlyByteBuf buf) {
            this(POTION_MAP_CODEC.decode(buf));
        }

        private void write(RegistryFriendlyByteBuf buf) {
            POTION_MAP_CODEC.encode(buf, this.potions);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }
    public record SyncRecipesPacket(Map<ResourceLocation, PotionRecipe> recipes) implements CustomPacketPayload {
        public static final Type<SyncRecipesPacket> type = new Type<>(SYNC_RECIPES);
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncRecipesPacket> CODEC =
                CustomPacketPayload.codec(SyncRecipesPacket::write, SyncRecipesPacket::new);

        public static final StreamCodec<ByteBuf, Map<ResourceLocation, PotionRecipe>> RECIPE_MAP_CODEC =
                ByteBufCodecs.map((m) -> new HashMap<>(), ResourceLocation.STREAM_CODEC, PotionRecipe.STREAM_CODEC);

        public SyncRecipesPacket(RegistryFriendlyByteBuf buf) {
            this(RECIPE_MAP_CODEC.decode(buf));
        }

        private void write(RegistryFriendlyByteBuf buf) {
            RECIPE_MAP_CODEC.encode(buf, this.recipes);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }

    public record AckPotionsPacket() implements CustomPacketPayload {
        public static final Type<AckPotionsPacket> type = new Type<>(ACK_POTIONS);
        public static final StreamCodec<RegistryFriendlyByteBuf, AckPotionsPacket> CODEC = CustomPacketPayload.codec(AckPotionsPacket::write, AckPotionsPacket::new);

        public AckPotionsPacket(RegistryFriendlyByteBuf buf) {
            this();
        }

        public void write(RegistryFriendlyByteBuf buf) {}

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return type;
        }
    }
}
