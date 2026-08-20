package me.cloudm1nd3.cloudchat.lang;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.configs.Config;
import me.cloudm1nd3.cloudchat.utilities.ColorService;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class Lang {

    private static final String DEFAULT_LOCALE = "en";
    private static final String RESOURCE_PREFIX = "lang/messages_";

    private static CloudChat plugin;
    private static FileConfiguration messages;
    private static FileConfiguration fallbackMessages;

    private Lang() {}

    public static void init(CloudChat pl) {
        plugin = pl;
        reload();
    }

    public static void reload() {
        String locale = Config.getLang();
        messages = load(locale);
        fallbackMessages = locale.equalsIgnoreCase(DEFAULT_LOCALE) ? null : load(DEFAULT_LOCALE);
    }

    private static FileConfiguration load(String locale) {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists() && !langFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create lang folder at " + langFolder.getAbsolutePath());
        }

        String resourcePath = RESOURCE_PREFIX + locale + ".yml";
        File file = new File(langFolder, "messages_" + locale + ".yml");

        if (!file.exists() && plugin.getResource(resourcePath) != null) {
            plugin.saveResource(resourcePath, false);
        }

        FileConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();

        InputStream shipped = plugin.getResource(resourcePath);
        if (shipped != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(shipped, StandardCharsets.UTF_8));
            config.setDefaults(defaults);
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save lang file " + file.getName() + ": " + e.getMessage());
            }
        } else if (!file.exists()) {
            plugin.getLogger().warning("No language file bundled or found on disk for locale '" + locale + "'");
        }

        return config;
    }

    private static String raw(String key) {
        if (messages != null && messages.isSet(key)) {
            return messages.getString(key);
        }
        if (fallbackMessages != null && fallbackMessages.isSet(key)) {
            return fallbackMessages.getString(key);
        }
        return key;
    }

    public static String get(String key, Object... placeholders) {
        String template = raw(key).replace("{prefix}", raw("prefix"));
        return applyPlaceholders(template, placeholders);
    }

    public static Component component(String key, Object... placeholders) {
        return ColorService.processLegacy(get(key, placeholders));
    }

    private static String applyPlaceholders(String template, Object... placeholders) {
        if (placeholders == null || placeholders.length == 0) {
            return template;
        }
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be passed as key/value pairs");
        }
        String result = template;
        for (int i = 0; i < placeholders.length; i += 2) {
            String key = String.valueOf(placeholders[i]);
            String value = String.valueOf(placeholders[i + 1]);
            result = result.replace("{" + key + "}", value);
        }
        return result;
    }
}