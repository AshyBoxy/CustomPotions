package xyz.ashyboxy.mc.custompotions;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static xyz.ashyboxy.mc.custompotions.CustomPotionsMod.LOGGER;

public class Config {
    public static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("custompotions.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static void load() {
        if (!Files.isRegularFile(configPath)) {
            save();
            return;
        }
        JsonElement j;
        try {
            j = JsonParser.parseString(Files.readString(configPath));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            return;
        }
        if (!j.isJsonObject()) return;
        JsonObject config = j.getAsJsonObject();

        debug = GsonHelper.getAsBoolean(config, "debug", debug);
    }

    public static void save() {
        JsonObject j = new JsonObject();

        j.addProperty("debug", debug);

        try {
            Files.writeString(configPath, gson.toJson(j));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }
}
