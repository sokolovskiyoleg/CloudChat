package me.cloudm1nd3.cloudchat.configs;

import me.cloudm1nd3.cloudchat.utilities.ColorService;
import me.cloudm1nd3.cloudchat.utilities.PlaceholderService;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public enum Messages {
    PREFIX("prefix", "&8[&bCloudChat&8]&r "),
    NO_PERMISSION("command.no-permission", "{prefix}&cУ вас нет прав на выполнение этой команды."),
    RELOAD_FAILED("command.reload-failed,", "{prefix}&cОшибка при перезагрузке. Смотрите в консоль."),
    RELOAD_SUCCESS("command.reload-success", "{prefix}&aКонфигурация и языковые файлы перезагружены."),

    CHANNEL_COOLDOWN("channel.cooldown", "{prefix}&cПомедленнее, сэмпай (˶˃ᆺ˂˶). %m%мин. %s%сек."),
    CHANNEL_NO_PERMISSION("channel.no-permission", "{prefix}&cУ вас нет прав говорить в этот канал.");


    private final String path;
    private final Object defaultValue;
    private static FileConfiguration langConfig;
    private static File langFile;
    private static final Map<Messages, String> stringCache = new HashMap<>();
    private static final Map<Messages, List<String>> listCache = new HashMap<>();
    private static JavaPlugin plugin;

    Messages(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }
    Messages(String path, List<String> defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public static void init(JavaPlugin pl, String language) {
        plugin = pl;

        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists() && !langFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create language folder: " + langFolder);
        }

        langFile = new File(langFolder, language + ".yml");

        String resourcePath = "lang/" + language + ".yml";

        if (!langFile.exists()) {
            plugin.saveResource(resourcePath, false);
        }

        reload();
    }

    @SuppressWarnings("unchecked")
    public static void reload() {
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        stringCache.clear();
        listCache.clear();

        for (Messages msg : values()) {
            if(msg.defaultValue instanceof List){
                List<String> listSection = langConfig.getStringList(msg.path);
                if(listSection.isEmpty()){
                    listSection = (List<String>) msg.defaultValue;
                }
                listCache.put(msg, listSection);
            } else {
                String value = langConfig.getString(msg.path, (String) msg.defaultValue);
                stringCache.put(msg, value);
            }
        }

        boolean changed = false;
        for (Messages msg : values()) {
            if (!langConfig.contains(msg.path)) {
                langConfig.set(msg.path, msg.defaultValue);
                changed = true;
            }
        }
        if (changed) {
            save();
        }

    }

    private static void save() {
        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            plugin.getLogger().severe("[CloudChat] Error while saving language file: " + e.getMessage());
        }
    }

    public String get(String... replacements) {
        String message = stringCache.getOrDefault(this, (String) defaultValue);
        return applyPlaceholders(message, replacements);
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String ... replacements){
        List<String> list = listCache.getOrDefault(this, (List<String>) defaultValue);
        if (list == null) return Collections.emptyList();

        return list.stream()
                .map(line -> applyPlaceholders(line, replacements))
                .toList();
    }

    private String applyPlaceholders(String text, String... replacements){
        String prefix = stringCache.getOrDefault(PREFIX, (String) PREFIX.defaultValue);
        text = text.replace("{prefix}", prefix);

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                text = text.replace(replacements[i], replacements[i + 1]);
            }
        }
        return text;
    }

    public void send(CommandSender sender, String... replacements) {
        if (sender == null) return;
        sender.sendMessage(ColorService.processLegacy(get(replacements)));
    }

    public void sendList(CommandSender sender, String... replacements) {
        if (sender == null) return;
        List<String> listSection = getList(replacements);
        for (String s : listSection) {
            sender.sendMessage(ColorService.processLegacy(s));
        }
    }
}
