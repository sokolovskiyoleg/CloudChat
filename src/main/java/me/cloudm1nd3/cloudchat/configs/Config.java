package me.cloudm1nd3.cloudchat.configs;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private static CloudChat plugin;
    private static FileConfiguration config;

    public static void init(CloudChat pl){
        plugin = pl;
        plugin.saveDefaultConfig();
        reload();

    }

    public static void reload(){
        plugin.reloadConfig();
        config = plugin.getConfig();

        boolean changed = addMissingDefaults();

        if (changed) {
            save();
            plugin.reloadConfig();
            config = plugin.getConfig();
        }
    }

    public static ConfigurationSection getOrCreateSection(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }
        return section;
    }

    public static ConfigurationSection getSection(String path){
        return config.getConfigurationSection(path);
    }

    private static boolean addMissingDefaults() {
        boolean changed = false;

        if (!config.isSet("lang")) {
            config.set("lang", "ru");
            changed = true;
        }
        if(!config.isSet("chat-event-priority")){
            config.set("chat-event-priority", "HIGHEST");
            changed = true;
        }

        return changed;
    }

    public static String getLang(){
        return config.getString("lang", "ru");
    }

    public static String getString(String path, String defValue){
        if (!config.isSet(path)) {
            config.set(path, defValue);
            return defValue;
        }
        return config.getString(path);
    }

    public static void setValue(String path, Object value) {
        config.set(path, value);
    }

    public static void save(){
        plugin.saveConfig();
    }
}
