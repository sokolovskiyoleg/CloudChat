package me.cloudm1nd3.cloudchat.managers;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.configs.Config;
import me.cloudm1nd3.cloudchat.objects.ChatChannel;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ChannelManager {
    private static ChannelManager instance;
    private final CloudChat plugin;

    private String defaultChannelName;
    private final Map<String, ChatChannel> channels = new LinkedHashMap<>();


    private ChannelManager(CloudChat plugin) {
        this.plugin = plugin;
    }

    public static void init(CloudChat plugin) {
        if (instance != null) {
            throw new IllegalStateException("ChannelManager already initialized");
        }
        instance = new ChannelManager(plugin);
        instance.reload();
    }

    public static ChannelManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ChannelManager not initialized");
        }
        return instance;
    }

    public void reload() {
        channels.clear();

        ConfigurationSection channelsSection = Config.getOrCreateSection("Channels");
        if (isSectionEmpty(channelsSection)) {
            createDefaultChannelInConfig();
            channelsSection = Config.getSection("Channels");
        }

        loadChannels(channelsSection);
        determineDefaultChannel();

        Config.save();
    }

    private boolean isSectionEmpty(ConfigurationSection section) {
        return section == null || section.getKeys(false).isEmpty();
    }

    private void createDefaultChannelInConfig() {
        plugin.getLogger().info("Channels section was missing or empty. Creating default channel for you <3");

        ConfigurationSection channelsSection = Config.getOrCreateSection("Channels");
        ConfigurationSection defaultSection = channelsSection.createSection("Default");

        defaultSection.set("speak-permission", "");
        defaultSection.set("message-sound", "NONE");
        defaultSection.set("cooldown", 3);
        defaultSection.set("cooldown-bypass-permission", "cloudchat.cooldown.bypass.default");
        defaultSection.set("format", "{player_name}: {message}");
        defaultSection.set("radius", 0);
        defaultSection.set("quickSymbol", "");

        plugin.getLogger().info("Default channel 'Default' has been created.");
    }

    private void loadChannels(ConfigurationSection channelsSection) {
        for (String channelName : channelsSection.getKeys(false)) {
            ConfigurationSection chSec = channelsSection.getConfigurationSection(channelName);
            if (chSec == null || chSec.getKeys(false).isEmpty()) {
                continue;
            }

            try {
                ChatChannel channel = parseChannel(channelName, chSec);
                channels.put(channelName.toLowerCase(), channel);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load channel '" + channelName + "': " + e.getMessage());
            }
        }
    }

    private void determineDefaultChannel() {
        String configuredDefault = Config.getString("defaultChannel", "Default").toLowerCase();

        if (channels.containsKey(configuredDefault)) {
            defaultChannelName = configuredDefault;
            return;
        }

        defaultChannelName = channels.keySet().iterator().next();
        Config.setValue("defaultChannel", defaultChannelName);
        plugin.getLogger().info("Default channel was invalid or missing. Set to first available channel: " + defaultChannelName);
    }

    public String getDefaultChannelName() {
        return defaultChannelName;
    }

    public ChatChannel getChatChannelByMessage(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        for (ChatChannel channel : channels.values()) {
            if(channel.getName().equalsIgnoreCase(defaultChannelName)) {
                continue;
            }
            if (message.startsWith(channel.getQuickSymbol())){
                return channel;
            }
        }
        return getChatChannelByName(defaultChannelName);
    }

    public ChatChannel getChatChannelByName(String channelName){
        return channels.get(channelName);
    }

    public Collection<ChatChannel> getAllChannels() {
        return new ArrayList<>(channels.values());
    }

    private ChatChannel parseChannel(String name, ConfigurationSection section) {
        String speakPerm = section.getString("speak-permission", "");
        String soundStr = section.getString("message-sound", "NONE");
        Sound sound = parseSound(soundStr);
        int cooldown = section.getInt("cooldown", 3);
        String bypassPerm = section.getString("cooldown-bypass-permission",
                "cloudchat.cooldown.bypass." + name.toLowerCase(Locale.ROOT));
        String format = section.getString("format", "{player_name}: {message}");
        int radius = section.getInt("radius", 0);
        String quickSymbol = section.getString("quickSymbol", "");

        return new ChatChannel(name, speakPerm, sound, cooldown, bypassPerm, format, radius, quickSymbol);
    }

    private Sound parseSound(String str) {
        if (str == null || str.equalsIgnoreCase("NONE") || str.trim().isEmpty()) {
            return null;
        }

        String soundName = str.trim().toLowerCase(Locale.ROOT);
        if (!soundName.contains(":")) {
            soundName = "minecraft:" + soundName;
        }

        try {
            NamespacedKey key = NamespacedKey.fromString(soundName);
            return key != null ? Registry.SOUNDS.get(key) : null;
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid sound '" + str + "': " + e.getMessage());
            return null;
        }
    }
}
