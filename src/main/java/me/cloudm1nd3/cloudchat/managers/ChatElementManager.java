package me.cloudm1nd3.cloudchat.managers;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.configs.Config;
import me.cloudm1nd3.cloudchat.objects.ChatElement;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatElementManager {
    private static ChatElementManager instance;
    private final CloudChat plugin;

    private final Map<String, ChatElement> chatElements = new HashMap<>();

    private ChatElementManager(CloudChat plugin) {
        this.plugin = plugin;
    }

    public ChatElement getChatElementByName(String value){
        return chatElements.getOrDefault(value, null);
    }

    public static void init(CloudChat plugin) {
        if (instance != null) {
            throw new IllegalStateException("ChatElementManager already initialized");
        }
        instance = new ChatElementManager(plugin);
        instance.reload();
    }

    public static ChatElementManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ChatElementManager not initialized");
        }
        return instance;
    }

    private boolean isSectionEmpty(ConfigurationSection section) {
        return section.getKeys(false).isEmpty();
    }

    public void reload() {
        chatElements.clear();

        ConfigurationSection elementsSection = Config.getOrCreateSection("ChatElements");
        if (!isSectionEmpty(elementsSection)) {
            loadChatElements(elementsSection);
        }
    }

    private void loadChatElements(ConfigurationSection elementSection){
        for (String elementName : elementSection.getKeys(false)) {
            ConfigurationSection elSec = elementSection.getConfigurationSection(elementName);
            if (elSec == null || elSec.getKeys(false).isEmpty()) {
                continue;
            }

            try {
                ChatElement element = parseElement(elementName, elSec);
                chatElements.put(elementName.toLowerCase(), element);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load chatElement '" + elementName + "': " + e.getMessage());
            }
        }
    }

    private ChatElement parseElement(String elementName, ConfigurationSection section){
        String text = section.getString("text", "");
        List<String> hoverLines = section.getStringList("hover");
        ConfigurationSection clickSection = section.getConfigurationSection("click");

        String type;
        String value;
        if(clickSection == null || isSectionEmpty(clickSection)){
            type = null; value = null;
        } else {
            type = clickSection.getString("type");
            value = clickSection.getString("value");
        }

        return new ChatElement(elementName, text, hoverLines, type, value);
    }

}
