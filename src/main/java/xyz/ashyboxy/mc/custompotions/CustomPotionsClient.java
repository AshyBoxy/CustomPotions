package xyz.ashyboxy.mc.custompotions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Collections;
import java.util.Map;

import static xyz.ashyboxy.mc.custompotions.CustomPotionsMod.LOGGER;

public class CustomPotionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // if anyone wants to help with this, that'd be great
        // 2024-10-16: i don't remember where i read this, but iirc something with fabric's
        //      resourcemanager api doesn't let you override vanilla language values, so a
        //      solution could be registering it without using the fabric api?
//        ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(CustomPotionsMod.MOD_ID,
//                "arabic_numerals"), FabricLoader.getInstance().getModContainer(CustomPotionsMod.MOD_ID).orElseThrow(),
//                ResourcePackActivationType.NORMAL);

        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            ClientPlayNetworking.registerReceiver(CustomPotionsMod.CheckPacket.type, ((payload, context) -> {
                context.responseSender().sendPacket(payload);
            }));
        }));

        // TODO: make this actually work with dynamic addons
        ClientPlayNetworking.registerGlobalReceiver(CustomPotionsMod.SyncPotionsPacket.type, (payload, context) -> {
            CustomPotionClientData.reiReloading = true;
            CustomPotionClientData.emiReloading = true;
            CustomPotionClientData.recipesReady = false;
            CustomPotionClientData.CUSTOM_POTIONS = Map.copyOf(payload.potions());
            LOGGER.info("Received {} custom potions from the server", CustomPotionClientData.CUSTOM_POTIONS.size());
            PluginHelper.reloadPotions();
            context.responseSender().sendPacket(new CustomPotionsMod.AckPotionsPacket());
        });
        ClientPlayNetworking.registerGlobalReceiver(CustomPotionsMod.SyncRecipesPacket.type, (payload, context) -> {
            CustomPotionClientData.POTION_RECIPES = Map.copyOf(payload.recipes());
            LOGGER.info("Received {} custom potion recipes from the server", CustomPotionClientData.POTION_RECIPES.size());
            PluginHelper.reloadRecipes();
            CustomPotionClientData.recipesReady = true;
            CustomPotionClientData.reiReloading = false;
            CustomPotionClientData.emiReloading = false;
        });

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            CustomPotionClientData.CUSTOM_POTIONS = Collections.emptyMap();
            CustomPotionClientData.POTION_RECIPES = Collections.emptyMap();
            // these need to be empty if e.g. we exit singleplayer then join a server
            PotionBrewing.CUSTOM_POTIONS = Collections.emptyMap();
            PotionBrewing.POTION_RECIPES = Collections.emptyMap();
        }));
    }
}
