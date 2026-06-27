package me.cloudm1nd3.cloudchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.cloudm1nd3.cloudchat.commands.Commands;
import me.cloudm1nd3.cloudchat.configs.Config;
import me.cloudm1nd3.cloudchat.listeners.AsyncChatListener;
import me.cloudm1nd3.cloudchat.listeners.PlayerSessionListener;
import me.cloudm1nd3.cloudchat.managers.ChannelManager;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.managers.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CloudChat extends JavaPlugin {

    public static CloudChat getInstance(){
        return getPlugin(CloudChat.class);
    }

    @Override
    public void onEnable() {
        Config.init(this);
        ChannelManager.init(this);
        ChatPlayerManager.init(this);
        CooldownManager.init();


        Commands chatCommand = new Commands();
        getCommand("chatCommand").setExecutor(chatCommand);


        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners(){
        PluginManager pluginManager = getServer().getPluginManager();

        String stringPriority = Config.getString("chat-event-priority", "HIGHEST");
        getLogger().info("Chat-event-priority is set to " + stringPriority);
        EventPriority chatPriority = EventPriority.valueOf(stringPriority);
        pluginManager.registerEvent(
                AsyncChatEvent.class,
                new AsyncChatListener(this),
                chatPriority,
                (listener, event) -> ((AsyncChatListener)listener).onAsyncChat((AsyncChatEvent) event),
                this,
                false);

        pluginManager.registerEvents(new PlayerSessionListener(), this);
    }
}
